package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.SellingBack;

public interface SellingBackMapper extends BaseMapper<SellingBack> {
	
	List<SellingBack> findAll(SellingBack sellingBack);
	
	
	void updateSelective(SellingBack sellingBack);
	
	
	List<String> selectSellingBackIdByDate(String dateString);


	/**
	 * @description: 根据运单查询销退单信息
	 * @param waybillNumber
	 * @return
	 */
	SellingBack selectSellingBackIdByWaybill(String waybillNumber);
	
	
	/**
	 * excel导出
	 * @param list
	 * @return
	 */
	List<SellingBack> excelExport(List<String> list);
	
	
	Integer checkCount(String waybillId);

	/**
	 * @description: 查询销退单列表
	 * @param sourceNumber
	 * @return
	 */
	List<SellingBack> selectSellListBySourceId(String sourceNumber);
}