package com.brandslink.cloud.inbound.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检单信息
 * @date 2019/6/4 9:32
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QcListResponseDto <T>{
    /**
     * 主键id1
     */
    @ApiModelProperty(value = "主键id")
    private Long sid;
    /**
     * 卡板号
     */
    @ApiModelProperty(value = "卡板号")
    private String cardBoardId;
    /**
     * 仓库名1
     */
    @ApiModelProperty(value = "仓库名")
    private String warehouse;
    /**
     * 质检台
     */
    @ApiModelProperty(value = "质检台")
    private String qcStationId;
    /**
     * 质检单号1
     */
    @ApiModelProperty(value = "质检单号")
    private String qualityControlOrderNumber;
    /**
     * 来源单号1
     */
    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;
    /**
     * 运单号
     */
    @ApiModelProperty(value = "运单号")
    private String waybillNumber;
    /**
     * 来源类型1
     */
    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;
    /**
     * 供应商名称1
     */
    @ApiModelProperty(value = "供应商名称")
    private String customerName;
    /**
     * 质检单单据状态1
     */
    @ApiModelProperty(value = "质检单单据状态")
    private Integer orderStatus;
    /**
     * 封箱箱号1
     */
    @ApiModelProperty(value = "封箱箱号")
    private String sealBoxSerialNumber;
    /**
     * 商品sku
     */
    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "质检良品数")
    private Integer goodProductNumber;
    /**
     * 质检完成数量(质检详情：商品数)
     */
    @ApiModelProperty(value = " 质检完成数量(质检详情：商品数)")
    private Integer qualityControlFinishNumber;
    /**
     * 质检sku对应的单据状态1
     */
    @ApiModelProperty(value = "质检sku对应的单据状态")
    private Integer skuStatus;
    /**
     * 质检sku对应的创建时间1
     */
    @ApiModelProperty(value = "质检sku对应的创建时间")
    private Date skuCreateTime;
    /**
     * 质检sku对应的更新时间
     */
    @ApiModelProperty(value = "质检sku对应的更新时间")
    private Date skuUpdateTime;
    /**
     *质检方式1
     */
    @ApiModelProperty(value = "质检方式")
    private String qcType;
    /**
     * 生产日期1
     */
    @ApiModelProperty(value = "生产日期")
    private Date produceTime;
    /**
     * 有效期
     */
    @ApiModelProperty(value = "有效期")
    private Integer qualityTime;
    /**
     * 失效日期
     */
    @ApiModelProperty(value = "失效日期")
    private Date expirationTime;
    /**
     * 异常原因
     */
    @ApiModelProperty(value = "异常原因")
    private String exceptionCause;
    /**
     * 异常箱号
     */
    @ApiModelProperty(value = "异常箱号")
    private String exceptionBoxNumber;
    /**
     * 异常sku数量
     */
    @ApiModelProperty(value = "异常sku数量")
    private Integer exceptionSkuCount;
    /**
     * 异常报告人
     */
    @ApiModelProperty(value = "异常报告人")
    private String exceptionReporterBy;
    /**
     * 异常上报时间
     */
    @ApiModelProperty(value = "异常上报时间")
    private Date exceptionReporterTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "质检创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private Date finishTime;
    /**
     * 计划数量1
     */
    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;
    /**
     * 采购人
     */
    @ApiModelProperty(value = "采购人")
    private String buyer;

    /**
     * 签到人
     */
    @ApiModelProperty(value = "签到人")
    private String signInCreater;

    /**
     * 签到时间
     */
    @ApiModelProperty(value = "签到时间")
    private Date signInTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "来源单详情（运单号信息集合）")
    private List<T> waybillInfoDtoList;

    @ApiModelProperty(value = " 每个来源单质检完成总数量")
    private Integer qcFinishTotalNumber;

    @ApiModelProperty(value = " 退货类型")
    private String sellingBackType;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getQualityControlOrderNumber() {
        return qualityControlOrderNumber;
    }

    public void setQualityControlOrderNumber(String qualityControlOrderNumber) {
        this.qualityControlOrderNumber = qualityControlOrderNumber;
    }

    public String getSourceOrderNumber() {
        return sourceOrderNumber;
    }

    public void setSourceOrderNumber(String sourceOrderNumber) {
        this.sourceOrderNumber = sourceOrderNumber;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getSealBoxSerialNumber() {
        return sealBoxSerialNumber;
    }

    public void setSealBoxSerialNumber(String sealBoxSerialNumber) {
        this.sealBoxSerialNumber = sealBoxSerialNumber;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getGoodProductNumber() {
        return goodProductNumber;
    }

    public void setGoodProductNumber(Integer goodProductNumber) {
        this.goodProductNumber = goodProductNumber;
    }

    public Integer getQualityControlFinishNumber() {
        return qualityControlFinishNumber;
    }

    public void setQualityControlFinishNumber(Integer qualityControlFinishNumber) {
        this.qualityControlFinishNumber = qualityControlFinishNumber;
    }

    public String getQcStationId() {
        return qcStationId;
    }

    public void setQcStationId(String qcStationId) {
        this.qcStationId = qcStationId;
    }

    public Integer getSkuStatus() {
        return skuStatus;
    }

    public void setSkuStatus(Integer skuStatus) {
        this.skuStatus = skuStatus;
    }

    public Date getSkuCreateTime() {
        return skuCreateTime;
    }

    public void setSkuCreateTime(Date skuCreateTime) {
        this.skuCreateTime = skuCreateTime;
    }

    public Date getSkuUpdateTime() {
        return skuUpdateTime;
    }

    public void setSkuUpdateTime(Date skuUpdateTime) {
        this.skuUpdateTime = skuUpdateTime;
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

    public String getExceptionCause() {
        return exceptionCause;
    }

    public void setExceptionCause(String exceptionCause) {
        this.exceptionCause = exceptionCause;
    }

    public String getExceptionBoxNumber() {
        return exceptionBoxNumber;
    }

    public void setExceptionBoxNumber(String exceptionBoxNumber) {
        this.exceptionBoxNumber = exceptionBoxNumber;
    }

    public Integer getExceptionSkuCount() {
        return exceptionSkuCount;
    }

    public void setExceptionSkuCount(Integer exceptionSkuCount) {
        this.exceptionSkuCount = exceptionSkuCount;
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

    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSignInCreater() {
        return signInCreater;
    }

    public void setSignInCreater(String signInCreater) {
        this.signInCreater = signInCreater;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<T> getWaybillInfoDtoList() {
        return waybillInfoDtoList;
    }

    public void setWaybillInfoDtoList(List<T> waybillInfoDtoList) {
        this.waybillInfoDtoList = waybillInfoDtoList;
    }

    public String getCardBoardId() {
        return cardBoardId;
    }

    public void setCardBoardId(String cardBoardId) {
        this.cardBoardId = cardBoardId;
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public Integer getQcFinishTotalNumber() {
        return qcFinishTotalNumber;
    }

    public void setQcFinishTotalNumber(Integer qcFinishTotalNumber) {
        this.qcFinishTotalNumber = qcFinishTotalNumber;
    }

    public String getSellingBackType() {
        return sellingBackType;
    }

    public void setSellingBackType(String sellingBackType) {
        this.sellingBackType = sellingBackType;
    }
}
