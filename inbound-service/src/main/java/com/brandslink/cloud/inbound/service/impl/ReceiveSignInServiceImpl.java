package com.brandslink.cloud.inbound.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.controller.ReceiveSignInController;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveSignIn;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveSignInMapper;
import com.brandslink.cloud.inbound.rabbitmq.InboundStatisticsSender;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.remote.RemoteLogisticsService;
import com.brandslink.cloud.inbound.service.ReceiveSignInService;
import com.brandslink.cloud.inbound.utils.MUtils;
import com.github.pagehelper.PageInfo;

@Service
public class ReceiveSignInServiceImpl implements ReceiveSignInService{
	@Resource
	private ReceiveSignInMapper receiveSignInMapper;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private RemoteLogisticsService remoteLogisticsService;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private InboundStatisticsSender inboundStatisticsSender;
	@Resource
	private MUtils mUtils;
	
	private final Logger logger = LoggerFactory.getLogger(ReceiveSignInController.class);
	
	/**
	 * 添加签到信息
	 * @param receiveSignIn
	 * @return
	 */
	@Override
	public ReceiveSignIn insertReceiveSignIn(ReceiveSignIn receiveSignIn) {
		if(StringUtils.isBlank(receiveSignIn.getWaybillId())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号不能为空");
		}
		if(StringUtils.isBlank(receiveSignIn.getWarehouse())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"仓库编码不能为空");
		}
		if(StringUtils.isBlank(receiveSignIn.getWaybillType())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运输方式不能为空");
		}
		if(StringUtils.isBlank(receiveSignIn.getReceiveGoodNumber())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"收货台信息不能为空");
		}
		if(receiveSignIn.getWaybillId() != null){
			receiveSignIn.setWaybillId(receiveSignIn.getWaybillId().toUpperCase());
		}
		//校验运单号是否存在
		ReceiveArrivalNotice arrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(receiveSignIn.getWaybillId());
		if(arrivalNotice == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统无此运单号所属的来源单号信息,请检查!");
		}
		if(receiveGoodDetailsMapper.checkWayBillIdAmount(receiveSignIn.getWaybillId())<=0) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统无此运单号的信息,请检查!");
		}
		//校验签到表中是否运单是否已存在
		if(receiveSignInMapper.checkWayBillId(receiveSignIn.getWaybillId()) > 0) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此运单号已签到，请检查!");
		}
		
		String sourceType = arrivalNotice.getSourceType();
		String sourceId = arrivalNotice.getSourceId();
		String status = arrivalNotice.getStatus();
		
		if("2".equals(sourceType)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号"+receiveSignIn.getWaybillId()+"属于销退入库，请进行退货接收！");
		}
		if(arrivalNotice.getWarehouse().equals(receiveSignIn.getWarehouse()) == false ) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"此运单号的仓库信息与当前作业仓库不符,不能签到");
		}
		
		Map<String, Object> hashMap = new HashMap<String,Object>();
		hashMap.put("logisticsMethodCode", receiveSignIn.getWaybillType());
		String selectMethod = null;
		try {
			selectMethod = remoteLogisticsService.selectMethod("1", "5", hashMap);
		} catch (Exception e) {
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"出库系统异常");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String result = Utils.returnRemoteResultDataString(selectMethod, "调用物流系统查询物流方式异常");
		if(!("[]".equals(result))){
			JSONObject parseObject = JSONObject.parseObject(result);
			JSONObject object = (JSONObject)parseObject.get("pageInfo");
			JSONArray jsonArray = object.getJSONArray("list");
			if(!CollectionUtils.isEmpty(jsonArray)){
				JSONObject object2 = (JSONObject)jsonArray.get(0);
				String logisticsMethodName = object2.getString("logisticsMethodName");
				receiveSignIn.setWaybillTypeName(logisticsMethodName);
			}
		}
		
		receiveSignIn.setSourceType(sourceType);
		receiveSignIn.setSourceId(sourceId);
		receiveSignIn.setSignInTime(new Date());
		receiveSignIn.setSignInCreater(getUserDetailInfoUtil.getUserDetailInfo().getName());
		receiveSignIn.setSignInId(getUserDetailInfoUtil.getUserDetailInfo().getId());
		int insertSelective = receiveSignInMapper.insertSelective(receiveSignIn);
		Integer id = receiveSignIn.getId();
		
		//入库统计消息发送
		InboundWorkloadInfo info = new InboundWorkloadInfo();
		info.setWaybillId(receiveSignIn.getWaybillId());
		info.setWarehouseCode(receiveSignIn.getWarehouse());
		info.setCustomer(arrivalNotice.getCustomerName());
		info.setPutType(Integer.valueOf(sourceType));
		info.setOperationType(1);
		info.setWorkingPeople(getUserDetailInfoUtil.getUserDetailInfo().getName());
		info.setSynergyPeople(receiveSignIn.getSynergyName());
		info.setPacketCheckinNum(1);
		info.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		try {
			inboundStatisticsSender.InboundStatisticsSend(info);
		} catch (Exception e) {
			logger.error("签到工作统计异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"签到工作统计异常");
		}
		
		ReceiveSignIn returnReceiveSignIn = null;
		if("2".equals(status)){
			returnReceiveSignIn =  receiveSignInMapper.selectByPrimaryKey(id);
		}else {
			ReceiveArrivalNotice receiveArrivalNotice2 = new ReceiveArrivalNotice();
			receiveArrivalNotice2.setStatus("2");
			receiveArrivalNotice2.setSourceId(sourceId);
			receiveArrivalNotice2.setArrivalTime(new Date());
			receiveArrivalNoticeMapper.updateSelective(receiveArrivalNotice2);
			returnReceiveSignIn = receiveSignInMapper.selectByPrimaryKey(id);
		}
		return returnReceiveSignIn;
	}


	
	@Override
	public Page<ReceiveSignIn> findAll(ReceiveSignIn receiveSignIn) {
		if(!StringUtils.isBlank(receiveSignIn.getWaybillType())){
			receiveSignIn.setWaybillId(receiveSignIn.getWaybillId().toUpperCase());
		}
		if(!StringUtils.isBlank(receiveSignIn.getSourceId())){
			receiveSignIn.setSourceId(receiveSignIn.getSourceId().toUpperCase());
		}
		List<ReceiveSignIn> findAll = receiveSignInMapper.findAll(receiveSignIn);
//		for (ReceiveSignIn receiveSignIn2 : findAll) {
//			if(!StringUtils.isBlank(receiveSignIn2.getWaybillType())){
//				Map<String, Object> hashMap = new HashMap<String,Object>();
//				hashMap.put("logisticsMethodCode", receiveSignIn2.getWaybillType());
//				String selectMethod = null;
//				try {
//					selectMethod = remoteLogisticsService.selectMethod("1", "5", hashMap);
//				} catch (Exception e) {
//					if("系统操作异常".equals(e.getMessage())){
//						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"物流系统异常,请稍后再试！");
//					}
//					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
//				}
//				String result = Utils.returnRemoteResultDataString(selectMethod, "调用物流系统查询物流方式异常");
//				if(!("[]".equals(result))){
//					JSONObject parseObject = JSONObject.parseObject(result);
//					JSONObject object = (JSONObject)parseObject.get("pageInfo");
//					JSONArray jsonArray = object.getJSONArray("list");
//					if(!CollectionUtils.isEmpty(jsonArray)){
//						JSONObject object2 = (JSONObject)jsonArray.get(0);
//						String logisticsMethodName = object2.getString("logisticsMethodName");
//						receiveSignIn2.setWaybillType(logisticsMethodName);
//					}
//				}
//			}
//		}
		PageInfo pageInfo = new PageInfo(findAll);
		return new Page(pageInfo);
	}
	
	
}
