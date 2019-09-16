package com.brandslink.cloud.inbound.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 运单维度
 *
 * @ClassName YunDan
 * @Author tianye
 * @Date 2019/6/11 11:21
 * @Version 1.0
 */
public class YunDan {

    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String supplier;
    /**
     *运单号
     */
    @ApiModelProperty(value = "运单号")
    private String waybillId;
    /**
     *楼层（基础数据）
     */
    @ApiModelProperty(value = "楼层（基础数据）")
    private String foodsFloor;

    @ApiModelProperty(value = "商品sku")
    private List<ShangPinSKU> list;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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

    public List<ShangPinSKU> getList() {
        return list;
    }

    public void setList(List<ShangPinSKU> list) {
        this.list = list;
    }
}
