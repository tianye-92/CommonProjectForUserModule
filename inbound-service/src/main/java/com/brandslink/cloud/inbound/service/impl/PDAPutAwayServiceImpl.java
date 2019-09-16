package com.brandslink.cloud.inbound.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.PutawayShelfDto;
import com.brandslink.cloud.inbound.entity.BoxInfo;
import com.brandslink.cloud.inbound.entity.PutawayList;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.BoxInfoMapper;
import com.brandslink.cloud.inbound.mapper.PutawayDetailsMapper;
import com.brandslink.cloud.inbound.mapper.PutawayListMapper;
import com.brandslink.cloud.inbound.mapper.PutawayShelfMapper;
import com.brandslink.cloud.inbound.mapper.QualityBoxPutawayMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.SellingBackMapper;
import com.brandslink.cloud.inbound.rabbitmq.InboundStatisticsSender;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.PDAPutAwayService;
import com.brandslink.cloud.inbound.utils.MUtils;

import jodd.util.StringUtil;

@Service
public class PDAPutAwayServiceImpl implements PDAPutAwayService{

	@Resource
	private PutawayShelfMapper putawayShelfMapper;
	@Resource
	private PutawayDetailsMapper putawayDetailsMapper;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private InboundStatisticsSender inboundStatisticsSender;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private QualityBoxPutawayMapper qualityBoxPutawayMapper;
	@Resource
	private BoxInfoMapper boxInfoMapper;
	@Resource
	private SellingBackMapper sellingBackMapper;
	@Resource
	private PutawayListMapper putawayListMapper;
	@Resource
	private MUtils mUtils;
	private final Logger logger = LoggerFactory.getLogger(PDAPutAwayServiceImpl.class);
	
	/**
	 * 根据质检箱号查询楼层信息
	 * @param sealBoxSerialNumber
	 * @return
	 */
	public String selectFloorInfo(String sealBoxSerialNumber,String warehouseCode) {
		while(StringUtil.isBlank(sealBoxSerialNumber) || StringUtil.isBlank(warehouseCode)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入参数不能为空，请检查！");
		}
		//验证该质检箱是否存在未楼层分理的质检数据
		Integer amount = qualityBoxPutawayMapper.checkAmountByQualityBox(sealBoxSerialNumber, warehouseCode);
		if(amount <= 0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"质检箱:"+sealBoxSerialNumber+"无未楼层分理的货物，请检查！");
		
		String floor = qualityBoxPutawayMapper.selectSkuFloorByQuBox(sealBoxSerialNumber, warehouseCode);
		if(StringUtil.isBlank(floor)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"质检箱:"+sealBoxSerialNumber+"无未楼层分理的货物，请检查！");
		}
		return floor;
	}

	
	/**
	 * 绑定楼层箱
	 */
	public Integer updateInfoSelective(String sealBoxSerialNumber,String floorBoxNumber,String floor,String warehouseCode) {
		if(StringUtil.isBlank(sealBoxSerialNumber) || StringUtil.isBlank(floorBoxNumber) || StringUtil.isBlank(floor)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"参数不能为空,请检查!");
		}
		//查询本地箱子信息
		BoxInfo selectBoxInfoByCode = boxInfoMapper.selectBoxInfoByCode(warehouseCode, floorBoxNumber);
		//查询基础服务箱号信息
		String containerByCode = remoteCenterService.getContainerByCode(warehouseCode, floorBoxNumber);
		String result = Utils.returnRemoteResultDataString(containerByCode, "查询中心服务楼层箱异常");
		if(result == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有此楼层箱的信息,请检查!");
		}
		logger.info("[查询中心服务箱子]"+result);
		JSONObject parseObject = JSONObject.parseObject(result);
		String containerCode = parseObject.getString("containerCode");
		String warehouseCodeCenter = parseObject.getString("warehouseCode");
		Integer workState = parseObject.getInteger("workState");
		Integer usageState = parseObject.getInteger("usageState");
		
		List<String> checkQualityBoxBind = qualityBoxPutawayMapper.checkQualityBoxBind(warehouseCode);
		logger.info("[]"+checkQualityBoxBind.toString());
		if(checkQualityBoxBind.contains(floorBoxNumber)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"亲 该箱正在使用,不可作为楼层箱使用，请检查!");
		}
		
		if(selectBoxInfoByCode == null){//本地无箱子信息  查询中心服务
			if(usageState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为禁用状态,请检查!");
			}
			if(workState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为已占用状态,请检查!");
			}
			BoxInfo boxInfo = new BoxInfo();
			boxInfo.setBoxId(floorBoxNumber);
			boxInfo.setWarehouse(warehouseCode);
			boxInfo.setFloor(floor);
			Integer insertSelective = boxInfoMapper.insertInfo(boxInfo);
			//将容器表中的楼层箱状态改为占用
			this.updateContainerStatus(warehouseCode, floorBoxNumber, "0");
		}else{
			if(usageState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为禁用状态,请检查!");
			}
			
			if(("0".equals(selectBoxInfoByCode.getListId()) == false) && (null != selectBoxInfoByCode.getListId()) ){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱已被使用,请检查!");
			}else if("0".equals(selectBoxInfoByCode.getFloor())){
				BoxInfo boxInfo = new BoxInfo();
				boxInfo.setFloor(floor);
				boxInfo.setBoxId(floorBoxNumber);
				boxInfo.setWarehouse(warehouseCode);
				boxInfoMapper.updateByPrimaryKeySelective(boxInfo);
				//将容器表中的楼层箱状态改为占用
				this.updateContainerStatus(warehouseCode, floorBoxNumber, "0");
			}else if(!floor.equals(selectBoxInfoByCode.getFloor())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"质检箱"+sealBoxSerialNumber+"上架楼层为"+floor+"，与楼层箱当前分理楼层不一致，请检查！");
			}
		}
		
		//绑定楼层箱
		Integer bindFloorBox = qualityBoxPutawayMapper.bindFloorBox(floorBoxNumber,sealBoxSerialNumber,warehouseCode);
		
		//将容器表中的质检箱状态改为释放
		this.updateContainerStatus(warehouseCode, sealBoxSerialNumber, "1");
		return bindFloorBox;
	}



	/**
	 * 查询sku的货列信息
	 */
	public String  selectSkuLocationCode(String sku,String floorBoxNumber,String warehouseCode) {
		if(StringUtil.isBlank(sku)){//校验操作
			Integer checkAmountByListBox = qualityBoxPutawayMapper.checkAmountByListBox(floorBoxNumber, warehouseCode);
			if(checkAmountByListBox > 0){
				return "1";
			}else{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"楼层箱:"+floorBoxNumber+",不存在未货列分理的货物,请检查!");
			}
		}
		List<String> selectSkuLocationInfo = qualityBoxPutawayMapper.selectListInfoByFloorBox(floorBoxNumber,sku,warehouseCode);
		List<String> collect = selectSkuLocationInfo.stream().sorted(StringUtils::compare).collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(collect)){
			String code = remoteCenterService.queryByCodeAndWarehouseCode(warehouseCode, collect.get(0));
			String resultCode = Utils.returnRemoteResultDataString(code, "查询中心服务货位异常");
			if(StringUtil.isBlank(resultCode)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有此货列的信息,请检查!");
			}
			JSONObject obj = JSONObject.parseObject(resultCode);
			String rowCode = obj.getString("rowCode");
			return  collect.get(0) + "&&" + rowCode;
		}
		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询不到商品上架位置信息或SKU:"+sku+"不属于楼层箱号"+floorBoxNumber+"，请检查！");
	}

	
	
	/**
	 * 通过sku查询上架详情信息
	 */
	public QualityBoxPutaway selectDetailInfoBySku(QualityBoxPutaway QualityBoxPutaway) {
		List<QualityBoxPutaway> selectSkuDetailInfo = qualityBoxPutawayMapper.selectSkuDetailInfo(QualityBoxPutaway);
		if(CollectionUtils.isEmpty(selectSkuDetailInfo))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"sku"+QualityBoxPutaway.getSku()+"不存在上架明细中，请检查！");
		List<String> list = new ArrayList<>();
		for (QualityBoxPutaway qualityBoxPutaway2 : selectSkuDetailInfo) {
			list.add(qualityBoxPutaway2.getRecommendedLocationCode());
		}
		List<String> collect = list.stream().sorted(StringUtils::compare).collect(Collectors.toList());
		QualityBoxPutaway.setRecommendedLocationCode(collect.get(0));
		List<QualityBoxPutaway>  selectSkuDetailInfoSecond = qualityBoxPutawayMapper.selectSkuDetailInfoSecond(QualityBoxPutaway);
		return selectSkuDetailInfoSecond.get(0);
	}
	
	

	/**
	 * 绑定货列号
	 */
	public Integer updatePutawayShelfInfoSelective(QualityBoxPutaway qualityBoxPutaway) {
		String warehouseCode = qualityBoxPutaway.getWarehouseCode();
		
		BoxInfo selectBoxInfoByCode = boxInfoMapper.selectBoxInfoByCode(warehouseCode, qualityBoxPutaway.getListBoxNumber());
		
		//查询基础服务箱号信息
		String containerByCode = remoteCenterService.getContainerByCode(warehouseCode, qualityBoxPutaway.getListBoxNumber());
		String result = Utils.returnRemoteResultDataString(containerByCode, "查询中心服务楼层箱异常");
		if(result == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有此箱的信息,请检查!");
		}
		logger.info("[查询中心服务箱子]"+result);
		JSONObject parseObject = JSONObject.parseObject(result);
		String containerCode = parseObject.getString("containerCode");
		String warehouseCodeCenter = parseObject.getString("warehouseCode");
		Integer workState = parseObject.getInteger("workState");
		Integer usageState = parseObject.getInteger("usageState");
		
		
		String code = remoteCenterService.queryByCodeAndWarehouseCode(warehouseCode, qualityBoxPutaway.getRecommendedLocationCode());
		String resultCode = Utils.returnRemoteResultDataString(code, "查询中心服务货位异常");
		if(StringUtil.isBlank(resultCode)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有此货列的信息,请检查!");
		}
		JSONObject obj = JSONObject.parseObject(resultCode);
		String rowCode = obj.getString("rowCode");
		
		if(selectBoxInfoByCode == null){//本地无箱子信息  查询中心服务
			if(usageState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为为禁用状态,请检查!");
			}
			if(workState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为已占用状态,请检查!");
			}
			BoxInfo boxInfo = new BoxInfo();
			boxInfo.setBoxId(qualityBoxPutaway.getListBoxNumber());
			boxInfo.setWarehouse(warehouseCode);
			boxInfo.setListId(rowCode);
			boxInfoMapper.insertInfo(boxInfo);
			//将容器表中的货列箱状态改为占用
			this.updateContainerStatus(warehouseCode, qualityBoxPutaway.getListBoxNumber(), "0");
		}else{
			if(usageState == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱为为禁用状态,请检查!");
			}
			
			if(("0".equals(selectBoxInfoByCode.getFloor()) == false) && (null != selectBoxInfoByCode.getFloor()) ){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该箱已被使用,请检查!");
		    }else if("0".equals(selectBoxInfoByCode.getListId())){
				BoxInfo boxInfo = new BoxInfo();
				boxInfo.setListId(rowCode);
				boxInfo.setBoxId(qualityBoxPutaway.getListBoxNumber());
				boxInfo.setWarehouse(warehouseCode);
				boxInfoMapper.updateByPrimaryKeySelective(boxInfo);
				//将容器表中的货列箱状态改为占用
				this.updateContainerStatus(warehouseCode, qualityBoxPutaway.getListBoxNumber(), "0");
			}else if(!rowCode.equals(selectBoxInfoByCode.getListId())){		
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该sku的上架货列与当前货列箱的分理货列不一致,请检查！");
			}
		}
		
		Integer bindListBox = qualityBoxPutawayMapper.bindListBox(qualityBoxPutaway);
		
		//校验楼层箱中的商品是否已经分理完
		Integer checkFloorBoxAmount = qualityBoxPutawayMapper.checkFloorBoxAmount(qualityBoxPutaway.getFloorBoxNumber(), warehouseCode);
		logger.info("校验楼层箱中的商品 " + checkFloorBoxAmount.toString());
		if(checkFloorBoxAmount <= 0){
			//将箱号表中的楼层信息置空
			BoxInfo box = new BoxInfo();
			box.setBoxId(qualityBoxPutaway.getFloorBoxNumber());
			box.setFloor("0");
			box.setListId("0");
			box.setWarehouse(warehouseCode);
			boxInfoMapper.updateByPrimaryKeySelective(box);
			//将容器表中的楼层箱状态改为释放
			this.updateContainerStatus(warehouseCode, qualityBoxPutaway.getFloorBoxNumber(), "1");
		}
		return bindListBox;
	}


	/**
	 * 通过货列箱号查询上架信息
	 */
	public List<PutawayShelfDto> selectSkuInfoByListBoxId(String listBoxNumber,String warehouseCode) {
		return qualityBoxPutawayMapper.selectSkuInfoByListBoxId(listBoxNumber,warehouseCode);
	}



	/**
	 * 上架确认
	 */
	public boolean updatePutawayShelfConfirm(QualityBoxPutaway qualityBoxPutaway) {
		if("3".equals(qualityBoxPutaway.getType())){
			//查询此货位是否存在
			String positionInfo = mUtils.getPositionInfo(qualityBoxPutaway.getWarehouseCode(), qualityBoxPutaway.getActualLocationCode());
			
		    //查询此货位在库存表中的信息
			Map<String, Object> param = new HashMap<>();
			param.put("positionCode", qualityBoxPutaway.getActualLocationCode());
			param.put("warehouseCode", qualityBoxPutaway.getWarehouseCode());
			logger.info("[通过货位编码查询货位的库存信息参数]"+param);
			String shelfInfoBySingleCondition = null;
			try {
				shelfInfoBySingleCondition = remoteCenterService.getShelfInfoBySingleCondition(param);
			} catch (Exception e) {
				logger.error("调用中心服务通过货位编码查询货位的库存信息失败",e);
				if("系统操作异常".equals(e.getMessage())){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
				}
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
			}
			String result = Utils.returnRemoteResultDataString(shelfInfoBySingleCondition, "通过货位编码查询货位的库存信息异常");
			if(null == result){
				return true;
			}
			logger.info("[通过货位编码查询货位的库存信息结果]"+result);
			JSONObject parseObject = JSONObject.parseObject(result);
			JSONObject object = (JSONObject)parseObject.get("pageInfo");
			JSONArray jsonArray = object.getJSONArray("list");
			if(null == jsonArray || jsonArray.size() == 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询不到此货位的库存信息,请检查!");
			}else{
				List<JSONObject> javaList = jsonArray.toJavaList(JSONObject.class);
				boolean flag = true;
				for (JSONObject jsonObject : javaList) {
					Integer amount = jsonObject.getInteger("okShelfLocation");
					if(amount > 0){
						flag = false;
					}
				}
				if(flag == false){//不是空货位
					ArrayList<String> list = new ArrayList<>();
					for (JSONObject jsonObject : javaList) {
						String  goodsSku = jsonObject.getString("goodsSku");
						if(goodsSku != null){
							list.add(goodsSku);
						}
					}
					if(list.contains(qualityBoxPutaway.getSku())){
						return true;
					}else{
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该货位不可强制上架!");
					}
				}else{//是空货位
					return true;
				}
			}
			
		}
		
		
		ReceiveArrivalNotice arrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(qualityBoxPutaway.getWaybillNumber());
		Integer putawayShelfSuccess = null;
		try {
			//更新pda上架单的实际上架位置   实际数量
			qualityBoxPutaway.setStatus(1);
			qualityBoxPutaway.setPutawayTime(new Date());
			qualityBoxPutaway.setOperator(getUserDetailInfoUtil.getUserDetailInfo().getName());
			qualityBoxPutaway.setOperatorId(getUserDetailInfoUtil.getUserDetailInfo().getId());
			putawayShelfSuccess = qualityBoxPutawayMapper.putawayShelfSuccess(qualityBoxPutaway);
		} catch (Exception e) {
			logger.error("更新PDA上架单异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"更新PDA上架单异常");
		}
		
		
		//校验楼层箱中的商品是否已经分理完
		Integer checkListBoxAmount = qualityBoxPutawayMapper.checkListBoxAmount(qualityBoxPutaway.getListBoxNumber(), qualityBoxPutaway.getWarehouseCode());
		if(checkListBoxAmount <= 0){
			//将箱号表中的楼层信息置空
			BoxInfo box = new BoxInfo();
			box.setBoxId(qualityBoxPutaway.getFloorBoxNumber());
			box.setListId("0");
			box.setFloor("0");
			box.setWarehouse(qualityBoxPutaway.getWarehouseCode());
			boxInfoMapper.updateByPrimaryKeySelective(box);
			//将容器表中的货列箱状态改为释放
			this.updateContainerStatus(qualityBoxPutaway.getWarehouseCode(), qualityBoxPutaway.getListBoxNumber(), "1");
		}
		
		
		List<Integer> status = qualityBoxPutawayMapper.selectPutawayStatusByPutawayId(qualityBoxPutaway.getPutawayId(),qualityBoxPutaway.getWarehouseCode());
		if(status.contains(0) == false){
			PutawayList putawayList = new PutawayList();
			putawayList.setFinishedTime(new Date());
			putawayList.setPutawayId(qualityBoxPutaway.getPutawayId());
			putawayList.setWarehouseCode(qualityBoxPutaway.getWarehouseCode());
			putawayListMapper.updateFinishTime(putawayList);
		}
		
		if(null != arrivalNotice){
			//添加库存
			Map<String,Object> param = new HashMap();
			param.put("businessType", "put_type/" + arrivalNotice.getSourceType());
			param.put("goodsSku", qualityBoxPutaway.getSku());
			param.put("skuNumbers", qualityBoxPutaway.getActualPutawayNumber());
			param.put("sourceOrderNumber", arrivalNotice.getSourceId());
			param.put("updateBy", getUserDetailInfoUtil.getUserDetailInfo().getName());
			param.put("warehouseCode", arrivalNotice.getWarehouse());
			if(qualityBoxPutaway.getRecommendedLocationCode().equals(qualityBoxPutaway.getActualLocationCode())){
				param.put("shelfLocationId", getGoodLocationId(arrivalNotice.getWarehouse(),qualityBoxPutaway.getRecommendedLocationCode()));
			}else{
				param.put("shelfLocationId", getGoodLocationId(arrivalNotice.getWarehouse(),qualityBoxPutaway.getRecommendedLocationCode()));
				param.put("currentShelfLocationId", getGoodLocationId(arrivalNotice.getWarehouse(),qualityBoxPutaway.getActualLocationCode()));
			}
			logger.info("【调用上架库存参数】" + param.toString());
			String affirmPutawayUpdateInventory = null;
			try {
				affirmPutawayUpdateInventory = remoteCenterService.affirmPutawayUpdateInventory(param);
				logger.info("【调用上架库存结果】" + affirmPutawayUpdateInventory);
			} catch (Exception e) {
				logger.error("上架修改库存异常-->中心服务异常",e);
				if("系统操作异常".equals(e.getMessage())){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
				}
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
			}
			JSONObject parseObject = JSONObject.parseObject(affirmPutawayUpdateInventory);
			if(parseObject.getBoolean("success") == false){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"更新库存失败,暂无法上架，请稍后再试！");
			}
			
			
			//入库统计消息发送
			InboundWorkloadInfo info = new InboundWorkloadInfo();
			info.setWarehouseCode(arrivalNotice.getWarehouse());
			info.setCustomer(arrivalNotice.getCustomerName());
			info.setPutType(Integer.valueOf(arrivalNotice.getSourceType()));
			info.setOperationType(3);
			info.setWorkingPeople(getUserDetailInfoUtil.getUserDetailInfo().getName());
			info.setPutawayTimeNum(1);
			info.setWaybillId(qualityBoxPutaway.getWaybillNumber());
			info.setPutawayNum(qualityBoxPutaway.getActualPutawayNumber());
			info.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			try {
				inboundStatisticsSender.InboundStatisticsSend(info);
			} catch (Exception e) {
				logger.error("上架消息发送异常",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"上架消息发送异常,请稍后再试!");
			}
			
			
		}else{
			//添加库存
			SellingBack sellingBack = sellingBackMapper.selectSellingBackIdByWaybill(qualityBoxPutaway.getWaybillNumber());
			Map<String,Object> param = new HashMap();
			param.put("businessType", "put_type/2");
			param.put("goodsSku", qualityBoxPutaway.getSku());
			param.put("skuNumbers", qualityBoxPutaway.getActualPutawayNumber());
			param.put("sourceOrderNumber", sellingBack.getSourceId());
			param.put("updateBy", getUserDetailInfoUtil.getUserDetailInfo().getName());
			param.put("warehouseCode", sellingBack.getWarehouse());
			if(qualityBoxPutaway.getRecommendedLocationCode().equals(qualityBoxPutaway.getActualLocationCode())){
				param.put("shelfLocationId", getGoodLocationId(sellingBack.getWarehouse(),qualityBoxPutaway.getRecommendedLocationCode()));
			}else{
				param.put("shelfLocationId", getGoodLocationId(sellingBack.getWarehouse(),qualityBoxPutaway.getRecommendedLocationCode()));
				param.put("currentShelfLocationId", getGoodLocationId(sellingBack.getWarehouse(),qualityBoxPutaway.getActualLocationCode()));
			}
			logger.info("【调用上架库存参数】" + param.toString());
			String affirmPutawayUpdateInventory = null;
			try {
				affirmPutawayUpdateInventory = remoteCenterService.affirmPutawayUpdateInventory(param);
				logger.info("【调用上架库存结果】" + affirmPutawayUpdateInventory);
			} catch (Exception e) {
				logger.error("上架修改库存异常-->中心服务异常",e);
				if("系统操作异常".equals(e.getMessage())){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
				}
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
			}
			JSONObject parseObject = JSONObject.parseObject(affirmPutawayUpdateInventory);
			if(parseObject.getBoolean("success") == false){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"更新库存失败,暂无法上架，请稍后再试！");
			}
			
			//入库统计消息发送
			InboundWorkloadInfo info = new InboundWorkloadInfo();
			info.setWarehouseCode(sellingBack.getWarehouse());
			info.setCustomer(sellingBack.getCustomer());
			info.setPutType(2);
			info.setOperationType(3);
			info.setWorkingPeople(getUserDetailInfoUtil.getUserDetailInfo().getName());
			info.setPutawayTimeNum(1);
			info.setWaybillId(qualityBoxPutaway.getWaybillNumber());
			info.setPutawayNum(qualityBoxPutaway.getActualPutawayNumber());
			info.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			try {
				inboundStatisticsSender.InboundStatisticsSend(info);
			} catch (Exception e) {
				logger.error("上架消息发送异常",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"上架消息发送异常,请稍后再试!");
			}
		}
		
		//更新上架数
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> paramUnpack = new HashMap<>();
        paramUnpack.put("warehouseCode", qualityBoxPutaway.getWarehouseCode());
        paramUnpack.put("operationType", 2);
        paramUnpack.put("number", qualityBoxPutaway.getActualPutawayNumber());
        logger.info("【更新上架数参数】" + paramUnpack.toString());
        paramList.add(paramUnpack);
        try {
        	String insertInventoryData = remoteCenterService.insertInventoryData(paramList);
        	logger.info("【更新上架数结果】" + insertInventoryData);
		} catch (Exception e) {
			logger.error("调用中心服务更新上架数失败",e);
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		
		if(putawayShelfSuccess >= 1){
			return true;
		}else {
			return false;
		}
	}

	
	public Integer getGoodLocationId(String warehouseCode,String Location){
		String goodCode = null;
		try {
			goodCode = remoteCenterService.queryByCodeAndWarehouseCode(warehouseCode, Location);
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String returnRemoteResultDataString = Utils.returnRemoteResultDataString(goodCode, "查询货位id转换异常");
		if(null == returnRemoteResultDataString){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的货位在系统中没有信息,请检查!");
		}else{
			JSONObject obj = JSONObject.parseObject(returnRemoteResultDataString);
			Integer id = obj.getInteger("id");
			return id;
		}
	}
	
	public  void updateContainerStatus(String warehouseCode,String containerCode,String status){
		UserDetailInfo userDetailInfo = getUserDetailInfoUtil.getUserDetailInfo();
		Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("warehouseCode", warehouseCode);
        paramMap.put("containerCode", containerCode);
        paramMap.put("usageBy", userDetailInfo.getName());
//        paramMap.put("usageBy", "zc");
        paramMap.put("usageTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paramMap.put("workState", status);
        //更新容器状态
        String updateResult = Utils.getResultData(remoteCenterService.altWorkState(paramMap), SystemConstants.nameType.SYS_CENTER,"更新容器状态异常");
        if("false".equals(updateResult)){
        	logger.info("更新基础数据容器号:【{}】状态失败");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础数据更新容器状态失败,请稍后再试!");
        }
	}
	
}
