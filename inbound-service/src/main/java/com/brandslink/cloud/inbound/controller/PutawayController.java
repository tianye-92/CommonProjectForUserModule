package com.brandslink.cloud.inbound.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.brandslink.cloud.inbound.entity.PutawayException;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.inbound.dto.PutawayDetailsResDto;
import com.brandslink.cloud.inbound.dto.PutawayListReqDto;
import com.brandslink.cloud.inbound.entity.PutawayList;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.PutawayListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 上架操作基本接口
 * @date 2019/6/14 10:26
 */
@Api(tags = "上架操作基本接口")
@RestController
@RequestMapping("/putaway")
public class PutawayController {

    @Resource
    private PutawayListService putawayListService;

    /**
     * @description: 查询上架单（分页）
     * @param putawayListReqDto
     * @return
     */
    @ApiOperation(value = "查询上架单（分页）")
    @PostMapping(value = "/getList")
    public Page<List<PutawayList>> getPutawayList(@RequestBody PutawayListReqDto putawayListReqDto){
         return putawayListService.selectByPutawayObj(putawayListReqDto);
    }

    /**
     * @description: 查询上架详情
     * @return
     */
    @ApiOperation(value = "查询上架详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "putawayId", value = "上架单号", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/list/details")
    public PutawayDetailsResDto getPutawayListDetails(@RequestParam(value = "putawayId") String putawayId){
        return putawayListService.selectPutawayDetailsByPutawayId(putawayId);
    }

    /**
     * @description: 查询上架异常明细
     * @return
     */
    @ApiOperation(value = "查询上架异常明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "putawayId", value = "上架单号", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/getException")
    public List<PutawayException> getPutawayExceptionDetails(@RequestParam(value = "putawayId") String putawayId){
        return putawayListService.getExceptionDetailsByPutawayId(putawayId);
    }

    
}
