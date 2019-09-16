package com.brandslink.cloud.inbound.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.SellingBackDto;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.entity.SellingBackDetail;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.QualityControlDetailsMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.mapper.SellingBackDetailMapper;
import com.brandslink.cloud.inbound.mapper.SellingBackMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.remote.RemoteLogisticsService;
import com.brandslink.cloud.inbound.remote.RemoteOutBoundService;
import com.brandslink.cloud.inbound.remote.RemoteUserService;
import com.brandslink.cloud.inbound.service.SellingBackService;
import com.brandslink.cloud.inbound.utils.MUtils;
import com.github.pagehelper.PageInfo;

@Service
public class SellingBackServiceImpl implements SellingBackService{

	@Resource
	private SellingBackMapper sellingBackMapper;
	@Resource
	private SellingBackDetailMapper sellingBackDetailMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private RemoteOutBoundService remoteOutBoundService;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private QualityControlDetailsMapper qualityControlDetailsMapper;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private RemoteUserService remoteUserService;
	@Resource
	private RemoteLogisticsService remoteLogisticsService;
	@Resource
	private MUtils mUtils;
	private final Logger logger = LoggerFactory.getLogger(SellingBackServiceImpl.class);


	
	public SellingBackDto selectDetailInfo(String wayBillId) {
		if(StringUtil.isBlank(wayBillId))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号不能为空");
		SellingBack sellingBack = new SellingBack();
		sellingBack.setWaybillId(wayBillId);
		List<SellingBack> findAll = sellingBackMapper.findAll(sellingBack);
		SellingBack returnSellingBack = new SellingBack();
		Map<String,Object> map = new HashMap<>();
		if(CollectionUtils.isEmpty(findAll)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"查询不到此运单号的信息");
		}else{
			returnSellingBack = findAll.get(0);
			returnSellingBack.setWarehouse(mUtils.selectWareHouseName(returnSellingBack.getWarehouse()));
			if(returnSellingBack.getPlatform() != null){
				returnSellingBack.setPlatform(mUtils.getLogisticsPlatform(returnSellingBack.getPlatform()));
			}
			Integer plannedTotal = sellingBackDetailMapper.quantityStatistics(returnSellingBack.getSellingBackId());
			map.put("plannedQuantity", plannedTotal);//计划数量
		}
		
		List<SellingBackDetail> selectInfo = sellingBackDetailMapper.selectInfo(wayBillId);
		List<Integer> list = new ArrayList<>();
		for (SellingBackDetail s : selectInfo) {
			//设置收货数量
			Integer selectDeliveryQuantityByWayBillId = qualityControlDetailsMapper.selectDeliveryQuantityByWayBillId(s.getWaybillId(), s.getSku());
			if(selectDeliveryQuantityByWayBillId != null){
				list.add(selectDeliveryQuantityByWayBillId);
				s.setDeliveryQuantity(selectDeliveryQuantityByWayBillId);
				s.setDifferQuantity(selectDeliveryQuantityByWayBillId - s.getPlannedQuantity()); 
			}else{
				s.setDeliveryQuantity(0);
				s.setDifferQuantity(0 - s.getPlannedQuantity()); 
			}
			String skuInfoBySku = remoteCenterService.getSkuInfoBySku(new String[]{s.getSku()});
			JSONArray parseObject4 = Utils.resultParseArray(skuInfoBySku,"基础数据", "sku查询异常");
			if(parseObject4 != null && parseObject4.size() != 0){
				JSONObject jsonObject = parseObject4.getJSONObject(0);
				if(jsonObject != null){
					String productName = jsonObject.getString("productName");
					String productPictures = jsonObject.getString("productPictures");
					s.setPictureUrl(productPictures);
					s.setProductName(productName);
				}else{
					s.setPictureUrl("暂无商品图片");
					s.setProductName("暂无商品名称");
				}
			}
		}
		int suma =  list.stream().mapToInt(Integer::intValue).sum();
		map.put("deliveryQuantity", suma);//实际收货数量
		returnSellingBack.setStatisticsMap(map);
		SellingBackDto sellingBackDto = new SellingBackDto();
		sellingBackDto.setSellingBack(returnSellingBack);
		sellingBackDto.setSellingBackDetail(selectInfo);
		return sellingBackDto;
	}
 
	 


	
	public Page<SellingBack> findAll(SellingBack sellingBack) {
		sellingBack.setWarehouseCodes(mUtils.getWarehouseCodeList());
		//统一大写
		if(sellingBack.getWaybillId() != null){
			sellingBack.setWaybillId(sellingBack.getWaybillId().toUpperCase());
		}
		
		List<SellingBack> findAll = sellingBackMapper.findAll(sellingBack);
		for (SellingBack sellingBack2 : findAll) {
			if(sellingBack2.getPlatform() != null){
				String logisticsPlatform = mUtils.getLogisticsPlatform(sellingBack2.getPlatform());
				sellingBack2.setPlatform(logisticsPlatform);
			}
			
			if(sellingBack2.getWarehouse() != null){
				sellingBack2.setWarehouse(mUtils.selectWareHouseName(sellingBack2.getWarehouse()));
			}
			if("3".equals(sellingBack2.getStatus())){
				List<SellingBackDetail> selectInfo = sellingBackDetailMapper.selectInfo(sellingBack2.getWaybillId());
				boolean flag = true;
				for (SellingBackDetail sd : selectInfo) {
					Integer deliveryQuantity = qualityControlDetailsMapper.selectDeliveryQuantityByWayBillId(sd.getWaybillId(), sd.getSku());
					if(null == deliveryQuantity){
						flag = false;
						break;
					}else{
						if(deliveryQuantity < sd.getPlannedQuantity()){
							flag = false;
						}
					}
					
				}
				if(flag == true){
					SellingBack sb = new SellingBack();
					sb.setStatus("4");
					sb.setSellingBackId(sellingBack2.getSellingBackId());
					sellingBackMapper.updateSelective(sb);
				}
			}
			
			if("1".equals(sellingBack2.getStatus())){
				List<SellingBackDetail> selectInfo = sellingBackDetailMapper.selectInfo(sellingBack2.getWaybillId());
				boolean flag = false;
				for (SellingBackDetail sd : selectInfo) {
					Integer selectDeliveryQuantityByWayBillId = qualityControlDetailsMapper.selectDeliveryQuantityByWayBillId(sd.getWaybillId(), sd.getSku());
					if(null != selectDeliveryQuantityByWayBillId){
						if(selectDeliveryQuantityByWayBillId > 0){
							flag = true; 
					    }
				    }
				}
				
				if(flag == true){
					SellingBack sb = new SellingBack();
					sb.setStatus("3");
					sb.setSellingBackId(sellingBack2.getSellingBackId());
					sellingBackMapper.updateSelective(sb);
					
					
					
				}
			}
			
		}
		PageInfo pageInfo = new PageInfo(findAll);
		return new Page(pageInfo);
	}



	
	public void sellingBackSuccess(List<SellingBack> sellingBack) {
		if(CollectionUtils.isEmpty(sellingBack))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"参数不能为空");
		sellingBack.forEach(s->{
			s.setStatus("4");//单据状态(1 待收货/2 收货中/3 部分收货/4 收货完成)
			sellingBackMapper.updateSelective(s);
		});
	}




	
	public void insertInfo(SellingBack sellingBack) {
		if(StringUtils.isBlank(sellingBack.getWaybillId()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号/包裹号不能为空");
		if(StringUtils.isBlank(sellingBack.getWarehouse()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"所属仓库不能为空");
		if(StringUtils.isBlank(sellingBack.getSellingBackType()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"销退类型不能为空");
		sellingBack.setWaybillId(sellingBack.getWaybillId().toUpperCase());
		//校验运单号是否已存在
		if(sellingBackMapper.checkCount(sellingBack.getWaybillId())>0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号"+sellingBack.getWaybillId()+"已存在,请检查！");
		Integer amount = receiveGoodDetailsMapper.checkWayBillIdAmount(sellingBack.getWaybillId());
		//运单号存在于到货通知单中
		if(amount > 0){
			ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
			receiveGoodDetails.setWaybillId(sellingBack.getWaybillId());
			List<ReceiveGoodDetails> selectGoodDetailsSelective = receiveGoodDetailsMapper.selectGoodDetailsSelective(receiveGoodDetails);
			ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(sellingBack.getWaybillId());
			
			if(!((notice.getWarehouse()).equals(sellingBack.getWarehouse()))){
				String currentWarehouse = mUtils.selectWareHouseName(sellingBack.getWarehouse());
				String rightWarehouse = mUtils.selectWareHouseName(notice.getWarehouse());
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号"+sellingBack.getWaybillId()+"来源单号仓库为"+rightWarehouse+"与当前作业仓库"+currentWarehouse+"不符，请检查！");
			}
			
			if("2".equals(notice.getSourceType())){
				sellingBack.setSourceId(notice.getSourceId());
				sellingBack.setSellingBackId(setSellingBackId());
				sellingBack.setStatus("1");
				sellingBack.setQualityType(notice.getQualityType());
				sellingBack.setCustomer(notice.getCustomer());
				sellingBack.setCustomerName(notice.getCustomerName());
				sellingBack.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getName());
				sellingBack.setReceiverId(getUserDetailInfoUtil.getUserDetailInfo().getId());
				sellingBack.setReceiveTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				//生成销退收货单
				sellingBackMapper.insertSelective(sellingBack);
				
				selectGoodDetailsSelective.forEach(s->{
					SellingBackDetail sbd = new SellingBackDetail();
					sbd.setWaybillId(s.getWaybillId());
					sbd.setSku(s.getSku());
					sbd.setPlannedQuantity(s.getPlannedQuantity());
					//生成销退详情单
					sellingBackDetailMapper.insertSelective(sbd);
					
					ReceiveGoodDetails receiveGoodDetails2 = new ReceiveGoodDetails();
					receiveGoodDetails2.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getName());
					receiveGoodDetails2.setReceiveTime(new Date());
					receiveGoodDetails2.setWaybillId(s.getWaybillId());
					receiveGoodDetailsMapper.updateSelective(receiveGoodDetails2);
				});
				
				//销退接收后  修改到货通知单状态
				ReceiveArrivalNotice n = new ReceiveArrivalNotice();
				n.setStatus("2");
				n.setSourceId(notice.getSourceId());
				receiveArrivalNoticeMapper.updateSelective(n);
			}else{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号:"+sellingBack.getWaybillId()+"没有单据记录,不可销退接收,请检查！");
			}
		} else {
//			根据包裹号查询出库单
//			String findOrderDetailsByOrderNum = null;
//			try {
//				findOrderDetailsByOrderNum = remoteOutBoundService.findOrderDetailsByOrderNum(sellingBack.getWaybillId());
//				logger.info("[出库调用]"+findOrderDetailsByOrderNum);
//			} catch (Exception e) {
//				if("系统操作异常".equals(e.getMessage())){
//					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"出库系统异常");
//				}
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
//			}
//			String returnDataString = Utils.returnRemoteResultDataString(findOrderDetailsByOrderNum, "调用出库异常");
//			if("[]".equals(returnDataString) == false){//运单号存在于订单池中
//				JSONArray parseArray = JSONObject.parseArray(returnDataString);
//				logger.info("[出库调用订单JSONArray]"+parseArray.toString());
//				for (Object obj : parseArray) {
//					JSONObject jsonObj = (JSONObject)JSON.toJSON(obj);
//					sellingBack.setSourceId(jsonObj.getString("orderNum"));
//					sellingBack.setSellingBackId(setSellingBackId());
//					sellingBack.setStatus("1");
//					sellingBack.setPlatform(jsonObj.getString("salesChannels"));
//					sellingBack.setLogistics(jsonObj.getString("receiver"));
//					sellingBack.setMailingMethod(jsonObj.getString("mailingMethod"));
//					sellingBack.setParcelId(jsonObj.getString("packageNum"));
//					sellingBack.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getAccount());
//					sellingBack.setReceiverId(getUserDetailInfoUtil.getUserDetailInfo().getId());
//					//生成销退单
//					sellingBackMapper.insertSelective(sellingBack);
//					
//					Object orderDetailsList = jsonObj.get("orderDetailsList");
//					if(null != orderDetailsList){
//						JSONArray parseArray2 = JSON.parseArray(orderDetailsList.toString());
//						logger.info("[出库调用订单详情JSONArray]"+parseArray.toString());
//						for (Object object : parseArray2) {
//							JSONObject detailObj = (JSONObject)JSON.toJSON(object);
//							SellingBackDetail sbd = new SellingBackDetail();
//							sbd.setWaybillId(sellingBack.getWaybillId());
//							sbd.setSku(detailObj.getString("sku"));
//							sbd.setPlannedQuantity(detailObj.getInteger("number"));
//							//生成销退详情单
//							sellingBackDetailMapper.insertSelective(sbd);
//						}
//					}else{
//						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此包裹没有单据记录,不可销退接收,请检查！");
//					}
//				}
				
				
//			    根据运单号查询出库单
				String orderListModel = null;
				try {
					orderListModel = remoteOutBoundService.findOrderDetailsByZ(sellingBack.getWaybillId());
					logger.info("[出库调用]"+orderListModel);
				} catch (Exception e) {
					if("系统操作异常".equals(e.getMessage())){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"出库系统异常");
					}
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
				}
				String returnDataString = Utils.returnRemoteResultDataString(orderListModel, "调用出库异常");
				if(null != returnDataString){//运单号存在于订单池中
						JSONObject jsonObj = JSONObject.parseObject(returnDataString);
						logger.info("[出库调用订单]"+jsonObj.toString());
						String warehouseCode = jsonObj.getString("warehouseCode");
						if(warehouseCode.equals(sellingBack.getWarehouse()) == false){
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此运单号的来源单号仓库信息与当前作业仓库不符,请检查");
						}
						sellingBack.setSourceId(jsonObj.getString("orderNum"));
						sellingBack.setSellingBackId(setSellingBackId());
						sellingBack.setStatus("1");
						sellingBack.setCustomer(jsonObj.getString("customer"));
						sellingBack.setPlatform(jsonObj.getString("salesChannels"));
						sellingBack.setLogistics(jsonObj.getString("receiver"));
						sellingBack.setMailingMethod(jsonObj.getString("mailingMethod"));
						sellingBack.setParcelId(jsonObj.getString("packageNum"));
						sellingBack.setReceiver(getUserDetailInfoUtil.getUserDetailInfo().getName());
						sellingBack.setReceiverId(getUserDetailInfoUtil.getUserDetailInfo().getId());
						sellingBack.setReceiveTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						
						String customerInfo = null;
						try {
							customerInfo = remoteUserService.getCustomerByCustomerCode(jsonObj.getString("customer"));
						} catch (Exception e) {
							logger.error("根据客户code查询货主信息异常",e);
							if("系统操作异常".equals(e.getMessage())){
								throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"用户服务出现异常,请稍后再试!");
							}
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
						}
						String result = Utils.returnRemoteResultDataString(customerInfo, "根据客户id查询货主列表异常");
						if(null != result){
							JSONObject parseObject = JSONObject.parseObject(result);
							String qualityType = parseObject.getString("cancellationRefundCode");
							String name = parseObject.getString("chineseName");
							String str = qualityType.substring(0, 1);
							sellingBack.setQualityType(str);
							sellingBack.setCustomerName(name);
						}else{
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此单号在出库订单中没有质检方式,暂不能销退，请检查后再试!");
						}
						
						//生成销退单
						sellingBackMapper.insertSelective(sellingBack);
						
						Object orderDetailsList = jsonObj.get("orderDetailsList");
						if(null != orderDetailsList){
							JSONArray parseArray2 = JSON.parseArray(orderDetailsList.toString());
							logger.info("[出库调用订单详情]"+parseArray2.toString());
							for (Object object : parseArray2) {
								JSONObject detailObj = (JSONObject)JSON.toJSON(object);
								SellingBackDetail sbd = new SellingBackDetail();
								sbd.setWaybillId(sellingBack.getWaybillId());
								sbd.setSku(detailObj.getString("sku"));
								sbd.setPlannedQuantity(detailObj.getInteger("number"));
								//生成销退详情单
								sellingBackDetailMapper.insertSelective(sbd);
							}
						}else{
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此包裹没有单据记录,不可销退接收,请检查！");
						}
				
			}else{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号没有单据记录,不可销退接收,请检查！");
			}
		}
	}
	
	
	
	public List<Map<String,Object>> quantityStatistics(List<String> sellingBackId) {
		List<Map<String,Object>> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		
		for (String str : sellingBackId) {
			List<SellingBackDetail>  skuMap = sellingBackDetailMapper.selectInfoBySellingBackId(str);
			for (SellingBackDetail sb : skuMap) {
				Integer deliveryQuantity = qualityControlDetailsMapper.selectDeliveryQuantityByWayBillId(sb.getWaybillId(), sb.getSku());
				if(null != deliveryQuantity){
					list2.add(deliveryQuantity);
				}
			}
			int sum = 0;
			if(CollectionUtils.isNotEmpty(list2)){
				sum = list2.stream().mapToInt(Integer::intValue).sum();
			}
			Integer quantityStatistics = sellingBackDetailMapper.quantityStatistics(str);//计划数量
			Map<String,Object> hashMap = new HashMap<>();
			hashMap.put("sellingBackId", str);
			hashMap.put("plannedQuantity", quantityStatistics);
			hashMap.put("deliveryQuantity", sum);
			hashMap.put("differQuantity", sum - quantityStatistics);
			list.add(hashMap);
		}
		return list;
	}
	
	
	
	public List<SellingBack> excelExport(List<String> list) {
		List<SellingBack> excelExport = sellingBackMapper.excelExport(list);
		for (int i = 0; i < excelExport.size(); i++) {
			excelExport.get(i).setId(i+1);
			excelExport.get(i).setWarehouse(mUtils.selectWareHouseName(excelExport.get(i).getWarehouse()));
			excelExport.get(i).setStatus(mUtils.getDictionaryDataDetail("receive_arrival_notice_status", excelExport.get(i).getStatus()));
			excelExport.get(i).setSellingBackType(mUtils.getDictionaryDataDetail("rejected_type",excelExport.get(i).getSellingBackType()));
			if(excelExport.get(i).getPlatform() != null){
				excelExport.get(i).setPlatform(mUtils.getLogisticsPlatform(excelExport.get(i).getPlatform()));
			}
		}
		return excelExport;
	}

	
	/**
	 * 生成销退单号
	 * @return
	 */
	public synchronized  String setSellingBackId(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String format = sdf.format(new Date());//20190601
		String substring = format.substring(2, format.length());//时间字符串
		
			//查询当天时间内的最后一位流水号  TH19060500001
			List<String> selectSellingBackIdByDate = sellingBackMapper.selectSellingBackIdByDate(substring);
			
			String SerialNumber = "";
			List<Integer> list = new ArrayList<>();
			if(selectSellingBackIdByDate != null && selectSellingBackIdByDate.size() != 0){
				//遍历出最大值
				for (String string : selectSellingBackIdByDate) {
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
		
			String SellingBackId = "TH" + substring + SerialNumber;
			return SellingBackId;
	}
	
	public String subStr(String str, int start) {
        if (str == null || str.equals("") || str.length() == 0)
            return "";
        if (start < str.length()) {
            return str.substring(start);
        } else {
            return "";
        }
    }
	
}
