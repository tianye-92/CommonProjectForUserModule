package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 入库台账请求参数
 * @date 2019/7/8 15:21
 */
public class InboundStandingBookReqDto {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "来源单号")
    private String sourceId;

    @ApiModelProperty(value = "入库类型")
    private Integer sourceType;

    @ApiModelProperty(value = "客户名称")
    private String[] customerName;

    @ApiModelProperty(value = "仓库code")
    private String  warehouseCode;

    @ApiModelProperty(value = "客户")
    private String customer;

    @ApiModelProperty(value = "货主")
    private String shipper;

    @ApiModelProperty(value = "仓库编码数组:用户权限下的仓库 + 请求查询仓库",hidden = true)
    private String[] warehouseCodeArray;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "当前页号")
    private String pageNum;

    @ApiModelProperty(value = "每页显示多少条数据")
    private String pageSize;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public String[] getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String[] customerName) {
        this.customerName = customerName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
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

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String[] getWarehouseCodeArray() {
        return warehouseCodeArray;
    }

    public void setWarehouseCodeArray(String[] warehouseCodeArray) {
        this.warehouseCodeArray = warehouseCodeArray;
    }
}
