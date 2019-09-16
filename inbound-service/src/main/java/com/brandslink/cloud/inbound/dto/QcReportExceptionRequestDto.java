package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 异常上报请求参数
 * @date 2019/6/3 19:35
 */
public class QcReportExceptionRequestDto {

    @ApiModelProperty(value = "运单详情主键id")
    private Long detailId;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "运单号")
    private String waybillNumber;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "异常sku数量")
    private Integer exceptionSkuCount;

    @ApiModelProperty(value = "异常原因id")
    private String exceptionCauseId;

    @ApiModelProperty(value = "异常箱号")
    private String exceptionBoxNumber;

    @ApiModelProperty(value = "异常报告时间")
    private Date exceptionReporterTime;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public String getSourceOrderNumber() {
        return sourceOrderNumber;
    }

    public void setSourceOrderNumber(String sourceOrderNumber) {
        this.sourceOrderNumber = sourceOrderNumber;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getExceptionSkuCount() {
        return exceptionSkuCount;
    }

    public void setExceptionSkuCount(Integer exceptionSkuCount) {
        this.exceptionSkuCount = exceptionSkuCount;
    }

    public String getExceptionCauseId() {
        return exceptionCauseId;
    }

    public void setExceptionCauseId(String exceptionCauseId) {
        this.exceptionCauseId = exceptionCauseId;
    }

    public String getExceptionBoxNumber() {
        return exceptionBoxNumber;
    }

    public void setExceptionBoxNumber(String exceptionBoxNumber) {
        this.exceptionBoxNumber = exceptionBoxNumber;
    }

    public Date getExceptionReporterTime() {
        return exceptionReporterTime;
    }

    public void setExceptionReporterTime(Date exceptionReporterTime) {
        this.exceptionReporterTime = exceptionReporterTime;
    }
}
