package com.brandslink.cloud.inbound.utils;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

public interface MUtils {
	/**
	 * 通过仓库编码查询仓库名称
	 * @param wareHouseCode
	 * @return
	 */
	String  selectWareHouseName(String wareHouseCode);
	
	
	/**
	 * 查询码表信息
	 * @param dictCode
	 * @param dataCode
	 * @return
	 */
	String getDictionaryDataDetail(String dictCode, String dataCode);

	
	/**
	 * 获取仓库code集合
	 * @param warehouseCodes
	 * @return
	 */
	List<String> getWarehouseCodeList();
	
	
	/**
	 * 查询sku详情
	 * @param skus
	 * @return
	 */
	String getSkuInfoBySku(String[] skus);
	
	
	/**
	 * 查询基础数据货位信息
	 * @param warehouseCode
	 * @param positionCode
	 * @return
	 */
	String getPositionInfo(String warehouseCode,String positionCode);
	
	
	
	
	/**
	 * 通过平台code获取平台名称
	 * @param platformCode
	 * @return
	 */
	String getLogisticsPlatform(String platformCode);
	
	
	
	
}
