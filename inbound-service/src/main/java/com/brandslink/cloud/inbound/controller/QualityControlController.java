package com.brandslink.cloud.inbound.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.dto.*;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.rabbitmq.InboundStatisticsSender;
import com.brandslink.cloud.inbound.service.QualityControlService;
import com.brandslink.cloud.inbound.service.impl.QualityControlServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaojiaxing
 * @description: 到货质检基本接口
 * @date 2019/5/30 17:02
 * @version 1.0
 */
@Api(tags = "到货质检基本接口")
@RestController
@RequestMapping("/qc")
public class QualityControlController extends CommonController {
    private final Logger LOGGER = LoggerFactory.getLogger(QualityControlController.class);
    @Resource
    private QualityControlService qualityControlService;
    @Resource
    private InboundStatisticsSender inboundStatisticsSender;

    /**
     * @description: 根据卡板号及运单号(或采购单号)查看货物信息（支持分页）
     * @params: cardBoardNum:卡板号;waybillNum：运单号或采购单号;flag:0,运单号,1,采购单号;
     * @return:
     */
    @ApiOperation(value = "根据卡板号及运单号查看货物信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = true, dataType = "string", paramType = "query",example = "100000000"),
            @ApiImplicitParam(name = "cardBoardNum", value = "卡板号", required = true, dataType = "string", paramType = "query",example = "100000000"),
            @ApiImplicitParam(name = "waybillNum", value = "运单号或来源单号",  required = true, dataType = "string", paramType = "query",example = "运单000001"),
            @ApiImplicitParam(name = "flag", value = "flag标志：1:运单号；2：来源单号", required = true, dataType = "string", paramType = "query",example = "1")
          })
    @PostMapping(value = "/getWaybillInfo")
    @RequestRequire(require = "cardBoardNum,waybillNum,flag", parameter = String.class)
    public QcListResponseDto getWaybillInfoByCardBoard(@RequestParam(value = "warehouseCode", required = false) String warehouseCode,
                                                       @RequestParam(value = "cardBoardNum") String cardBoardNum,
                                                       @RequestParam(value = "waybillNum") String waybillNum,
                                                       @RequestParam(value = "flag") String flag,
                                                       AssistantDto assistants){
        return qualityControlService.getWaybillInfoByCardBoard(warehouseCode, cardBoardNum, waybillNum, assistants, flag);
    }

    /**
     * @description: 添加QC完成数量
     * @params: qcWaybillRequsetDto
     * @return: String
     */
    @ApiOperation(value = "添加QC完成数量",notes = "flag:(0:到货质检；1：销退质检)")
    @PostMapping(value = "/addWaybillInfo")
    @RequestRequire(require = "qualityControlOrderNumber,waybillNumber,sku,goodProductNumber,skuPlanNumber,sourceOrderNumber,qcStationId,qualityControlFinishNumber,warehouseCode,sealBoxFinishNumber,flag", parameter = QcWaybillRequestDto.class)
    public String addQCWaybillFinish(QcWaybillRequestDto qcWaybillRequestDto){
        InboundWorkloadInfo info = qualityControlService.addQCWaybillFinish(qcWaybillRequestDto);
        this.Sender(info);
        return "success";
    }


    /**
     * @description: 绑定容器
     * @params: 需要传入sid与箱号的集合
     * @return:
     */
    @ApiOperation(value = "绑定容器")
    @PostMapping(value = "/sealBox")
    public String addQCBoxFinish(@RequestBody List<QcSealBoxReqDto> qcSealBoxReqList){
        return qualityControlService.addQCBoxFinish(qcSealBoxReqList);
    }

    /**
     * @description: 质检异常上报
     * @param qcReportExceptionRequestDto
     * @return
     */
    @ApiOperation(value = "质检异常上报")
    @PostMapping(value = "/addException")
    @RequestRequire(require = "waybillNumber,sku,exceptionCauseId,sourceOrderNumber,exceptionSkuCount,exceptionBoxNumber,warehouseCode,detailId", parameter = QcReportExceptionRequestDto.class)
    public String addQCException(@RequestBody QcReportExceptionRequestDto qcReportExceptionRequestDto){
        return qualityControlService.addQCException(qcReportExceptionRequestDto);
    }

    /**
     * @description: 查询质检单列表或质检单详情（支持分页）
     * @param qcListRequest
     * @return
     */
    @ApiOperation(value = "查询质检单列表或质检单详情（支持分页)(flag:（0：查询质检单列表；1:查询质检明细））")
    @PostMapping(value = "/getList")
    @RequestRequire(require = "flag",parameter = QcListRequestDto.class)
    public Page<QcListResponseDto> getQCList(@RequestBody QcListRequestDto qcListRequest){
        return qualityControlService.getQCList(qcListRequest);
    }

    /**
     * @description: 查询异常单号列表（支持分页）
     * @param waybillNumber
     * @return
     */
    @ApiOperation(value = "查询异常单号列表（支持分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "waybillNumber", value = "运单号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/getException")
    @RequestRequire(require = "waybillNumber,pageNum,pageSize", parameter = String.class)
    public Page<QcExceptionDetailResDto> getQCException(@RequestParam(value = "waybillNumber") String waybillNumber,
                                                        @RequestParam(value = "pageNum") String pageNum,
                                                        @RequestParam(value = "pageSize") String row){
        return qualityControlService.getQCException(waybillNumber,pageNum,row);
    }

    @ApiOperation(value = "根据销退运单号查询货物信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "waybillNum", value = "运单号",  required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/getSellingBack/waybillNum")
    @RequestRequire(require = "warehouseCode, waybillNum", parameter = String.class)
    public QcListResponseDto getSellingBackByWaybillNum(@RequestParam(value = "waybillNum") String waybillNum,
                                                        @RequestParam(value = "warehouseCode", required = false) String warehouseCode,
                                                       AssistantDto assistants){
        return qualityControlService.getSellingBackByWaybillNum(warehouseCode, waybillNum.toUpperCase(), assistants);
    }

    @ApiOperation(value = "根据采购单号查询货物信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceNumber", value = "采购单号",  required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/getSellingBack/sourceNumber")
    @RequestRequire(require = "warehouseCode, sourceNumber", parameter = String.class)
    public QcListResponseDto getSellingBackBySourceNumber(@RequestParam(value = "sourceNumber") String sourceNumber,
                                                          @RequestParam(value = "warehouseCode", required = false) String warehouseCode,
                                                          AssistantDto assistants){
        return qualityControlService.getSellingBackBySourceNumber(warehouseCode, sourceNumber.toUpperCase(), assistants);
    }

    /**
     * @description: 消息推送
     * @param info
     */
    public void Sender(InboundWorkloadInfo info){
        try {
            LOGGER.info("【质检完成统计】运单：{}，新增质检次数：{},新增质检个数：{}", info.getWaybillId(), info.getQcTimeNum(), info.getQcNum());
            inboundStatisticsSender.InboundStatisticsSend(info);
        } catch (Exception e) {
            LOGGER.error("质检完成消息发送异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"质检消息发送异常");
        }
    }

}
