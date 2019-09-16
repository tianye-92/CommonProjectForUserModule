package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.inbound.entity.Attribute;
import com.brandslink.cloud.common.mapper.BaseMapper;

public interface AttributeMapper extends BaseMapper<Attribute> {
    int update(Attribute attribute);
}