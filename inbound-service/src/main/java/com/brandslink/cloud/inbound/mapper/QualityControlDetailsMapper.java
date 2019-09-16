package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.QcListResponseDto;
import com.brandslink.cloud.inbound.dto.QcWaybillInfoDto;
import com.brandslink.cloud.inbound.entity.QualityControlDetails;
import com.brandslink.cloud.inbound.entity.QualityExceptionControl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QualityControlDetailsMapper extends BaseMapper<QualityControlDetails> {
    /**
     * @description: 根据卡板号及运单号查询货物信息
     * @param cardBoardNum：卡板号
     * @param waybillNum：运单号
     * @return
     */
    List<QcWaybillInfoDto> getWaybillInfoByCardBoardAndWaybillNum(@Param("cardBoardNum") String cardBoardNum, @Param("waybillNum") String waybillNum);

    /**
     * @description: 根据卡板号及来源单号查询货物信息
     * @param cardBoardNum：卡板号
     * @param waybillNum：来源单号
     * @return
     */
    List<QcWaybillInfoDto> getWaybillInfoByCardBoardAndSourceNum(@Param("cardBoardNum") String cardBoardNum, @Param("waybillNum") String waybillNum);

    /**
     * 根据运单号及商品sku查询商品详情
     *
     * @param qualityControlOrderNumber
     * @param waybillNumber :运单号
     * @param sku :商品sku
     * @return
     */
    QualityControlDetails selectQCWaybillDetails(@Param("qualityControlOrderNumber") String qualityControlOrderNumber, @Param("waybillNumber") String waybillNumber, @Param("sku") String sku);

    /**
     * 根据运单号及商品sku查询异常详情
     * @param waybillNumber 运单号
     * @param sku 商品sku
     * @return
     */
    QualityExceptionControl selectQCException(@Param("waybillNumber") String waybillNumber, @Param("sku") String sku);



    /**
     * @description: 根据来源单号查询订单总数量
     * @param sourceOrderNumber
     * @return
     */
    Integer selectCountBySourceId(String sourceOrderNumber);

    /**
     * 查询质检单详情
     * @param qualityControlOrderNumber
     * @return
     */
    List<QcListResponseDto> getQCListDetails(String qualityControlOrderNumber);

    /**
     * @description: 根据采购单号及卡板号查询运单详情
     * @param cardBoardNum: 卡板号
     * @param purchaseOrder: 采购单号
     * @return
     */
    List<QualityControlDetails> selectQCDetailsByCardBoardNum(@Param("cardBoardNum") String cardBoardNum, @Param("purchaseOrder") String purchaseOrder);

    /**
     * @description: 根据运单号查询质检总数
     * @param waybillNumber
     * @return
     */
    Integer selectCountByWaybillId(String waybillNumber);
    
    /**
     * @description: 根据来源单号查询质检总数
     * @param sourceId
     * @return
     */
    Integer selectCountBySourceOrderNumber(String sourceId);
    
    
    
    /**
     * 根据来源单号&sku查询质检完成状态
     * @param sourceOrderNumber
     * @param sku
     * @return
     */
    List<Integer> selectOkStatusBySourceOrderNumber(@Param("sourceOrderNumber") String sourceOrderNumber, @Param("sku") String sku);
    
    
    
    /**
     * 根据运单号和sku查询收货数
     * @param waybillNumber
     * @param sku
     * @return
     */
    Integer selectDeliveryQuantityByWayBillId(@Param("waybillNumber")String waybillNumber,@Param("sku")String sku);

    /**
     * @description: 根据主键查询运单详情
     * @param sid
     * @return
     */
    QcWaybillInfoDto selectDetailByPrimaryKey(Long sid);

    /**
     * @description: 根据来源单号更新质检单详情
     * @param qcDetails
     */
    void updateBySourceOrderNum(QualityControlDetails qcDetails);

    /**
     * @description:
     * @param waybillNumber
     * @return
     */
    List<String> selectStatusByWaybillId(String waybillNumber);

    /**
     * @description: 查询拆包数
     * @param waybillNumber
     * @return
     */
    Integer selectUnpackByWaybill(String waybillNumber);
}