package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 绑定容器请求参数
 * @date 2019/6/12 20:10
 */
public class QcSealBoxReqDto {

    @ApiModelProperty(value = "运单id")
    private String sid;

    @ApiModelProperty(value = "商品计划数量")
    private Integer skuPlanNumber;

    @ApiModelProperty(value = "商品计划封箱数量")
    private String skuDefinedNumber;

    @ApiModelProperty(value = "封箱箱号")
    private String sealBoxSerialNumber;

    @ApiModelProperty(value = "楼层")
    private String skuFloor;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getSkuPlanNumber() {
        return skuPlanNumber;
    }

    public void setSkuPlanNumber(Integer skuPlanNumber) {
        this.skuPlanNumber = skuPlanNumber;
    }

    public String getSkuDefinedNumber() {
        return skuDefinedNumber;
    }

    public void setSkuDefinedNumber(String skuDefinedNumber) {
        this.skuDefinedNumber = skuDefinedNumber;
    }

    public String getSealBoxSerialNumber() {
        return sealBoxSerialNumber;
    }

    public void setSealBoxSerialNumber(String sealBoxSerialNumber) {
        this.sealBoxSerialNumber = sealBoxSerialNumber;
    }

    public String getSkuFloor() {
        return skuFloor;
    }

    public void setSkuFloor(String skuFloor) {
        this.skuFloor = skuFloor;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
