package com.brandslink.cloud.inbound.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.entity.PutawayStrategy;
import com.brandslink.cloud.inbound.mapper.PutawayStrategyMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.PutawayStrategyService;
import com.github.pagehelper.PageInfo;

import jodd.util.StringUtil;

@Service
public class PutawayStrategyServiceImpl implements PutawayStrategyService{

	@Resource
	private PutawayStrategyMapper putawayStrategyMapper;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	@Resource
	private RemoteCenterService remoteCenterService;
	
	
	/**
	 * 添加策略信息
	 * @param putawayStrategy
	 */
	@Override
    public void insertInfo(PutawayStrategy putawayStrategy) {
		putawayStrategyMapper.insertSelective(putawayStrategy);
	}


	@Override
    public void updateInfo(PutawayStrategy putawayStrategy) {
		putawayStrategyMapper.updateByPrimaryKeySelective(putawayStrategy);
	}

	
	@Override
    public Page<PutawayStrategy> findAll(PutawayStrategy putawayStrategy) {
		UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();

		if (StringUtils.isBlank(putawayStrategy.getWarehouse())){
			putawayStrategy.setWarehouseCodes(new ArrayList<>(Arrays.asList(userInfo.getWarehouseCode().split(","))));
			putawayStrategy.getWarehouseCodes().add("0");
		}

		List<PutawayStrategy> findAll = putawayStrategyMapper.findAll(putawayStrategy);
		
		for (PutawayStrategy putawayStrategy2 : findAll) {
			String dataCode = putawayStrategy2.getStrategyRule();
			String dictCode = "strategy_rule";
			if(StringUtil.isNotBlank(dataCode)){
				String dictionaryDataDetail = remoteCenterService.getDictionaryDataDetail(dictCode, dataCode);
				String result = Utils.returnRemoteResultDataString(dictionaryDataDetail, "查询码表策略信息异常");
				JSONObject parseObject = JSONObject.parseObject(result);
				if(parseObject == null){
					putawayStrategy2.setStrategyRule("无仓库信息");
				}
				String object = (String)parseObject.get("dataName");
				putawayStrategy2.setStrategyRule(object);
			}
		}
		PageInfo pageInfo = new PageInfo(findAll);
		return new Page(pageInfo);
	}


	@Override
    public void deleteInfoById(Integer id) {
		putawayStrategyMapper.deleteByPrimaryKey(id);
	}
	
}
