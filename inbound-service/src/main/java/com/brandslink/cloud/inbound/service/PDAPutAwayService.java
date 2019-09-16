package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.inbound.dto.PutawayShelfDto;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;

public interface PDAPutAwayService {

    /**
	* 根据封箱箱号查询楼层信息
	* @param sealBoxSerialNumber
	* @return
	*/
	String selectFloorInfo(String sealBoxSerialNumber,String warehouseCode);
	
	/**
	 * 修改信息
	 * @param qualityControlBox
	 */
	Integer updateInfoSelective(String sealBoxSerialNumber,String floorBoxNumber,String floor,String warehouseCode); 
	
	
	
	/**
	 * 查询sku的货列号
	 * @param sku
	 * @return
	 */
	String  selectSkuLocationCode(String sku,String floorBoxNumber,String warehouseCode);
	
	
	/**
	 * 通过sku查询上架信息
	 */
	QualityBoxPutaway selectDetailInfoBySku(QualityBoxPutaway QualityBoxPutaway);
	
	
	/**
	 * 绑定货列号
	 * @param qualityBoxPutaway
	 */
	Integer updatePutawayShelfInfoSelective(QualityBoxPutaway qualityBoxPutaway);
	
	
	/**
	 * 通过货列箱号查询上架信息
	 * @param listBoxNumber
	 * @return
	 */
	List<PutawayShelfDto>  selectSkuInfoByListBoxId(String listBoxNumber,String warehouseCode);
	
	
	/**
	 * 上架确认
	 * @param putawayShelf
	 */
	boolean updatePutawayShelfConfirm(QualityBoxPutaway qualityBoxPutaway);
	
	
}
