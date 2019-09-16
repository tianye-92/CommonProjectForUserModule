package com.brandslink.cloud.inbound.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 质检异常控制
 * 实体类对应的数据表为：  t_quality_exception_control
 * @author zhaojiaxing
 * @date 2019-06-05 17:13:49
 */
@ApiModel(value ="QualityExceptionControl")
public class QualityExceptionControl implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "运单号，SKU对应所在的承运商单号")
    private String waybillNumber;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "异常原因id")
    private String exceptionCauseId;

    @ApiModelProperty(value = "异常商品数量")
    private Integer exceptionSkuCount;

    @ApiModelProperty(value = "异常上报人id")
    private Long exceptionReporterId;

    @ApiModelProperty(value = "异常报告人名称")
    private String exceptionReporterBy;

    @ApiModelProperty(value = "异常箱号")
    private String exceptionBoxNumber;

    @ApiModelProperty(value = "异常报告时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exceptionReporterTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_quality_exception_control
     *
     * @mbg.generated 2019-06-05 17:13:49
     */
    private static final long serialVersionUID = 1L;

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
        this.sourceOrderNumber = sourceOrderNumber == null ? null : sourceOrderNumber.trim();
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber == null ? null : waybillNumber.trim();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public String getExceptionCauseId() {
        return exceptionCauseId;
    }

    public void setExceptionCauseId(String exceptionCauseId) {
        this.exceptionCauseId = exceptionCauseId;
    }

    public Integer getExceptionSkuCount() {
        return exceptionSkuCount;
    }

    public void setExceptionSkuCount(Integer exceptionSkuCount) {
        this.exceptionSkuCount = exceptionSkuCount;
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
        this.exceptionReporterBy = exceptionReporterBy == null ? null : exceptionReporterBy.trim();
    }

    public String getExceptionBoxNumber() {
        return exceptionBoxNumber;
    }

    public void setExceptionBoxNumber(String exceptionBoxNumber) {
        this.exceptionBoxNumber = exceptionBoxNumber == null ? null : exceptionBoxNumber.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getExceptionReporterTime() {
        return exceptionReporterTime;
    }

    public void setExceptionReporterTime(Date exceptionReporterTime) {
        this.exceptionReporterTime = exceptionReporterTime;
    }
}