package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.inbound.entity.PutawayDetails;
import org.apache.ibatis.annotations.Param;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.PutawayShelfDto;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;

public interface QualityBoxPutawayMapper extends BaseMapper<QualityBoxPutaway> {

    /**
     * @description: 根据sku及运单号，查询封箱总数
     * @param waybillId
     * @param sku
     * @param skuFloor
     * @return
     */
    Integer selectBoxSkuSum(@Param("waybillId") String waybillId, @Param("sku") String sku, @Param("skuFloor") String skuFloor);

	/**
	 * 通过质检箱号查询楼层
	 * @param sealBoxSerialNumber
	 * @return
	 */
	String selectSkuFloorByQuBox(@Param("sealBoxSerialNumber")String sealBoxSerialNumber,@Param("warehouseCode")String warehouseCode);
	
	/**
	 * 查询质检箱未封箱数量
	 * @param sealBoxSerialNumber
	 * @param warehouseCode
	 * @return
	 */
	Integer checkAmountByQualityBox(@Param("sealBoxSerialNumber")String sealBoxSerialNumber,@Param("warehouseCode")String warehouseCode);
	
	
	/**
	 * 绑定楼层箱是校验是否重复绑定
	 * @param sealBoxSerialNumber
	 * @param warehouseCode
	 * @return
	 */
	String checkRepetitionFloorBox(@Param("sealBoxSerialNumber")String sealBoxSerialNumber,@Param("warehouseCode")String warehouseCode);
	
	
	/**
	 * 校验操作  防止绑定质检箱
	 * @param warehouseCode
	 * @return
	 */
	List<String> checkQualityBoxBind(String warehouseCode);
	
	
	/**
	 * 质检箱绑定楼层箱
	 * @param sealBoxSerialNumber
	 * @param warehouseCode
	 * @return
	 */
	Integer bindFloorBox(@Param("floorBoxNumber")String floorBoxNumber,@Param("sealBoxSerialNumber")String sealBoxSerialNumber,@Param("warehouseCode")String warehouseCode);

	

	/**
	 * 校验楼层箱内是否有未货列分离的商品
	 * @param floorBoxNumber
	 * @param warehouseCode
	 * @return
	 */
	Integer checkAmountByListBox(@Param("floorBoxNumber")String floorBoxNumber,@Param("warehouseCode")String warehouseCode);
	
	
	
	/**
	 * 通过楼层箱号，sku查询货列
	 * @return
	 */
	List<String> selectListInfoByFloorBox(@Param("floorBoxNumber")String floorBoxNumber,@Param("sku")String sku,@Param("warehouseCode")String warehouseCode);


	/**
	 * 校验楼层箱中的商品是否已经分理完
	 * @param floorBoxNumber
	 * @param warehouseCode
	 * @return
	 */
	Integer checkFloorBoxAmount(@Param("floorBoxNumber")String floorBoxNumber,@Param("warehouseCode")String warehouseCode);
	
	
	/**
	 * 查询楼层箱内的商品详情信息
	 * @param floorBoxNumber
	 * @param warehouseCode
	 * @return
	 */
	List<QualityBoxPutaway> selectFloorBoxSkuDetailInfo(@Param("floorBoxNumber")String floorBoxNumber,@Param("warehouseCode")String warehouseCode);
	
	
	/**
	 * 绑定货列号
	 * @param QualityBoxPutaway
	 * @return
	 */
	Integer bindListBox(QualityBoxPutaway QualityBoxPutaway);

	
	/**
	 * 校验货列箱中的商品是否已经分理完
	 * @param listBoxNumber
	 * @param warehouseCode
	 * @return
	 */
	Integer checkListBoxAmount(@Param("listBoxNumber")String listBoxNumber,@Param("warehouseCode")String warehouseCode);

	
	/**
	 * 查询sku上架详情信息
	 * @param QualityBoxPutaway
	 * @return
	 */
	List<QualityBoxPutaway> selectSkuDetailInfo(QualityBoxPutaway QualityBoxPutaway);
	
	
	/**
	 * 第二次查询sku上架详情信息
	 * @param QualityBoxPutaway
	 * @return
	 */
	List<QualityBoxPutaway>  selectSkuDetailInfoSecond(QualityBoxPutaway QualityBoxPutaway);

	
	/**
	 * 上架确认
	 * @param QualityBoxPutaway
	 * @return
	 */
	Integer putawayShelfSuccess(QualityBoxPutaway QualityBoxPutaway);

	
	/**
	 * 通过货列箱号查询上架信息
	 * @param listBoxNumber
	 * @return
	 */
	List<PutawayShelfDto>  selectSkuInfoByListBoxId(@Param("listBoxNumber")String listBoxNumber,@Param("warehouseCode")String warehouseCode);

	/**
	 * @description: 根据上架单号查询上架详情
	 * @param putawayId
	 * @return
	 */
	List<QualityBoxPutaway> selectByPutawayId(String putawayId);
	
	
	/**
	 * 根据上架单号查询上架状态
	 * @param putawayId
	 * @return
	 */
	List<Integer> selectPutawayStatusByPutawayId(@Param("putawayId")String putawayId,@Param("warehouseCode")String warehouseCode);

}