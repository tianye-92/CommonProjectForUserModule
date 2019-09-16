package com.brandslink.cloud.inbound.mapper;

import java.util.List;
import java.util.Map;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.SellingBackDetailDto;
import com.brandslink.cloud.inbound.entity.SellingBackDetail;

public interface SellingBackDetailMapper extends BaseMapper<SellingBackDetail> {

    /**
     * @description: 根据销退运单号查询货物详情
     * @param waybillNum
     * @return
     */
    List<SellingBackDetailDto> getSellingBackDetailsByWaybillNum(String waybillNum);

    /**
     * @description: 根据来源单号查询销退货物详情
     * @param sourceNumber
     * @return
     */
    List<SellingBackDetailDto> getSellingBackBySourceNumber(String sourceNumber);

	List<SellingBackDetail> selectInfo(String wayBillId);

	void updateSelective(SellingBackDetail sellingBackDetail);

	
	/**
	 * 销退数量统计
	 * @param wayBillId
	 * @return
	 */
	Integer quantityStatistics(String sellingBackId);

	/**
	 * @description: 根据运单号查询运单计划总数量
	 * @param waybillNumber
	 * @return
	 */
    Integer selectCountByWaybillId(String waybillNumber);

	/**
	 * @description: 根据来源单查询销退单信息
	 * @param sourceNumber
	 * @return
	 */
	List<SellingBackDetailDto> selectBySourceId(String sourceNumber);

	/**
	 * @description: 根据运单查询销退单信息
	 * @param waybillNum
	 * @return
	 */
	List<SellingBackDetailDto> selectByWaybillId(String waybillNum);
	
	
	/**
	 * 查询销退单号下的sku
	 * @param sellingBackId
	 * @return
	 */
	List<SellingBackDetail> selectInfoBySellingBackId(String sellingBackId);
	
	
	
}