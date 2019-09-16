package com.brandslink.cloud.inbound.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * 商品维度
 *
 * @ClassName ShangPinSKU
 * @Author tianye
 * @Date 2019/6/11 11:22
 * @Version 1.0
 */
public class ShangPinSKU {

    /**
     *图片（基础数据）
     */
    @ApiModelProperty(value = "图片（基础数据）")
    private String pictureUrl;
    /**
     *SKU
     */
    @ApiModelProperty(value = "SKU")
    private String SKU;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;
    /**
     *缺货数量
     */
    @ApiModelProperty(value = "差异数量")
    private Integer differQuantity;
    /**
     *计划数量
     */
    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;
    /**
     *QC完成数量
     */
    @ApiModelProperty(value = "QC完成数量")
    private Integer qualityControlFinishNumber;
    /**
     *已封箱数量
     */
    @ApiModelProperty(value = "已封箱数量")
    private Integer sealBoxFinishNumber;
    /**
     *未封箱数量
     */
    @ApiModelProperty(value = "未封箱数量")
    private Integer sealBoxUnfinishNumber;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getDifferQuantity() {
        return differQuantity;
    }

    public void setDifferQuantity(Integer differQuantity) {
        this.differQuantity = differQuantity;
    }

    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
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
}
