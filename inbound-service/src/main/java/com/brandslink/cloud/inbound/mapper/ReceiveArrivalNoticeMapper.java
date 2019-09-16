package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;

public interface ReceiveArrivalNoticeMapper extends BaseMapper<ReceiveArrivalNotice> {
	
	/**
	 * 到货通知单信息查询
	 * @param receiveArrivalNotice
	 * @return
	 */
	List<ReceiveArrivalNotice> findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice);
	
	/**
	 * 修改到货通知单
	 * @param receiveArrivalNotice
	 * @return
	 */
	Integer updateSelective(ReceiveArrivalNotice receiveArrivalNotice);
	
	
	/**
	 * 查询当天时间内的流水号
	 * @param dateString
	 * @return
	 */
	List<String> selectArrivalNoticeIdByDate(String dateString);
	
	/**
	 * 查询当天时间内的流水号
	 * @param dateString
	 * @return
	 */
	List<String> selectSourceIdByDate(String dateString);

	/**
	 * @description: 根据来源单号查询到货通知单
	 * @param sourceOrderNumber
	 * @return
	 */
	ReceiveArrivalNotice selectArrivalNoticeBySourceId(String sourceOrderNumber);

	/**
	 * @description: 根据运单号查询到货通知单
	 * @param waybillNumber
	 * @return
	 */
    ReceiveArrivalNotice selectArrivalNoticeByWaybill(String waybillNumber);
    
    
    /**
     * 根据运单号模糊查询到货通知单
     * @param waybillNumber
     * @return
     */
    List<ReceiveArrivalNotice> selectArrivalNoticeByWaybillDim (ReceiveArrivalNoticeInfo receiveArrivalNotice);
    
    
    
    String selectSupplier(String sourceId);
    
    
    /**
     * 查询来源单号是否已存在
     * @param sourceId
     * @return
     */
    Integer selectAmountBySourceId(String sourceId);
    
    
    /**
     * 删除入库订单
     * @param list
     */
    void deleteOrders(List<Integer> list);
    
    
    /**
	 * 客户端到货通知单信息查询
	 * @param receiveArrivalNotice
	 * @return
	 */
	List<ReceiveArrivalNotice> findAllClient(ReceiveArrivalNoticeInfo receiveArrivalNotice);
    
    
	ReceiveArrivalNotice selectByPrimaryKey(Integer id);
	
}