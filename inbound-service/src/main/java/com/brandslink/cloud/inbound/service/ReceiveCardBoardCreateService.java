package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.inbound.entity.ReceiveCardBoardCreate;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;

public interface ReceiveCardBoardCreateService {
	
	/**
	 * 查询收货商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	List<ReceiveGoodDetails> selectGoodDetailsSelective(ReceiveGoodDetails receiveGoodDetails);

	/**
	 * 添加卡板创建信息
	 * @param receiveCardBoardCreate
	 * @return
	 */
	Integer insertReceiveCardBoardCreate(List<ReceiveCardBoardCreate> receiveCardBoardCreate);


	/**
	 * 联查商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	ReceiveGoodDetails  selectInfoById(ReceiveGoodDetails receiveGoodDetails);

	
	/**
	 * 生成卡板流水号
	 * @return
	 */
	String setReceiveArrivalNoticeId();
	
	
	
}
