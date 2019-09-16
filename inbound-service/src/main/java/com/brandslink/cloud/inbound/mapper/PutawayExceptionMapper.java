package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.PutawayException;

import java.util.List;

public interface PutawayExceptionMapper extends BaseMapper<PutawayException> {
    /**
     * @description: 根据上架单查询上架异常明细
     * @param putawayId
     * @return
     */
    List<PutawayException> selectExceptionDetailsByPutawayId(String putawayId);

    /**
     * @description: 根据上架单号查询异常总数
     * @param putawayId
     * @return
     */
    Integer selectExcCountByPutawayId(String putawayId);
}