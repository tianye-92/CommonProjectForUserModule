package com.brandslink.cloud.inbound.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.entity.ReceiveSignIn;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.ReceiveSignInService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "签到扫描接口")
@RestController
@RequestMapping("/receiveSignIn")
public class ReceiveSignInController {

	@Autowired
	private ReceiveSignInService receiveSignInService;
	
	private final Logger logger = LoggerFactory.getLogger(ReceiveSignInController.class);
	
	
	/**
	 * 添加签到信息
	 * @param receiveSignIn
	 * @return
	 */
	@ApiOperation(value = "添加签到信息", notes = "")
	@PostMapping("/insertReceiveSignIn")
	public ReceiveSignIn insertReceiveSignIn(ReceiveSignIn receiveSignIn) {
		return receiveSignInService.insertReceiveSignIn(receiveSignIn);
	}
	
	
	@ApiOperation(value = "查询签到列表信息", notes = "")
	@GetMapping("/signInFindAll")
	@RequestRequire(require="page,row",parameter=String.class)
	public Page<ReceiveSignIn> signInFindAll(ReceiveSignIn receiveSignIn,String page,String row){
		Page.builder(page, row);
		return receiveSignInService.findAll(receiveSignIn);
	}
	
	
	
	
	
	
	
	
	
	
}
