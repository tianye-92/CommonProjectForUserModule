package com.brandslink.cloud.inbound.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.InboundStandingBookReqDto;
import com.brandslink.cloud.inbound.dto.InboundStandingBookResDto;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 入库统计业务层
 * @date 2019/7/8 15:18
 */
public interface InboundStatisticsService {

    /**
     * @description: 查询入库台账
     * @param inboundRequest
     * @return
     */
    Page<InboundStandingBookResDto> getStandingBook(InboundStandingBookReqDto inboundRequest);

    /**
     * 查询入库台账总数
     * @param inboundStandingBookReqDto
     * @return
     */
    InboundStandingBookResDto getStandingBookCount(InboundStandingBookReqDto inboundStandingBookReqDto);
}
