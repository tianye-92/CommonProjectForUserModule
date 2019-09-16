package com.brandslink.cloud.inbound.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.entity.PutawayException;
import com.brandslink.cloud.inbound.entity.ReceiveExceptionHanding;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.ReceiveExceptionHandingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "异常上报接口")
@RestController
@RequestMapping("/exHandle")
public class ReceiveExceptionHandingController {
	@Resource
	private ReceiveExceptionHandingService receiveExceptionHandingService;
	
	private final Logger logger = LoggerFactory.getLogger(ReceiveExceptionHandingController.class);
	
	
	/**
	 * 添加异常信息
	 * @param receiveExceptionHanding
	 * @return
	 */
	@ApiOperation(value = "添加异常上报信息", notes = "")
	@PostMapping("/insertExInfo")
	public void insertExInfo(ReceiveExceptionHanding exHandle) {
		try {
			receiveExceptionHandingService.insertExceptionInfo(exHandle);
		} catch (Exception e) {
			logger.error("添加异常上报信息",e);
		}
	}
	   
	
    @ApiOperation(value = "添加上架异常信息")
    @PostMapping("/insertPutawayExInfo")
    public boolean insertPutawayExInfo(PutawayException putawayEx){
    	 return receiveExceptionHandingService.insertPutawayExInfo(putawayEx);
    }
	
	
}
