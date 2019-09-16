package com.brandslink.cloud.inbound.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.remote.RemoteLogisticsService;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class MUtilsImpl implements MUtils{

	private final Logger logger = LoggerFactory.getLogger(MUtilsImpl.class);
	@Autowired
	public RemoteCenterService remoteCenterService;
	@Autowired
	public RemoteLogisticsService remoteLogisticsService;
	@Autowired
	public GetUserDetailInfoUtil getUserDetailInfoUtil;
	
	/**
	 * 通过仓库编码查询仓库名称
	 * @param wareHouseCode
	 * @return
	 */
	@Override
    public String selectWareHouseName(String wareHouseCode){
		String warehouseDetail = null;
		String wareHouseName = null;
		try {
		    warehouseDetail = remoteCenterService.getWarehouseByCode(wareHouseCode);
		} catch (Exception e) {
			logger.error("调用中心服务查询仓库信息失败",e);
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String returnString = Utils.returnRemoteResultDataString(warehouseDetail, "查询仓库信息异常");
		if(returnString == null){
			wareHouseName = "无仓库名称";
		}else{
			JSONObject parseObject = JSONObject.parseObject(returnString);
			wareHouseName = parseObject.getString("warehouseName");
		}
		return wareHouseName;
	}

	
	/**
	 * 查询码表信息
	 */
	@Override
    public String getDictionaryDataDetail(String dictCode, String dataCode) {
		String dataName = null;
		try {
			dataName = remoteCenterService.getDictionaryDataDetail(dictCode, dataCode);
		} catch (Exception e) {
			logger.error("调用中心服务查询仓库信息失败",e);
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String obj = Utils.returnRemoteResultDataString(dataName,"查询销退状态异常");
		if(null != obj){
			JSONObject parseObject = JSONObject.parseObject(obj);
			String name = (String)parseObject.get("dataName");
			return name;
		}else{
			return null;
		}
	}


	/**
	 * 获取仓库code集合
	 */
	@Override
    public List<String> getWarehouseCodeList() {
		String warehouseCode = getUserDetailInfoUtil.getUserDetailInfo().getWarehouseCode();
		if(StringUtils.isBlank(warehouseCode)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"当前用户没有仓库查看权限，请检查后再试！");
		}
		String[] split = warehouseCode.split(",");
		List<String> arrayList = new ArrayList<>();
		Collections.addAll(arrayList, split);
		return arrayList;
	}


	
	/**
	 * 查询sku详情
	 */
	@Override
    public String getSkuInfoBySku(String[] skus) {
		String skuInfo = null;
		try {
			skuInfo = remoteCenterService.getSkuInfoBySku(skus);
		} catch (Exception e) {
			logger.error("调用中心服务查询sku信息失败",e);
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String obj = Utils.returnRemoteResultDataString(skuInfo,"调用中心服务查询sku信息失败");
		return Optional.ofNullable(obj).orElseThrow(()->new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有sku"+skus.toString()+"的信息，请检查后再试！"));
	}


	/**
	 * 查询基础数据货位信息
	 */
	@Override
    public String getPositionInfo(String warehouseCode, String positionCode) {
		String queryByCodeAndWarehouseCode = null;
		try {
			queryByCodeAndWarehouseCode = remoteCenterService.queryByCodeAndWarehouseCode(warehouseCode, positionCode);
		} catch (Exception e) {
			logger.error("调用中心服务查询货位信息失败",e);
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础服务出现异常,请稍后再试！");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		String obj = Utils.returnRemoteResultDataString(queryByCodeAndWarehouseCode,"调用中心服务查询货位信息失败");
		return Optional.ofNullable(obj).orElseThrow(()->new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统中没有此货位的信息，请检查后再试！"));
	}


	/**
	 * 通过平台code获取平台名称
	 */
	public String getLogisticsPlatform(String platformCode) {
		Map<String, Object> hashMap = new HashMap<String,Object>();
		hashMap.put("platformCode", platformCode);
		hashMap.put("page", 1);
		hashMap.put("row", 5);
		String result = null;
		try {
			String selectMethod = remoteLogisticsService.selectPlatform(hashMap);
		    result = Utils.returnRemoteResultDataString(selectMethod, "调用物流系统查询物流方式异常");
		} catch (Exception e) {
			if("系统操作异常".equals(e.getMessage())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"出库系统异常");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
		if(null != result){
			JSONObject parseObject = JSONObject.parseObject(result);
			JSONObject object = (JSONObject)parseObject.get("pageInfo");
			JSONArray jsonArray = object.getJSONArray("list");
			if(!CollectionUtils.isEmpty(jsonArray)){
				JSONObject object2 = (JSONObject)jsonArray.get(0);
				return object2.getString("platformCn");
			}
		}
		return null;
	}
	
	
	
}
