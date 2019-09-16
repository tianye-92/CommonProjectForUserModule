package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 销退单信息
 * @date 2019/6/27 9:37
 */
@ApiModel(value ="SellingBackDetailDto")
public class SellingBackDetailDto<T> {
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "质检单号")
    private String qualityControlOrderNumber;

    @ApiModelProperty(value = "销退单号")
    private String sellingBackId;

    @ApiModelProperty(value = "运单号")
    private String waybillId;

    @ApiModelProperty(value = "来源单号")
    private String sourceId;

    @ApiModelProperty(value = "仓库编码")
    private String warehouse;

    @ApiModelProperty(value = "退货类型")
    private String sellingBackType;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "商品良品数")
    private Integer goodProductNumber;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "图片路径")
    private String pictureUrl;

    @ApiModelProperty(value = "销退接收数量")
    private Integer deliveryQuantity;

    @ApiModelProperty(value = "计划数")
    private Integer plannedQuantity;

    @ApiModelProperty(value = "qc完成数量")
    private Integer qualityControlFinishNumber;

    @ApiModelProperty(value = "差异数")
    private Integer differQuantity;

    @ApiModelProperty(value = "封箱数")
    private Integer sealBoxFinishNumber;

    @ApiModelProperty(value = "未封箱数量")
    private Integer sealBoxUnfinishNumber;

    @ApiModelProperty(value = "楼层")
    private String skuFloor;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "同运单下，不同sku集合")
    private List<T> waybillList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQualityControlOrderNumber() {
        return qualityControlOrderNumber;
    }

    public void setQualityControlOrderNumber(String qualityControlOrderNumber) {
        this.qualityControlOrderNumber = qualityControlOrderNumber;
    }

    public String getSellingBackId() {
        return sellingBackId;
    }

    public void setSellingBackId(String sellingBackId) {
        this.sellingBackId = sellingBackId;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getSellingBackType() {
        return sellingBackType;
    }

    public void setSellingBackType(String sellingBackType) {
        this.sellingBackType = sellingBackType;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getDeliveryQuantity() {
        return deliveryQuantity;
    }

    public void setDeliveryQuantity(Integer deliveryQuantity) {
        this.plannedQuantity = deliveryQuantity;
        this.deliveryQuantity = deliveryQuantity;
    }

    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public Integer getDifferQuantity() {
        return differQuantity;
    }

    public void setDifferQuantity(Integer differQuantity) {
        this.differQuantity = differQuantity;
    }

    public Integer getQualityControlFinishNumber() {
        return qualityControlFinishNumber;
    }

    public void setQualityControlFinishNumber(Integer qualityControlFinishNumber) {
        this.differQuantity = qualityControlFinishNumber - plannedQuantity;
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

    public String getSkuFloor() {
        return skuFloor;
    }

    public void setSkuFloor(String skuFloor) {
        this.skuFloor = skuFloor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<T> getWaybillList() {
        return waybillList;
    }

    public void setWaybillList(List<T> waybillList) {
        this.waybillList = waybillList;
    }

}
