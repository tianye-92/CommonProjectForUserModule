package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.QualityBoxAssistants;

import java.util.List;

public interface QualityBoxAssistantsMapper extends BaseMapper<QualityBoxAssistants> {


    /**
     * @description: 根据协同人主键更新封箱关系
     * @param qcBoxAssistants
     */
    int updateByZeroAndAssistantsSid(QualityBoxAssistants qcBoxAssistants);

    /**
     * @description: 根据协同人主键查询封箱关系
     * @param sid
     * @return
     */
    List<QualityBoxAssistants> selectByZeroAndAssistantsSid(Long sid);
}