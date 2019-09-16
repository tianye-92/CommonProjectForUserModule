package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.ReceiveSignIn;

public interface ReceiveSignInMapper extends BaseMapper<ReceiveSignIn> {
	
	List<ReceiveSignIn> findAll(ReceiveSignIn receiveSignIn);
	
	
	ReceiveSignIn selectByPrimaryKey(Integer id);
	
	
	Integer checkWayBillId(String waybillId);
	
}