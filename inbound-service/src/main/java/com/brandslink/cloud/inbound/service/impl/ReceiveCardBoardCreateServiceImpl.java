package com.brandslink.cloud.inbound.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.controller.ReceiveSignInController;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveCardBoardCreate;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveCardBoardCreateMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.rabbitmq.InboundStatisticsSender;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.ReceiveCardBoardCreateService;
import com.brandslink.cloud.inbound.utils.MUtils;

@Service
public class ReceiveCardBoardCreateServiceImpl implements ReceiveCardBoardCreateService{
	@Resource
	private ReceiveCardBoardCreateMapper receiveCardBoardCreateMapper;
	@Resource
	private ReceiveGoodDetailsMapper ReceiveGoodDetailsMapper;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private RedisUtils  redisUtil;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private InboundStatisticsSender inboundStatisticsSender;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private MUtils mUtils;
	private final Logger logger = LoggerFactory.getLogger(ReceiveSignInController.class);
	
	
	@Override
	public List<ReceiveGoodDetails> selectGoodDetailsSelective(ReceiveGoodDetails receiveGoodDetails) {
		List<ReceiveGoodDetails> selectGoodDetailsSelective = ReceiveGoodDetailsMapper.selectGoodDetailsSelective(receiveGoodDetails);
		return selectGoodDetailsSelective;
	}

	
	/**
	 * 添加卡板信息
	 */
	@Override
	public Integer insertReceiveCardBoardCreate(List<ReceiveCardBoardCreate> receiveCardBoardCreate) {
		
		for (ReceiveCardBoardCreate receiveCardBoardCreate2 : receiveCardBoardCreate) {
			receiveCardBoardCreate2.setWaybillId(receiveCardBoardCreate2.getWaybillId().toUpperCase());
			receiveCardBoardCreate2.setCardCreater(getUserDetailInfoUtil.getUserDetailInfo().getName());
			receiveCardBoardCreate2.setCardCreaterId(getUserDetailInfoUtil.getUserDetailInfo().getId());
			receiveCardBoardCreate2.setCreateTime(new Date());
			Integer insert = receiveCardBoardCreateMapper.insertSelective(receiveCardBoardCreate2);
			
			//更新商品详情表中的数据
			ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
			receiveGoodDetails.setWaybillId(receiveCardBoardCreate2.getWaybillId());
			receiveGoodDetails.setCardBoardId(receiveCardBoardCreate2.getCardBoardId());
			receiveGoodDetails.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getName());
			receiveGoodDetails.setReceiverId(getUserDetailInfoUtil.getUserDetailInfo().getId());
			receiveGoodDetails.setReceiveTime(new Date());
			ReceiveGoodDetailsMapper.updateSelective(receiveGoodDetails);
		}
		
		ReceiveArrivalNotice arrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(receiveCardBoardCreate.get(0).getSourceId());
		
		//入库统计消息发送
		InboundWorkloadInfo info = new InboundWorkloadInfo();
		info.setWarehouseCode(arrivalNotice.getWarehouse());
		info.setCustomer(arrivalNotice.getCreater());
		info.setPutType(Integer.valueOf(arrivalNotice.getSourceType()));
		info.setOperationType(1);
		info.setWorkingPeople(getUserDetailInfoUtil.getUserDetailInfo().getName());
		info.setSynergyPeople(receiveCardBoardCreate.get(0).getSynergyName());
		info.setEncoderNum(1);
		info.setPacketEceiveNum(receiveCardBoardCreate.size());
		info.setCreateTime(new Date().toString());
		
		try {
			inboundStatisticsSender.InboundStatisticsSend(info);
		} catch (Exception e) {
			logger.error("签到消息发送异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"签到消息发送异常");
		}
		
		//删除多余信息
		receiveCardBoardCreateMapper.deleteInfo("sourceId", "wayBillId");
		return null;
	}

	

	@Override
	public ReceiveGoodDetails selectInfoById(ReceiveGoodDetails receiveGoodDetails) {
		List<ReceiveGoodDetails> selectInfoById = ReceiveGoodDetailsMapper.selectInfoById(receiveGoodDetails);
		if(CollectionUtils.isNotEmpty(selectInfoById)){
			ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(receiveGoodDetails.getWaybillId());
			ReceiveGoodDetails receiveGoodDetails2 = selectInfoById.get(0);
			notice.setWarehouse(mUtils.selectWareHouseName(receiveGoodDetails2.getReceiveArrivalNotice().getWarehouse()));
			receiveGoodDetails2.setReceiveTime(new Date());
			receiveGoodDetails2.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getName());
			receiveGoodDetails2.setReceiveArrivalNotice(notice);
			return receiveGoodDetails2;
		}
		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100501,"未查询出此运单号在系统中的信息,请检查");
	}


	
	/**
	 * 生成卡板流水号
	 * @return
	 */
	@Override
	public synchronized  String setReceiveArrivalNoticeId(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String format = sdf.format(new Date());//20190601
		String substring = format.substring(2, format.length());//时间字符串
			//查询当天时间内的最后一位流水号  KB19060500001
			List<String> selectArrivalNoticeIdByDate = receiveCardBoardCreateMapper.selectCardBoardCreateIdByDate(substring);
			
			String SerialNumber = "";
			List<Integer> list = new ArrayList<>();
			if(selectArrivalNoticeIdByDate != null && selectArrivalNoticeIdByDate.size() != 0){
				//遍历出最大值
				for (String string : selectArrivalNoticeIdByDate) {
					String sub = string.substring(8, string.length());
					Integer valueOf = Integer.valueOf(sub);
					list.add(valueOf);
				}
				Integer max = Collections.max(list);
				int newMaxNumber = 100000 + max + 1;
				SerialNumber = subStr(String.valueOf(newMaxNumber),1);
			}else{
				SerialNumber = "00001";
			}
		
			String CardBoardCreateId = "KB" + substring + SerialNumber;
			
			ReceiveCardBoardCreate receiveCardBoardCreate = new  ReceiveCardBoardCreate();
			receiveCardBoardCreate.setCardBoardId(CardBoardCreateId);
			receiveCardBoardCreate.setSourceId("sourceId");
			receiveCardBoardCreate.setWaybillId("waybillId");
			Integer insert = receiveCardBoardCreateMapper.insertSelective(receiveCardBoardCreate);
			return CardBoardCreateId;
	}
	
	public String subStr(String str, int start) {
        if (str == null || str.equals("") || str.length() == 0) {
            return "";
        }
        if (start < str.length()) {
            return str.substring(start);
        } else {
            return "";
        }
    }
	
	
}
	
	

