package com.brandslink.cloud.inbound.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.PutawayShelf;

public interface PutawayShelfMapper extends BaseMapper<PutawayShelf> {
	
	/**
	 * @description: 查询货位分配信息
	 * @param putawayShelf ：查询条件：运单号，SKU, 楼层, 推荐货位，上架状态, 仓库编码
	 * @return
	 */
    PutawayShelf selectByObj(PutawayShelf putawayShelf);

	/**
	 * @description: 根据运单+sku+楼层查询已分配上架
	 * @param warehouseCode : 仓库code
	 * @param waybillNumber : 运单号
	 * @param sku ：sku
	 * @param skuFloor : 楼层
	 * @return
	 */
    List<PutawayShelf> selectDistributed(@Param("warehouseCode") String warehouseCode, @Param("waybillNumber") String waybillNumber, @Param("sku") String sku, @Param("skuFloor") String skuFloor);

    /**
     * @description: 根据主键更新剩余未封箱sku
     * @param putawayShelf
     */
    int updateRemainNumByPrimaryKey(PutawayShelf putawayShelf);

	/**
	 * @description: 根据仓库+运单+SKU+楼层更新剩余封箱sku
	 * @param putawayShelf
	 */
	void updateByWaybillAndSkuAndFloor(PutawayShelf putawayShelf);

	/**
	 * @description: 查询未封箱运单的sku
	 * @param waybillNumber
	 * @param sku
	 * @return
	 */
	List<String> selectUnSealFloor(@Param("waybillNumber") String waybillNumber, @Param("sku") String sku);
}