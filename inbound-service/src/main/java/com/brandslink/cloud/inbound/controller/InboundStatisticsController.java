package com.brandslink.cloud.inbound.controller;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.InboundStandingBookReqDto;
import com.brandslink.cloud.inbound.dto.InboundStandingBookResDto;
import com.brandslink.cloud.inbound.service.InboundStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 入库统计
 * @date 2019/7/8 15:13
 */
@Api(tags = "入库统计基本接口")
@RestController
@RequestMapping("/inbound")
public class InboundStatisticsController {

    @Resource
    private InboundStatisticsService inboundStatisticsService;
    /**
     * @description: 入库统计
     * @params: qcWaybillRequsetDto
     * @return: String
     */
    @ApiOperation(value = "查询入库台账",notes = "支持分页")
    @PostMapping(value = "/getStandingBook")
    public Page<InboundStandingBookResDto> getStandingBook(@RequestBody InboundStandingBookReqDto InboundRequest){
        return inboundStatisticsService.getStandingBook(InboundRequest);
    }


    /**
     * @description: 入库统计
     * @params: qcWaybillRequsetDto
     * @return: String
     */
    @ApiOperation(value = "查询入库台账总数")
    @PostMapping(value = "/getStandingBookCount")
    public InboundStandingBookResDto getStandingBookCount(@RequestBody InboundStandingBookReqDto inboundRequest){
        inboundRequest.setPageNum(null);
        inboundRequest.setPageSize(null);
        return inboundStatisticsService.getStandingBookCount(inboundRequest);
    }
}
