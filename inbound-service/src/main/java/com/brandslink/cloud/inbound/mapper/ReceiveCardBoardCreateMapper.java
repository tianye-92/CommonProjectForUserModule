package com.brandslink.cloud.inbound.mapper;

import java.util.List;

import com.brandslink.cloud.inbound.entity.QualityControlList;
import org.apache.ibatis.annotations.Param;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.entity.ReceiveCardBoardCreate;

public interface ReceiveCardBoardCreateMapper extends BaseMapper<ReceiveCardBoardCreate> {
	
	/**
	 * 查询流水号
	 * @param dataString
	 * @return
	 */
	List<String> selectCardBoardCreateIdByDate(String dataString);
	
	
	Integer deleteByPrimaryKey(Integer id);
	
	
	void deleteInfo(@Param("sourceId")String sourceId,@Param("waybillId")String waybillId);
	
	
	
	Integer checkWayBillIdAmount(String waybillId);
	

	/**
	 * @description: 根据卡板号查询货物信息
	 * @param cardBoardNum
	 * @return
	 */
    List<ReceiveCardBoardCreate> selectByCardBoardId(@Param("cardBoardNum") String cardBoardNum, @Param("sourceOrderNumber") String sourceOrderNumber, @Param("waybillId") String waybillId);
}