package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.entity.ReceiveSignIn;

public interface ReceiveSignInService {
	
	/**
	 * 添加签到信息
	 * @param receiveSignIn
	 * @return
	 */
	ReceiveSignIn insertReceiveSignIn(ReceiveSignIn receiveSignIn);
	
	
	Page<ReceiveSignIn> findAll(ReceiveSignIn receiveSignIn);
	
	
}
