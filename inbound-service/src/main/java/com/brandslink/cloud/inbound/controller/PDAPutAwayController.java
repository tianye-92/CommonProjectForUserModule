package com.brandslink.cloud.inbound.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.dto.PutawayShelfDto;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.QualityBoxPutawayMapper;
import com.brandslink.cloud.inbound.service.PDAPutAwayService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "上架PDA接口")
@RestController
@RequestMapping("/pdaPutAway")
public class PDAPutAwayController extends CommonController{

	@Resource
	private PDAPutAwayService pDAPutAwayService;
	
	@Resource
	private QualityBoxPutawayMapper qualityBoxPutawayMapper;
	
	private final Logger logger = LoggerFactory.getLogger(PDAPutAwayController.class);
	
	
	/**
	 * 查询封箱的楼层信息
	 * @param sealBoxSerialNumber
	 * @return
	 */
	@ApiOperation(value = "查询质检箱的楼层", notes = "")
	@GetMapping("/selectFloorInfo")
	public String selectFloorInfo(String sealBoxSerialNumber,String warehouseCode){
		return pDAPutAwayService.selectFloorInfo(sealBoxSerialNumber,warehouseCode);
	}
	
	
	/**
	 * 修改封箱表信息
	 * @param 
	 */
	@ApiOperation(value = "将质检封箱箱号绑定楼层箱号", notes = "")
	@PutMapping("/updateInfoSelective")
	public boolean updateInfoSelective(String sealBoxSerialNumber,String floorBoxNumber,String floor,String warehouseCode){
			Integer updateInfoSelective = pDAPutAwayService.updateInfoSelective(sealBoxSerialNumber,floorBoxNumber,floor,warehouseCode);
			if(updateInfoSelective >= 1){
				return true;
			}else{
				return false;
			}
	}
	
	
	
	/**
	 * 通过sku查询上架信息
	 * @param qualityBoxPutaway
	 * @return
	 */
	@ApiOperation(value = "查询sku的货列信息", notes = "")
	@PostMapping("/selectListInfo")
	public Object selectInfoSku(QualityBoxPutaway qualityBoxPutaway){
		if("1".equals(qualityBoxPutaway.getType())){
			String selectSkuLocationCode = pDAPutAwayService.selectSkuLocationCode(qualityBoxPutaway.getSku(),qualityBoxPutaway.getFloorBoxNumber(),qualityBoxPutaway.getWarehouseCode());
			if("1".equals(selectSkuLocationCode)){
				return qualityBoxPutawayMapper.selectFloorBoxSkuDetailInfo(qualityBoxPutaway.getFloorBoxNumber(), qualityBoxPutaway.getWarehouseCode());
			}else{
				String[] split = selectSkuLocationCode.split("&&");
				Map<String,String> hashMap = new HashMap<>();
				hashMap.put("recommendedLocationCode", split[0]);
				hashMap.put("code", split[1]);
				return hashMap;
			}
		}else if("2".equals(qualityBoxPutaway.getType())){
			if(StringUtil.isBlank(qualityBoxPutaway.getSku()) && (StringUtil.isBlank(qualityBoxPutaway.getListBoxNumber()) == false)){
				Integer checkListBoxAmount = qualityBoxPutawayMapper.checkListBoxAmount(qualityBoxPutaway.getListBoxNumber(), qualityBoxPutaway.getWarehouseCode());
				if(checkListBoxAmount <= 0){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该货列箱不存在未上架的货物，请检查!");
				}
				return true;
			}
			QualityBoxPutaway selectInfoBysku = pDAPutAwayService.selectDetailInfoBySku(qualityBoxPutaway);
			return selectInfoBysku;
		}else{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询方式错误");
		}
	}

	

	
	@ApiOperation(value = "绑定货列号", notes = "")
	@PutMapping("/updatePutawayShelfInfoSelective")
	public boolean updatePutawayShelfInfoSelective(QualityBoxPutaway qualityBoxPutaway){
			Integer updatePutawayShelfInfoSelective = pDAPutAwayService.updatePutawayShelfInfoSelective(qualityBoxPutaway);
			if(updatePutawayShelfInfoSelective >= 1){
				return true;
			}else{
				return false;
			}
	}
	
	
	
	/**
	 * 通过货列箱号查询上架信息
	 * @param listBoxNumber
	 */
	@ApiOperation(value = "通过货列箱号查询上架信息", notes = "")
	@GetMapping("/selectSkuInfoByListBoxId")
	public List<PutawayShelfDto>  selectSkuInfoByListBoxId(String listBoxNumber,String warehouseCode){
		return pDAPutAwayService.selectSkuInfoByListBoxId(listBoxNumber,warehouseCode);
	}
	
	
	@ApiOperation(value = "上架确认", notes = "")
	@PostMapping("/putawayShelfConfirm")
	public Object putawayShelfConfirm(QualityBoxPutaway qualityBoxPutaway){
		return  pDAPutAwayService.updatePutawayShelfConfirm(qualityBoxPutaway);
	}
	
}
