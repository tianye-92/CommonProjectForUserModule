package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.QualityControlAssistants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QualityControlAssistantsMapper extends BaseMapper<QualityControlAssistants> {

    /**
     * @description: 查询协同人及质检台相关信息
     * @param waybillNumber
     * @param sku
     * @param recommendedLocationCode
     * @return
     */
    List<QualityControlAssistants> selectAssistants(@Param("waybillNumber") String waybillNumber, @Param("sku") String sku, @Param("recommendedLocationCode") String recommendedLocationCode);
}