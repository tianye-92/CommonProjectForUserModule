package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsInfo;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;

public interface ReceiveGoodDetailsMapper extends BaseMapper<ReceiveGoodDetails> {
	
	/**
	 * 查询收货商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	List<ReceiveGoodDetails> selectGoodDetailsSelective(ReceiveGoodDetails receiveGoodDetails);
	
	
	/**
	 * 关联查询商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	List<ReceiveGoodDetails> selectGoodDetailsBySourcrId(ReceiveGoodDetails receiveGoodDetails);
	
	
	
	/**
	 * 联查商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	List<ReceiveGoodDetails> selectInfoById(ReceiveGoodDetails receiveGoodDetails);
	


	/**
	 * 灵活修改收货商品信息
	 * @param receiveGoodDetails
	 * @return
	 */
	Integer updateSelective(ReceiveGoodDetails receiveGoodDetails);


	/**
	 * @description: 根据运单号查询到货详情
	 * @param waybillNum
	 * @return
	 */
    List<ReceiveGoodDetails> selectInfoByWaybillNum(String waybillNum);

    
    /**
	 * @description: 根据来源单号查询到货详情
	 * @param sourceId
	 * @return
	 */
    List<ReceiveGoodDetails> selectInfoBySourceId(String sourceId);
    
    /**
     * 根据来源单号查询sku
     * @param sourceId
     * @return
     */
    List<String> selectSkuBySourceId(String sourceId);
    
    
    
    List<String> selectWayBillIdBySourceId(String sourceId);
    
    
	/**
	 * 销退时校验运单号是否存在
	 * @param wayBillId
	 * @return
	 */
	Integer checkWayBillIdAmount(String wayBillId);
	
	
	/**
	 * 销退时校验运单号是否存在
	 * @param wayBillId
	 * @return
	 */
	Integer checkWayBillIdAmountWhenUpdate(@Param("waybillId") String waybillId,@Param("sourceId") String sourceId);

	/**
	 * @description: 根据运单号查询计划数
	 * @param waybillNumber
	 * @return
	 */
	Integer selectCountByWaybillId(String waybillNumber);

	/**
	 * @description: 根据卡板号及运单号查询运单信息
	 * @param cardBoardUpperCase
	 * @param waybillNumUpperCase
	 * @return
	 */
    List<ReceiveGoodDetails> selectByCardBoardIdAndWaybillNum(@Param("cardBoardUpperCase") String cardBoardUpperCase, @Param("waybillNumUpperCase") String waybillNumUpperCase);

	/**
	 * @description: 根据卡板号及来源单号查询运单信息
	 * @param cardBoardNum
	 * @param sourceId
	 * @return
	 */
	List<ReceiveGoodDetails> selectByCardBoardIdAndSourceId(@Param("cardBoardUpperCase") String cardBoardNum, @Param("sourceId") String sourceId);
	
	
	Integer updateByPrimaryKeySelective(ReceiveGoodDetailsInfo info);
	
	
	Integer insertSelective(ReceiveGoodDetailsInfo info);

	/**
	 * @description: 新增运单详情，存在就更新，不存在就新添
	 * @param receiveGoodDetailsInfo
	 */
    void insertUpdateSelective(ReceiveGoodDetailsInfo receiveGoodDetailsInfo);

    /**
     * @description: 根据运单删除未确认的订单
     * @param waybillId
     */
    void deleteUnconfirmedByWaybillId(String waybillId);
}