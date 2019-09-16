package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 入库台账返回参数
 * @date 2019/7/8 16:20
 */
public class InboundStandingBookResDto {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @ApiModelProperty(value = "货主名称")
    private String goodsOwner;

    @ApiModelProperty(value = "入库类型")
    private String sourceType;

    @ApiModelProperty(value = "来源单号")
    private String sourceId;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "产品名称")
    private String goodsName;

    @ApiModelProperty(value = "本地分类")
    private String localType;

    @ApiModelProperty(value = "质检商品数")
    private Integer qcGoodsNum;

    @ApiModelProperty(value = "上架商品数")
    private Integer putawayNum;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "货主")
    private String shipper;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "上架商品总数")
    private Integer putawayTotalNum;

    @ApiModelProperty(value = "质检商品总数")
    private Integer qcTotalNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getGoodsOwner() {
        return goodsOwner;
    }

    public void setGoodsOwner(String goodsOwner) {
        this.goodsOwner = goodsOwner;
    }


    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getLocalType() {
        return localType;
    }

    public void setLocalType(String localType) {
        this.localType = localType;
    }

    public Integer getQcGoodsNum() {
        return qcGoodsNum;
    }

    public void setQcGoodsNum(Integer qcGoodsNum) {
        this.qcGoodsNum = qcGoodsNum;
    }

    public Integer getPutawayNum() {
        return putawayNum;
    }

    public void setPutawayNum(Integer putawayNum) {
        this.putawayNum = putawayNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPutawayTotalNum() {
        return putawayTotalNum;
    }

    public void setPutawayTotalNum(Integer putawayTotalNum) {
        this.putawayTotalNum = putawayTotalNum;
    }

    public Integer getQcTotalNum() {
        return qcTotalNum;
    }

    public void setQcTotalNum(Integer qcTotalNum) {
        this.qcTotalNum = qcTotalNum;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }
}
