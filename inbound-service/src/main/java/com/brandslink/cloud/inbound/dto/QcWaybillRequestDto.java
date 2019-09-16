package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检详情传入参数
 * @date 2019/6/1 13:55
 */
public class QcWaybillRequestDto implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "卡板号")
    private String cardBoardId;

    @ApiModelProperty(value = "消退单号")
    private String sellingBackId;

    @ApiModelProperty(value = "图片路径")
    private String pictureUrl;

    @ApiModelProperty(value = "QC质检单号")
    private String qualityControlOrderNumber;
    /**
     *来源单号
     */
    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;
    /**
     *运单号
     */
    @ApiModelProperty(value = "运单号")
    private String waybillNumber;
    /**
     *商品SKU
     */
    @ApiModelProperty(value = "商品SKU")
    private String sku;
    /**
     * 良品数
     */
    @ApiModelProperty(value = "良品数")
    private Integer goodProductNumber;
    /**
     * 次品数
     */
    @ApiModelProperty(value = "次品数")
    private Integer unusableProductNumber;
    /**
     * 商品计划数量
     */
    @ApiModelProperty(value = "商品计划数量")
    private Integer skuPlanNumber;
    /**
     * QC完成数量
     */
    @ApiModelProperty(value = "QC完成数量")
    private Integer qualityControlFinishNumber;
    /**
     * 封箱数
     */
    @ApiModelProperty(value = "封箱数")
    private Integer sealBoxFinishNumber;
    /**
     * 未封箱数
     */
    @ApiModelProperty(value = "未封箱数")
    private Integer sealBoxUnfinishNumber;
    /**
     * 封箱箱号
     */
    @ApiModelProperty(value = "封箱箱号")
    private Integer sealBoxSerialNumber;
    /**
     * SKU单据状态(0:待质检；1:质检中；2:质检完成)
     */
    @ApiModelProperty(value = "SKU单据状态(0:待质检；1:质检中；2:质检完成)")
    private Byte skuStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "质检台id")
    private String qcStationId;

    @ApiModelProperty(value = "质检人id")
    private Long createId;

    @ApiModelProperty(value = "协助人")
    private String assistants;

    @ApiModelProperty(value = "质检方式：0：到货质检；1：销退质检")
    private String flag;
    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getSellingBackId() {
        return sellingBackId;
    }

    public void setSellingBackId(String sellingBackId) {
        this.sellingBackId = sellingBackId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCardBoardId() {
        return cardBoardId;
    }

    public void setCardBoardId(String cardBoardId) {
        this.cardBoardId = cardBoardId;
    }

    public String getQualityControlOrderNumber() {
        return qualityControlOrderNumber;
    }

    public void setQualityControlOrderNumber(String qualityControlOrderNumber) {
        this.qualityControlOrderNumber = qualityControlOrderNumber;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
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

    public Integer getUnusableProductNumber() {
        return unusableProductNumber;
    }

    public void setUnusableProductNumber(Integer unusableProductNumber) {
        this.unusableProductNumber = unusableProductNumber;
    }

    public Integer getSkuPlanNumber() {
        return skuPlanNumber;
    }

    public void setSkuPlanNumber(Integer skuPlanNumber) {
        this.skuPlanNumber = skuPlanNumber;
    }

    public Integer getQualityControlFinishNumber() {
        return qualityControlFinishNumber;
    }

    public void setQualityControlFinishNumber(Integer qualityControlFinishNumber) {
        this.qualityControlFinishNumber = qualityControlFinishNumber;
    }

    public Integer getSealBoxFinishNumber() {
        return sealBoxFinishNumber;
    }

    public void setSealBoxFinishNumber(Integer sealBoxFinishNumber) {
        this.sealBoxFinishNumber = sealBoxFinishNumber;
    }

    public Integer getSealBoxUnfinishNumber() {
        return sealBoxUnfinishNumber;
    }

    public void setSealBoxUnfinishNumber(Integer sealBoxUnfinishNumber) {
        this.sealBoxUnfinishNumber = sealBoxUnfinishNumber;
    }

    public Integer getSealBoxSerialNumber() {
        return sealBoxSerialNumber;
    }

    public void setSealBoxSerialNumber(Integer sealBoxSerialNumber) {
        this.sealBoxSerialNumber = sealBoxSerialNumber;
    }

    public Byte getSkuStatus() {
        return skuStatus;
    }

    public void setSkuStatus(Byte skuStatus) {
        this.skuStatus = skuStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQcStationId() {
        return qcStationId;
    }

    public void setQcStationId(String qcStationId) {
        this.qcStationId = qcStationId;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public String getAssistants() {
        return assistants;
    }

    public void setAssistants(String assistants) {
        this.assistants = assistants;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
