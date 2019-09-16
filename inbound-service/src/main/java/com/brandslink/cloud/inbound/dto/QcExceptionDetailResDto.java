package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检异常明细返回信息
 * @date 2019/6/11 19:56
 */
public class QcExceptionDetailResDto {

    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "运单号，SKU对应所在的承运商单号")
    private String waybillNumber;

    @ApiModelProperty(value = "异常原因")
    private String exceptionCauseId;

    @ApiModelProperty(value = "异常箱号")
    private String exceptionBoxNumber;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "异常商品数量")
    private Integer exceptionSkuCount;

    @ApiModelProperty(value = "生产日期")
    private Date produceTime;

    @ApiModelProperty(value = "有效期")
    private Integer qualityTime;

    @ApiModelProperty(value = "失效日期")
    private Date expirationTime;

    @ApiModelProperty(value = "异常上报人id")
    private Long exceptionReporterId;

    @ApiModelProperty(value = "异常报告人名称")
    private String exceptionReporterBy;

    @ApiModelProperty(value = "异常报告时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exceptionReporterTime;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
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

    public Date getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(Date produceTime) {
        this.produceTime = produceTime;
    }

    public Integer getQualityTime() {
        return qualityTime;
    }

    public void setQualityTime(Integer qualityTime) {
        this.qualityTime = qualityTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getExceptionReporterId() {
        return exceptionReporterId;
    }

    public void setExceptionReporterId(Long exceptionReporterId) {
        this.exceptionReporterId = exceptionReporterId;
    }

    public String getExceptionReporterBy() {
        return exceptionReporterBy;
    }

    public void setExceptionReporterBy(String exceptionReporterBy) {
        this.exceptionReporterBy = exceptionReporterBy;
    }

    public Date getExceptionReporterTime() {
        return exceptionReporterTime;
    }

    public void setExceptionReporterTime(Date exceptionReporterTime) {
        this.exceptionReporterTime = exceptionReporterTime;
    }
}
