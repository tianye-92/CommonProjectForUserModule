package com.brandslink.cloud.inbound.mapper;

import org.apache.ibatis.annotations.Param;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.BoxInfo;

public interface BoxInfoMapper extends BaseMapper<BoxInfo> {
	
	
	BoxInfo selectBoxInfoByCode(@Param("warehouse")String warehouse,@Param("boxId")String boxId);
	
	
	Integer insertInfo(BoxInfo boxInfo);
	
	
}