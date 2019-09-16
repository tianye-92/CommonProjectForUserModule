package com.brandslink.cloud.inbound.service;

import com.brandslink.cloud.inbound.entity.PutawayException;
import com.brandslink.cloud.inbound.entity.ReceiveExceptionHanding;

public interface ReceiveExceptionHandingService {

	void insertExceptionInfo(ReceiveExceptionHanding receiveExceptionHanding);
	
	boolean insertPutawayExInfo(PutawayException putawayEx);
	
}
