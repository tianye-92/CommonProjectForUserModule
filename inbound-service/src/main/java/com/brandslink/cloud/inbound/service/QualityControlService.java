package com.brandslink.cloud.inbound.service;

import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.*;

import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检服务层接口
 * @date 2019/6/3 9:26
 */
public interface QualityControlService {

    /**
     * @description 根据卡板号及运单号(或采购单号)查看货物信息
     * @param cardBoardNum
     * @param waybillNum
     * @param flag
     * @return
     */
    QcListResponseDto getWaybillInfoByCardBoard(String warehouseCode, String cardBoardNum, String waybillNum, AssistantDto assistants,String flag);

    /**
     * @description: 添加QC完成数量
     * @param qcWaybillRequestDto
     * @return
     */
    InboundWorkloadInfo addQCWaybillFinish(QcWaybillRequestDto qcWaybillRequestDto);

    /**
     * @description: 添加封箱完成数量
     * @param qcSealBoxReqList
     * @return
     */
    String addQCBoxFinish(List<QcSealBoxReqDto> qcSealBoxReqList);

    /**
     * @description: 异常上报
     * @param qcReportExceptionRequestDto
     * @return
     */
    String addQCException(QcReportExceptionRequestDto qcReportExceptionRequestDto);

    /**
     * @description: 查询质检单列表或质检单详情
     * @param qcListReauest
     * @return
     */
    Page<QcListResponseDto> getQCList(QcListRequestDto qcListReauest);

    /**
     * @description: 根据来源单号查询异常信息
     * @param sourceNumber
     * @param pageNum
     * @param row
     * @return
     */
    Page<QcExceptionDetailResDto> getQCException(String sourceNumber, String pageNum, String row);


    /**
     * @description: 根据运单号查询货物信息
     * @param waybillNum
     * @param assistants
     * @return
     */
    QcListResponseDto getSellingBackByWaybillNum(String warehouseCode, String waybillNum, AssistantDto assistants);

    /**
     * @description: 根据来源单号查询货物信息
     * @param sourceNumber
     * @param assistants
     * @return
     */
    QcListResponseDto getSellingBackBySourceNumber(String warehouseCode, String sourceNumber, AssistantDto assistants);

}
