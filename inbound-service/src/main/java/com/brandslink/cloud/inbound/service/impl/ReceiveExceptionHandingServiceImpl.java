package com.brandslink.cloud.inbound.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.entity.PutawayException;
import com.brandslink.cloud.inbound.entity.PutawayList;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveExceptionHanding;
import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.PutawayExceptionMapper;
import com.brandslink.cloud.inbound.mapper.PutawayListMapper;
import com.brandslink.cloud.inbound.mapper.QualityBoxPutawayMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveExceptionHandingMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.mapper.SellingBackMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.ReceiveExceptionHandingService;

@Service
public class ReceiveExceptionHandingServiceImpl  implements ReceiveExceptionHandingService{
	@Resource
	private ReceiveExceptionHandingMapper receiveExceptionHandingMapper;
	@Resource
    private PutawayExceptionMapper putawayExceptionMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	@Resource
	private QualityBoxPutawayMapper qualityBoxPutawayMapper;
	@Resource
	private SellingBackMapper sellingBackMapper;
	@Resource
	private PutawayListMapper putawayListMapper;
	private final Logger logger = LoggerFactory.getLogger(ReceiveExceptionHandingServiceImpl.class);
	
	
	@Override
	public void insertExceptionInfo(ReceiveExceptionHanding exHandle) {
		if(StringUtil.isBlank(exHandle.getWaybillId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号不能为空");
        }
		if(StringUtil.isBlank(exHandle.getHandler())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"受理人不能为空");
        }
		if(StringUtil.isBlank(exHandle.getWarehouse())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"仓库不能为空");
        }
		if(StringUtil.isBlank(exHandle.getLogistics())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运输方式不能为空");
        }
		if(exHandle.getParcelQuantity() == null || exHandle.getParcelQuantity() <= 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"包裹数有误  请检查");
        }
		
		if(receiveGoodDetailsMapper.checkWayBillIdAmount(exHandle.getWaybillId())<=0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统无此运单号的信息,请检查!");
        }
		
		receiveExceptionHandingMapper.insertSelective(exHandle);
//		ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(exHandle.getWaybillId());
//		/**---------------------------异常上报-------------------------------------------*/
//		Map<String, Object> param = new HashMap<>();
////        param.put("createBy", getUserDetailInfoUtil.getUserDetailInfo().getAccount());
//        param.put("createBy", "zc");
//        param.put("exNumber", exHandle.getParcelQuantity());
//        param.put("exReason", 5);
//        param.put("sourceNo", notice.getSourceId());
//        param.put("sourceType", 1);
//        param.put("warehouseCode", exHandle.getWarehouse());
//        param.put("wayBillNo", exHandle.getWaybillId());
//        try {
//        	String addEx = remoteCenterService.addEx(param);
//            String data = Utils.returnRemoteResultDataString(addEx, "基础数据，异常上报接口异常！！");
//        } catch (GlobalException e){
//            if ("系统操作异常".equals(e.getMessage())){
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础数据，异常上报接口异常！！");
//            }
//            throw new GlobalException(e.getErrorCode(), e.getMessage());
//        }
	}


	
	@Override
	public boolean insertPutawayExInfo(PutawayException putawayEx) {
	   if(StringUtil.isBlank(putawayEx.getListBoxId())) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"货列箱号不能为空");
       }
	   if(StringUtil.isBlank(putawayEx.getPutawayId())) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"上架单号不能为空");
       }
       if(StringUtil.isBlank(putawayEx.getExceptionBoxId())) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"异常箱号不能为空");
       }
       if(StringUtil.isBlank(putawayEx.getSku())) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"sku不能为空");
       }
       if(StringUtil.isBlank(putawayEx.getWarehouseCode())) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"仓库信息不能为空");
       }
       if(putawayEx.getAmount() == null || putawayEx.getAmount() <= 0) {
           throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"异常上报数量有误 请检查");
       }
		
//     Integer checkListBoxAmount = qualityBoxPutawayMapper.checkListBoxAmount(putawayEx.getListBoxId(), putawayEx.getWarehouse());
//	   if(checkListBoxAmount <= 0){
//		  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该货列箱不存在未上架的货物,不能异常上报。请检查!");
//	   }
	   //上架前异常上报
//       QualityBoxPutaway qualityBoxPutaway = new QualityBoxPutaway();
//       qualityBoxPutaway.setListBoxNumber(putawayEx.getListBoxId());
//       qualityBoxPutaway.setSku(putawayEx.getSku());
//       qualityBoxPutaway.setWarehouseCode(putawayEx.getWarehouse());
//	   QualityBoxPutaway selectSkuDetailInfo = qualityBoxPutawayMapper.selectSkuDetailInfo(qualityBoxPutaway);
//	   if(selectSkuDetailInfo == null)
//		   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"sku"+qualityBoxPutaway.getSku()+"不存在上架明细中,不能异常上报。请检查！");
//	   if(putawayEx.getAmount() > selectSkuDetailInfo.getPlannedPutawayNumber())
//		   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"异常上报数量大于计划上架数量，不能异常上报！");
	   
       putawayEx.setCreater(getUserDetailInfoUtil.getUserDetailInfo().getName());
	   putawayEx.setCreaterId(getUserDetailInfoUtil.getUserDetailInfo().getId());
	   putawayEx.setCreateTime(new Date());
	   int insertSelective = putawayExceptionMapper.insertSelective(putawayEx);
	   
	   PutawayList putawayList = new PutawayList();
	   putawayList.setPutawayId(putawayEx.getPutawayId());
	   putawayList.setWarehouseCode(putawayEx.getWarehouseCode());
	   putawayList.setIsException(Byte.valueOf("1"));
	   putawayListMapper.updateByPrimaryKeySelective(putawayList);
	   
	   ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(putawayEx.getWaybillNumber());
	   if(null != notice){
		   Map<String, Object> param = new HashMap<>();
		   param.put("createBy", getUserDetailInfoUtil.getUserDetailInfo().getName());
		   param.put("boxNo", putawayEx.getExceptionBoxId());
		   param.put("goodsSku", putawayEx.getSku());
		   param.put("positionCode", putawayEx.getRecommendedLocationCode());
		   param.put("exNumber", putawayEx.getAmount());
		   param.put("exReason", putawayEx.getExceptionCause());
		   param.put("sourceNo", notice.getSourceId());
		   param.put("sourceType", 2);
		   param.put("warehouseCode", notice.getWarehouse());
		   param.put("wayBillNo", putawayEx.getWaybillNumber());
		   logger.info("【上架异常上报】"+param.toString());
		   try {
			   String resultData = Utils.getResultData(remoteCenterService.addEx(param), SystemConstants.nameType.SYS_CENTER, "异常上报接口异常");
			   logger.info("【上架异常上报结果】"+resultData);
		   } catch (Exception e){
			   if ("系统操作异常".equals(e.getMessage())){
				   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础数据，异常上报接口异常！！");
			   }
			   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
		   }
	   }else{
		   SellingBack sellingBack = sellingBackMapper.selectSellingBackIdByWaybill(putawayEx.getWaybillNumber());
		   Map<String, Object> param = new HashMap<>();
		   param.put("createBy", getUserDetailInfoUtil.getUserDetailInfo().getName());
		   param.put("boxNo", putawayEx.getExceptionBoxId());
		   param.put("goodsSku", putawayEx.getSku());
		   param.put("positionCode", putawayEx.getRecommendedLocationCode());
		   param.put("exNumber", putawayEx.getAmount());
		   param.put("exReason", putawayEx.getExceptionCause());
		   param.put("sourceNo", sellingBack.getSourceId());
		   param.put("sourceType", 2);
		   param.put("warehouseCode", sellingBack.getWarehouse());
		   param.put("wayBillNo", putawayEx.getWaybillNumber());
		   logger.info("【上架异常上报】"+param.toString());
		   try {
			   String resultData = Utils.getResultData(remoteCenterService.addEx(param), SystemConstants.nameType.SYS_CENTER, "异常上报接口异常");
			   logger.info("【上架异常上报结果】"+resultData);
		   } catch (Exception e){
			   if ("系统操作异常".equals(e.getMessage())){
				   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础数据，异常上报接口异常！！");
			   }
			   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
		   }
	   }
       if(insertSelective >= 1){
    	   return true;
       }else{
    	   return false;
       }
	}

	
}
