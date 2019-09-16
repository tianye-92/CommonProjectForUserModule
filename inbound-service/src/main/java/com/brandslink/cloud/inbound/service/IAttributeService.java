package com.brandslink.cloud.inbound.service;

import com.brandslink.cloud.inbound.entity.Attribute;
import com.brandslink.cloud.common.service.BaseService;

public interface IAttributeService extends BaseService<Attribute> {
    Object updateAttribute(Attribute attribute) throws InterruptedException;
}
