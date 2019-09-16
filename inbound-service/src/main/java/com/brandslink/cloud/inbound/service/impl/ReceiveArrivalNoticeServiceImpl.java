package com.brandslink.cloud.inbound.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.AttestationSignUtil;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.InventoryUpdateDTO;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsDto;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsInfo;
import com.brandslink.cloud.inbound.entity.QualityControlDetails;
import com.brandslink.cloud.inbound.entity.QualityControlList;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.PutawayListMapper;
import com.brandslink.cloud.inbound.mapper.QualityControlDetailsMapper;
import com.brandslink.cloud.inbound.mapper.QualityControlListMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.remote.RemoteUserService;
import com.brandslink.cloud.inbound.service.ReceiveArrivalNoticeService;
import com.brandslink.cloud.inbound.utils.MUtils;
import com.github.pagehelper.PageInfo;

@Service("receiveArrivalNoticeServiceImpl")
public class ReceiveArrivalNoticeServiceImpl implements ReceiveArrivalNoticeService{

	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private QualityControlListMapper qualityControlListMapper;
	@Resource
	private QualityControlDetailsMapper qualityControlDetailsMapper;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private AttestationSignUtil attestationSignUtil;
	@Resource
	private RemoteUserService remoteUserService;
	@Resource
	private PutawayListMapper putawayListMapper;
	@Resource
	private MUtils mUtils;
	private final Logger logger = LoggerFactory.getLogger(ReceiveArrivalNoticeServiceImpl.class);
	
	/**-----------------------------------------到货通知接口--------------------------------------------------------*/
	
	
	/**
	 * 添加到货通知单信息
	 */
	public boolean insertReceiveArrivalNotice(ReceiveArrivalNotice notice) {
		CustomerInfoEntity verifySign = attestationSignUtil.verifySign();
		List<ReceiveGoodDetailsInfo> details = notice.getReceiveGoodDetails();
		ArrayList<String> typeList = new ArrayList<String>(Arrays.asList("1","2","3"));
		if(notice == null || details == null || details.size() == 0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"商品信息不能为空");
		/**----------------------------校验到货通知单信息是否为空----------------------------------- */
		if(StringUtils.isBlank(notice.getSourceId()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"来源单号不能为空");
		if(receiveArrivalNoticeMapper.selectAmountBySourceId(notice.getSourceId()) > 0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200407,"来源单号"+notice.getSourceId()+"已存在,请检查后再试!");
		if(StringUtils.isBlank(notice.getSourceType()))					
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"来源类型不能为空");
		if(typeList.contains(notice.getSourceType()) == false)					
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"来源类型错误,请检查");
		if(StringUtils.isBlank(notice.getQualityType()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"质检方式不能为空");
		if(StringUtils.isBlank(notice.getWarehouse()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"仓库编码不能为空");
		if(StringUtils.isBlank(notice.getPlannedTime()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"预到仓时间不能为空");
		if(!StringUtils.isBlank(notice.getShipper())){
			String shipperByCustomerId = null;
			try {
				shipperByCustomerId = remoteUserService.getShipperByCustomerId(verifySign.getId());
			} catch (Exception e) {
				logger.error("根据客户id查询货主列表异常",e);
				if("系统操作异常".equals(e.getMessage())){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,"用户服务出现异常,请稍后再试!");
				}
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,e.getMessage());
			}
			String result = Utils.returnRemoteResultDataString(shipperByCustomerId, "根据客户id查询货主列表异常");
			ArrayList<String> list = new ArrayList<>();
			if(!("[]".equals(result))){
				JSONArray parseArray = JSON.parseArray(result);
				if(null != parseArray || parseArray.size()>0){
					List<JSONObject> javaList = parseArray.toJavaList(JSONObject.class);
					for (JSONObject jsonObject : javaList) {
						String shipperCode = jsonObject.getString("shipperCode");
						list.add(shipperCode);
					}
				}
				
				if(!list.contains(notice.getShipper()))
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"货主编码填写错误,请检查！");
			}
		}				
		
		/**-------------------- 校验商品详情信息是否为空及sku是否存在----------------------------------- */
		ArrayList<String> listForCheckSku = new ArrayList<>();
		details.stream().forEach(r->{
			if(listForCheckSku.contains(r.getSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"商品详情信息中sku: "+r.getSku()+" 重复,不能入库 请检查后再试!");
			}
			if(StringUtils.isBlank(r.getSourceId()))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"商品详情来源单号不能为空");
			if(notice.getSourceId().equals(r.getSourceId()) == false)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"入库来源单号: "+notice.getSourceId()+" 与商品详情来源单号: "+r.getSourceId()+" 不一致,请检查后再试！");
			if(StringUtils.isBlank(r.getWaybillId()))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"运单号不能为空");
			if(receiveGoodDetailsMapper.checkWayBillIdAmount(r.getWaybillId())>0)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200407,"运单号"+r.getWaybillId()+"已存在,请检查后再试!");
			if(StringUtils.isBlank(r.getSku()))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"商品sku不能为空");
			if(r.getPlannedQuantity() == null || r.getPlannedQuantity() <= 0)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"商品计划数量错误");
			
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if(skuInfoBySku == null || "[]".equals(skuInfoBySku))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"仓储系统中无sku:"+r.getSku()+"的对应信息,不能入库 请检查");
			JSONArray parseArray = JSONObject.parseArray(skuInfoBySku);
			JSONObject object = (JSONObject)parseArray.get(0);
			String customer = object.getString("customer");
			if(!customer.equals(verifySign.getCustomerCode()))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200408,"输入的sku:"+r.getSku()+"不属于当前客户,不能入库 请检查");
			listForCheckSku.add(r.getSku());
		});
		
		
		/**-------------------- 添加到货商品详情信息----------------------------------- */
		ArrayList<Integer> plannedNumber = new ArrayList<>();
		ArrayList<String> wayBillNumber = new ArrayList<>();
		details.stream().forEach(r->{
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if((StringUtils.isBlank(skuInfoBySku) == false) && ("[]".equals(skuInfoBySku) == false)){
				JSONArray parseArray = JSONObject.parseArray(skuInfoBySku);
				JSONObject object = (JSONObject)parseArray.get(0);
				String categoryName = object.getString("categoryName");
				String productName = object.getString("productName");
				r.setGoodName(productName);
				r.setGoodType(categoryName);
			}
			r.setCreateTime(new Date());
			r.setWaybillId(r.getWaybillId().toUpperCase());
			receiveGoodDetailsMapper.insertSelective(r);
			plannedNumber.add(r.getPlannedQuantity());
			wayBillNumber.add(r.getWaybillId());
		});
		
		
		/**-------------------- 添加到货通知单信息----------------------------------- */
		int suma =  plannedNumber.stream().mapToInt(Integer::intValue).sum();
		TreeSet<String> treeSet = new TreeSet<String>(wayBillNumber);
		notice.setStatus("1");
		notice.setCreater(verifySign.getChineseName());
		notice.setCreaterId(Long.valueOf(verifySign.getId()));
		notice.setCustomer(verifySign.getCustomerCode());
		notice.setCustomerName(verifySign.getChineseName());
		notice.setPlannedQuantity(suma);
		notice.setParcelQuantity(treeSet.size());
		notice.setCreateTime(new Date());
		notice.setSourceId(notice.getSourceId().toUpperCase());
		notice.setArrivalNoticeId(notice.getWarehouse() + setReceiveArrivalNoticeId());//设置流水号
		receiveArrivalNoticeMapper.insertSelective(notice);
		
		
		if(("1".equals(notice.getSourceType())) || ("3".equals(notice.getSourceType()))){
			/**通知库存生成 来料/调拨 在途的数据*/
			List<InventoryUpdateDTO> list = new ArrayList<>();
			Map<String,Integer> map = new HashMap<>();
			for (ReceiveGoodDetailsInfo detail : details) {
				Integer integer = map.get(detail.getSku());
				if(integer != null){
					map.put(detail.getSku(), detail.getPlannedQuantity() + integer);
				}else{
					map.put(detail.getSku(), detail.getPlannedQuantity());
				}
			}
			
			Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, Integer> next = iterator.next();
				InventoryUpdateDTO dto = new InventoryUpdateDTO();
				dto.setGoodsSku(next.getKey());
				dto.setUpdateBy("system");
				dto.setBusinessType("put_type/"+notice.getSourceType());
				dto.setSourceOrderNumber(notice.getSourceId());
				dto.setWarehouseCode(notice.getWarehouse());
				dto.setSkuNumbers(next.getValue());
				list.add(dto);
			}
			logger.info("{入库单生成调用库存参数}：" + list.toString());
			try {
				String arrivalNoticeUpdateInventory = remoteCenterService.arrivalNoticeUpdateInventory(list);
				logger.info("{入库单生成调用库存结果}：" + list.toString());
				Utils.getResultData(arrivalNoticeUpdateInventory, SystemConstants.nameType.SYS_CENTER, "入库单生成：更新库存数据异常");
			} catch (Exception e) {
				logger.error("库存调用异常",e);
				if("系统操作异常".equals(e.getMessage())){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,"基础服务出现异常,请稍后再试!");
				}
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,e.getMessage());
			}
		}
		return true;
	}
	
	
	/**
	 * 到货通知单信息查询
	 */
	public Page<ReceiveArrivalNotice> findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice,String page,String row) {
		receiveArrivalNotice.setWarehouseCodes(mUtils.getWarehouseCodeList());
//		Page.builder(page, row);
//		List<ReceiveArrivalNotice> findAll = receiveArrivalNoticeMapper.findAll(receiveArrivalNotice);
//		findAll.stream().forEach(f->{
//			//自动修改收货状态  部分收货-->收货完成
//			if("3".equals(f.getStatus())){
//				boolean flag = true;
//				List<String> selectSkuBySourceId = receiveGoodDetailsMapper.selectSkuBySourceId(f.getSourceId());
//				for (String str : selectSkuBySourceId) {
//					List<Integer> OkStatus = qualityControlDetailsMapper.selectOkStatusBySourceOrderNumber(f.getSourceId(), str);
//					if(OkStatus.isEmpty()){
//						flag = false;
//						break;
//					}else{
//						for (Integer  status : OkStatus) {
//							if(3 != status){
//								flag = false;
//							}
//						}
//					}	
//				}
//				if(flag == true){
//					ReceiveArrivalNotice n = new ReceiveArrivalNotice();
//					n.setStatus("4");
//					n.setSourceId(f.getSourceId());
//					receiveArrivalNoticeMapper.updateSelective(n);
//				}
//			}
//			
//			//自动修改收货状态  收货中-->部分收货
//			if("2".equals(f.getStatus())){
//				boolean flag = false;
//				List<String> selectSkuBySourceId = receiveGoodDetailsMapper.selectSkuBySourceId(f.getSourceId());
//				for (String str : selectSkuBySourceId) {
//					List<Integer> OkStatus = qualityControlDetailsMapper.selectOkStatusBySourceOrderNumber(f.getSourceId(), str);
//					if(OkStatus.isEmpty() == false){
//						for (Integer  status : OkStatus) {
//							if(3 == status || 2 == status){
//								flag = true;
//							}
//						}
//					}
//					
//				}
//				if(flag == true){
//					ReceiveArrivalNotice n = new ReceiveArrivalNotice();
//					n.setStatus("3");
//					n.setSourceId(f.getSourceId());
//					receiveArrivalNoticeMapper.updateSelective(n);
//				}
//			}
//			
//		});
		Page.builder(page, row);
		List<ReceiveArrivalNotice> findAll2 = receiveArrivalNoticeMapper.findAll(receiveArrivalNotice);
		findAll2.stream().forEach(f->{
			//调用接口查询仓库信息
			f.setWarehouse(mUtils.selectWareHouseName(f.getWarehouse()));
			//查询收货数  差异数
			Integer number = qualityControlDetailsMapper.selectCountBySourceOrderNumber(f.getSourceId());
			if(number != null){
				f.setDeliveryQuantity(number);
				f.setDifferQuantity(number - f.getPlannedQuantity()); 
			}else{
				f.setDeliveryQuantity(0);
				f.setDifferQuantity(0 - f.getPlannedQuantity()); 
			}
		});
		
		PageInfo pageInfo = new PageInfo(findAll2);
		return new Page(pageInfo);
	}

	/**
	 * 根据运单号模糊查询到货通知单
	 */
	public Page<ReceiveArrivalNotice> selectArrivalNoticeByWaybillDim(String wayBillId,String page,String row) {
		Page.builder(page, row);
		ReceiveArrivalNoticeInfo info =new ReceiveArrivalNoticeInfo();
		info.setWayBillId(wayBillId);
		info.setWarehouseCodes(mUtils.getWarehouseCodeList());
		List<ReceiveArrivalNotice> selectArrivalNoticeByWaybillDim = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybillDim(info);
		for (ReceiveArrivalNotice receiveArrivalNotice : selectArrivalNoticeByWaybillDim) {
			receiveArrivalNotice.setWarehouse(mUtils.selectWareHouseName(receiveArrivalNotice.getWarehouse()));
		}
		PageInfo pageInfo = new PageInfo(selectArrivalNoticeByWaybillDim);
		return new Page(pageInfo);
	}
	
	
	/**
	 * 修改到货通知单
	 */
	public Integer updateSelective(List<ReceiveArrivalNotice> receiveArrivalNotice) {
		for (ReceiveArrivalNotice notice1 : receiveArrivalNotice) {
			
			List<Integer> selectPutawayStatus = putawayListMapper.selectPutawayStatus(notice1.getSourceId());
			if( selectPutawayStatus.contains(1) 
				|| selectPutawayStatus.contains(2) 
				|| null == selectPutawayStatus
				|| CollectionUtils.isEmpty(selectPutawayStatus)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"来源单号："+notice1.getSourceId()+" 有尚未上架完的商品,请检查上架单后再试!");
			}
			notice1.setStatus("4");
			receiveArrivalNoticeMapper.updateSelective(notice1);

			//更新质检单及质检单详情状态
			QualityControlList qcListParam = new QualityControlList();
			qcListParam.setOrderStatus(Byte.valueOf("3"));
			qcListParam.setSourceOrderNumber(notice1.getSourceId());
			qualityControlListMapper.updateBySourceOrderNum(qcListParam);

			QualityControlDetails qcDetails = new QualityControlDetails();
			qcDetails.setSkuStatus(Byte.valueOf("3"));
			qcDetails.setSourceOrderNumber(notice1.getSourceId());
			qualityControlDetailsMapper.updateBySourceOrderNum(qcDetails);

			ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(notice1.getSourceId());
			if(("1".equals(notice.getSourceType())) || ("3".equals(notice.getSourceType()))){
				/**通知库存修改来料/调拨在途的数据*/
				List<ReceiveGoodDetails> selectInfoBySourceId = receiveGoodDetailsMapper.selectInfoBySourceId(notice1.getSourceId());
				List<String> skuList = new ArrayList<>();
				selectInfoBySourceId.stream().forEach(det->{
					if(!skuList.contains(det.getSku())){
						skuList.add(det.getSku());
					}
				}); 
				List<InventoryUpdateDTO> list = new ArrayList<>();
				for (String str : skuList) {
					InventoryUpdateDTO dto = new InventoryUpdateDTO();
					dto.setGoodsSku(str);
					dto.setUpdateBy("system");
					dto.setBusinessType("put_type/"+notice.getSourceType());
					dto.setSourceOrderNumber(notice.getSourceId());
					dto.setWarehouseCode(notice.getWarehouse());
					list.add(dto);
				}
				logger.info("{入库单完成调用库存参数}：" + list.toString());
				try {
					String receivingFinish = remoteCenterService.receivingFinishUpdateInventory(list);
					String resultData = Utils.getResultData(receivingFinish, SystemConstants.nameType.SYS_CENTER, "更新库存数据异常");
					logger.info("{入库单完成调用库存结果}：" + resultData);
				} catch (Exception e) {
					logger.error("库存调用异常",e);
					if("系统操作异常".equals(e.getMessage())){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,"基础服务出现异常,请稍后再试!");
					}
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200500,e.getMessage());
				}
			}
				
		}
			
		return null;
	}

	
	
	
	
	
	/*-----------------------------------------到货商品详情接口--------------------------------------------------------*/
	
	/**
	 * 查询收货商品详情信息
	 */
	public ReceiveGoodDetailsDto selectGoodDetailsSelective(ReceiveGoodDetails receiveGoodDetails) {
		List<ReceiveGoodDetails> selectGoodDetailsSelective = receiveGoodDetailsMapper.selectInfoBySourceId(receiveGoodDetails.getSourceId());
		ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(receiveGoodDetails.getSourceId());
		if(null == notice){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200409,"云仓系统内没有此入库单号的订单信息");
		}
		for (ReceiveGoodDetails s : selectGoodDetailsSelective) {
			Integer quantity = qualityControlDetailsMapper.selectDeliveryQuantityByWayBillId(s.getWaybillId(), s.getSku());
			if(quantity != null){
				s.setDeliveryQuantity(quantity);
				s.setDifferQuantity(quantity - s.getPlannedQuantity());
			}else{
				s.setDeliveryQuantity(0);
				s.setDifferQuantity(0 - s.getPlannedQuantity());
			}
			
			if(notice.getCustomer() != null){
				s.setSupplier(notice.getCustomerName());
			}else{
				s.setSupplier("无供应商信息");
			}
		}
		Integer number = qualityControlDetailsMapper.selectCountBySourceOrderNumber(receiveGoodDetails.getSourceId());
		if(number != null){
			notice.setDeliveryQuantity(number);
			notice.setDifferQuantity(number - notice.getPlannedQuantity()); 
		}else{
			notice.setDeliveryQuantity(0);
			notice.setDifferQuantity(0 - notice.getPlannedQuantity()); 
		}
		return new ReceiveGoodDetailsDto(notice,selectGoodDetailsSelective);
	}
	
	
	
	/**
	 * 关联查询收货商品详情信息
	 */
	public List<ReceiveGoodDetails> selectGoodDetailsBySourcrId(ReceiveGoodDetails receiveGoodDetails) {
		return receiveGoodDetailsMapper.selectGoodDetailsBySourcrId(receiveGoodDetails);
	}

	
	/**
	 * 修改商品详情信息
	 */
	public Integer updateDetailSelective(ReceiveGoodDetails receiveGoodDetails) {
		return  receiveGoodDetailsMapper.updateSelective(receiveGoodDetails);
	}
	
	
	/**
	 * 生成到货流水号
	 * @return
	 */
	public String setReceiveArrivalNoticeId(){
		String format = DateFormatUtils.format(new Date(), "yyyyMMdd");
		String substring = format.substring(2, format.length());//时间字符串
		
		synchronized (this) {
			//查询当天时间内的最后一位流水号
			List<String> selectArrivalNoticeIdByDate = receiveArrivalNoticeMapper.selectArrivalNoticeIdByDate(substring);
			
			String SerialNumber = "";
			List<Integer> list = new ArrayList<>();
			if(!CollectionUtils.isEmpty(selectArrivalNoticeIdByDate)){
				selectArrivalNoticeIdByDate.stream().forEach(s->{
					String sub = s.substring(s.lastIndexOf(substring) + 6, s.length());
					Integer valueOf = Integer.valueOf(sub);
					list.add(valueOf);
				});
				Integer max = Collections.max(list);
				int newParseInt = 10000 + max + 1;
				SerialNumber = subStr(""+ newParseInt,1);
			}else{
				SerialNumber = "0001";
			}
			return substring + SerialNumber;
		}
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
