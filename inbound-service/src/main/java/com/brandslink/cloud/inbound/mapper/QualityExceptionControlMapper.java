package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.QcExceptionDetailResDto;
import com.brandslink.cloud.inbound.entity.QualityExceptionControl;

import java.util.List;

public interface QualityExceptionControlMapper extends BaseMapper<QualityExceptionControl> {
    /**
     * @description: 根据来源单号查询异常单详情
     * @param sourceNumber
     * @return
     */
    List<QcExceptionDetailResDto> selectQCExceptionBySourceNumber(String sourceNumber);

    /**
     * @description: 根据来运单号查询异常单详情
     * @param waybillNumber
     * @return
     */
    List<QcExceptionDetailResDto> selectQCExceptionBywaybillNumber(String waybillNumber);
}