package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.entity.PutawayStrategy;

public interface PutawayStrategyService {
	
	/**
	 * 添加策略信息
	 * @param putawayStrategy
	 */
	void insertInfo(PutawayStrategy putawayStrategy);
	
	void updateInfo(PutawayStrategy putawayStrategy);
	
	Page<PutawayStrategy> findAll(PutawayStrategy putawayStrategy);
	
	void deleteInfoById(Integer id);
	
}
