package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;


/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检单
 * @date 2019/6/4 9:18
 */
public class QcListRequestDto {
    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private List<String> warehouse;
    /**
     * 质检单号
     */
    @ApiModelProperty(value = "质检单号")
    private String qualityControlOrderNumber;
    /**
     * 来源单号
     */
    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;
    /**
     * 来源类型
     */
    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "客户")
    private String customerName;
    /**
     * 单据状态
     */
    @ApiModelProperty(value = "单据状态")
    private Integer orderStatus;
    /**
     * 创建时间开始
     */
    @ApiModelProperty(value = "创建时间开始")
    private String createTimeStart;
    /**
     * 创建时间结束
     */
    @ApiModelProperty(value = "创建时间结束")
    private String createTimeEnd;
    /**
     * 查询类型（0：查询质检单列表；1:查询质检明细）
     */
    @ApiModelProperty(value = "查询类型（0：查询质检单列表；1:查询质检明细）",required = true)
    private Integer flag;

    @ApiModelProperty(value = "排序：升序：asc，倒序：desc（默认）")
    private String order = "desc";

    @ApiModelProperty(value = "可排序字段")
    private String orderField;

    @ApiModelProperty(value = "当前页号")
    private String pageNum;

    @ApiModelProperty(value = "每页显示多少条数据")
    private String pageSize;

    public List<String> getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(List<String> warehouse) {
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

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
