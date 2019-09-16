package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.PutawayStrategy;
import org.apache.ibatis.annotations.Param;

public interface PutawayStrategyMapper extends BaseMapper<PutawayStrategy> {
	
	List<PutawayStrategy> findAll(PutawayStrategy putawayStrategy);
	
	int deleteByPrimaryKey(Integer primaryKey);

	/**
	 * @description: 根据仓库id获取策略信息
	 * @param warehouseId
	 * @return
	 */
    List<PutawayStrategy> selectByWareHouse(@Param("warehouseId") String warehouseId, @Param("strategyStatus") String strategyStatus);
}