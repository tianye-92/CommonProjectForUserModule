package com.brandslink.cloud.inbound.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.utils.RedisUtils;


/**
 * 通用控制层
 * */
@RestController
public class CommonController extends BaseController {

    @Value("${test-1}")
    private String test1;

    @Autowired
    protected RedisUtils redisUtils;

    @Autowired
    protected HttpServletRequest request;
    
    @Autowired
    protected  HttpServletResponse response;
    
    
    
    
    
    //@ApolloConfig
    //private Config config;

    






}
