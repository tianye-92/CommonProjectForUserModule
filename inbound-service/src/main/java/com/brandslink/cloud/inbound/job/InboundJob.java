package com.brandslink.cloud.inbound.job;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.brandslink.cloud.inbound.mapper.ReceiveCardBoardCreateMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;


//@JobHandler(value="brandslink-inbound-service")
//@Component
//public class InboundJob extends IJobHandler {
//
//	@Resource
//	private ReceiveCardBoardCreateMapper receiveCardBoardCreateMapper;
//	
//	
////	public ReturnT<String> execute(String param) throws Exception {
////		XxlJobLogger.log("入库任务开始执行");
////		receiveCardBoardCreateMapper.deleteInfo("sourceId", "waybillId");
////		return SUCCESS;
////	}
//
//	
//	
//	
//	
//	
//	
//	
//}
