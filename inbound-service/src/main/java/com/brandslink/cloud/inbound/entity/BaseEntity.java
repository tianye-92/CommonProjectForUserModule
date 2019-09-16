package com.brandslink.cloud.inbound.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 分页及排序公共属性
 * @date 2019/7/15 17:39
 */
public abstract class BaseEntity {
    @ApiModelProperty(value = "排序：升序：asc，倒序：desc（默认）")
    private String order = "desc";

    @ApiModelProperty(value = "可排序字段")
    private String orderField;

    @ApiModelProperty(value = "当前页号")
    private String pageNum;

    @ApiModelProperty(value = "每页显示多少条数据")
    private String pageSize;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
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
}
