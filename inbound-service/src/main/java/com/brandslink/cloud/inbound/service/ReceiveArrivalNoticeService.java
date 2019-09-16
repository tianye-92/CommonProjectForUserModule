package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsDto;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsInfo;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;

public interface ReceiveArrivalNoticeService {
	
	/*-----------------------------------------到货通知接口--------------------------------------------------------*/
	
	/**
	 * 到货通知单信息查询
	 * @param receiveArrivalNotice
	 * @return
	 */
	Page<ReceiveArrivalNotice> findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice,String page,String row);
	
	
	/**
	 * 根据运单号模糊查询到货通知单
	 * @param wayBillId
	 * @return
	 */
	Page<ReceiveArrivalNotice> selectArrivalNoticeByWaybillDim(String wayBillId,String page,String row);
	
	
	/**
	 * 修改到货通知单
	 * @param receiveArrivalNotice
	 * @return
	 */
	Integer updateSelective(List<ReceiveArrivalNotice> receiveArrivalNotice);

	/**
	 * 添加到货通知单信息
	 * @param receiveArrivalNotice
	 * @return
	 */
	boolean insertReceiveArrivalNotice(ReceiveArrivalNotice notice);
	
	
	
	
	
	
	/*-----------------------------------------到货商品详情接口--------------------------------------------------------*/
	
	/**
	 * 查询收货商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	ReceiveGoodDetailsDto selectGoodDetailsSelective(ReceiveGoodDetails receiveGoodDetails);
	
	
	/**
	 * 关联查询商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	List<ReceiveGoodDetails> selectGoodDetailsBySourcrId(ReceiveGoodDetails receiveGoodDetails);
	
	/**
	 * 修改到货通知单
	 * @param receiveArrivalNotice
	 * @return
	 */
	Integer updateDetailSelective(ReceiveGoodDetails receiveGoodDetails);
	
}
