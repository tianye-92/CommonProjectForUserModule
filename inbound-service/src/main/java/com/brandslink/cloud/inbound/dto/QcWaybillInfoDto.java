package com.brandslink.cloud.inbound.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检运单详情表
 * @date 2019/5/31 17:13
 */
public class QcWaybillInfoDto<T> implements Serializable {

    @ApiModelProperty(value = "运单号主键id")
    private Integer id;

    @ApiModelProperty(value = "质检单号")
    private String qualityControlOrderNumber;

    @ApiModelProperty(value = "来源单号")
    private String sourceId;

    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;

    @ApiModelProperty(value = "采购员")
    private String creater;

    @ApiModelProperty(value = "质检方式")
    private String qcType;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @ApiModelProperty(value = "运单号")
    private String waybillId;

    @ApiModelProperty(value = "楼层（基础数据）")
    private String foodsFloor;

    @ApiModelProperty(value = "图片（基础数据）")
    private String pictureUrl;

    @JSONField(name = "SKU")
    @ApiModelProperty(value = "SKU")
    private String SKU;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "差异数量")
    private Integer differQuantity;

    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;

    @ApiModelProperty(value = "QC完成数量")
    private Integer qualityControlFinishNumber = 0;

    @ApiModelProperty(value = "质检良品数")
    private Integer goodProductNumber;

    @ApiModelProperty(value = "已封箱数量")
    private Integer sealBoxFinishNumber = 0;

    @ApiModelProperty(value = "未封箱数量")
    private Integer sealBoxUnfinishNumber = 0;

    @ApiModelProperty(value = "运单所在楼层")
    private String skuFloor = "0";

    @ApiModelProperty(value = "卡板号")
    private String cardBoardId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "相同运单下的不同sku集合")
    private List<T> qcWaybillInfoDtoList;

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

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getFoodsFloor() {
        return foodsFloor;
    }

    public void setFoodsFloor(String foodsFloor) {
        this.foodsFloor = foodsFloor;
    }

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
        this.differQuantity = differQuantity ;
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

    public Integer getGoodProductNumber() {
        return goodProductNumber;
    }

    public void setGoodProductNumber(Integer goodProductNumber) {
        this.goodProductNumber = goodProductNumber;
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


    public String getCardBoardId() {
        return cardBoardId;
    }

    public void setCardBoardId(String cardBoardId) {
        this.cardBoardId = cardBoardId;
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

    public List<T> getQcWaybillInfoDtoList() {
        return qcWaybillInfoDtoList;
    }

    public void setQcWaybillInfoDtoList(List<T> qcWaybillInfoDtoList) {
        this.qcWaybillInfoDtoList = qcWaybillInfoDtoList;
    }
}
