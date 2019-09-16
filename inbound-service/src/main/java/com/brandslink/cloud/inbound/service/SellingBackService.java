package com.brandslink.cloud.inbound.service;

import java.util.List;
import java.util.Map;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.SellingBackDto;
import com.brandslink.cloud.inbound.entity.SellingBack;

public interface SellingBackService {

	
	SellingBackDto selectDetailInfo(String wayBillId);
	
	
	Page<SellingBack> findAll(SellingBack sellingBack);
	
	
	void sellingBackSuccess(List<SellingBack> sellingBack);

	
	void insertInfo(SellingBack sellingBack);
	
	
	List<Map<String,Object>> quantityStatistics(List<String> sellingBackId);
	
	
	List<SellingBack> excelExport(List<String> list);
	
}
