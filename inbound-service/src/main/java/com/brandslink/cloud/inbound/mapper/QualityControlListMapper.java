package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.InboundStandingBookReqDto;
import com.brandslink.cloud.inbound.dto.InboundStandingBookResDto;
import com.brandslink.cloud.inbound.dto.QcListRequestDto;
import com.brandslink.cloud.inbound.dto.QcListResponseDto;
import com.brandslink.cloud.inbound.entity.QualityControlList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QualityControlListMapper extends BaseMapper<QualityControlList> {
    /**
     * @description: 查询质检单列表
     * @param qcListRequest
     * @return
     */
    List<QcListResponseDto> getQCList(QcListRequestDto qcListRequest);

    /**
     * 查询最后一条质检单号
     * @return
     */
    String selectLastData();

    /**
     * @description: 根据运单号及消退单号查询质检单信息
     * @param sellingBackId
     * @return
     */
    QualityControlList selectQcListBySellingBackId(String sellingBackId);

    /**
     * @description: 根据质检单对象动态更新数据
     * @param qcSelect
     */
    int updateSelective(QualityControlList qcSelect);

    /**
     * @description: 根据卡板号及运单号查询质检单
     * @param waybillNumber
     * @return
     */
    QualityControlList selectQcListByWaybillId(String waybillNumber);

    /**
     * @description: 查询入库台账
     * @param inboundRequest
     * @return
     */
    List<InboundStandingBookResDto> getStandingBookByObJ(InboundStandingBookReqDto inboundRequest);

    /**
     * 查询入库台账总数
     * @param inboundRequest
     * @return
     */
    InboundStandingBookResDto getStandingBookCount(InboundStandingBookReqDto inboundRequest);

    /**
     * @description: 根据来源单号更新质检单状态
     * @param qcListParam
     */
    void updateBySourceOrderNum(QualityControlList qcListParam);

    /**
     * @description: 根据运单号查询版本号
     * @param waybillNumber
     * @return
     */
    Integer selectVersionByWaybillId(String waybillNumber);
}