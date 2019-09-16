package com.brandslink.cloud.inbound.controller;

import javax.annotation.Resource;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.inbound.entity.PutawayStrategy;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.PutawayStrategyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "上架策略接口")
@RestController
@RequestMapping("/putawayStrategy")
public class PutawayStrategyController {

	@Resource
	private PutawayStrategyService putawayStrategyService;
	
	private final Logger logger = LoggerFactory.getLogger(PutawayStrategyController.class);
	
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;
	
	/**
	 * 添加策略信息
	 * @param putawayStrategy
	 */
	@ApiOperation(value = "添加策略信息", notes = "")
	@PostMapping("/insertInfo")
	public void insertInfo(PutawayStrategy putawayStrategy) {
		if(StringUtil.isBlank(putawayStrategy.getStrategyName())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"策略名称不能为空");
        }
		if(StringUtil.isBlank(putawayStrategy.getWarehouse())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"仓库信息不能为空");
        }
		if(StringUtil.isBlank(putawayStrategy.getStrategyRule())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"策略规则不能为空");
        }
		try {
			putawayStrategy.setCreater(getUserDetailInfoUtil.getUserDetailInfo().getAccount());
			putawayStrategy.setCreaterId(getUserDetailInfoUtil.getUserDetailInfo().getId());
			putawayStrategyService.insertInfo(putawayStrategy);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("添加策略信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"添加策略信息异常");
		}
	}
	
	
	
	/**
	 * 添加策略信息
	 * @param putawayStrategy
	 */
	@ApiOperation(value = "修改策略信息", notes = "")
	@PutMapping("/updateStatus")
	public void updateStatus(PutawayStrategy putawayStrategy) {
		if(putawayStrategy.getStatus() == null || putawayStrategy.getId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"状态值或id值不能为空");
        }
		
		try {
			putawayStrategyService.updateInfo(putawayStrategy);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("修改策略状态异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"修改策略状态异常");
		}
	}
	
	
	/**
	 * 查询策略规则信息
	 * @param page
	 * @param row
	 * @return
	 */
	@ApiOperation(value = "查询策略规则信息", notes = "")
	@GetMapping("/putawayStrategyFindAll")	
	@RequestRequire(require="pageNum,pageSize",parameter=String.class)
	public Page<PutawayStrategy> putawayStrategyFindAll(PutawayStrategy putawayStrategy,String pageNum,String pageSize){
		try {
			Page.builder(pageNum, pageSize);
			return putawayStrategyService.findAll(putawayStrategy);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询策略规则信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询策略规则信息异常");
		}
	}
	
	/**
	 * 删除策略规则信息
	 */
	@ApiOperation(value = "删除策略规则信息", notes = "")
	@DeleteMapping("/deleteInfoById")	
	public void deleteInfoById(Integer id){
		if(id == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"id值不能为空");
        }
		try {
			putawayStrategyService.deleteInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除策略规则信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"删除策略规则信息异常");
		}
	}
	
	
	
	
	
	
	
	
	
 	
	
	
}
