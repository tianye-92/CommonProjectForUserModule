package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检单信息（优化类）
 * @date 2019/6/11 21:00
 */
public class QcListResVDto {

    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "仓库名")
    private String warehouse;

    @ApiModelProperty(value = "质检单号")
    private String qualityControlOrderNumber;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;

    @ApiModelProperty(value = "供应商名称")
    private String customerName;

    @ApiModelProperty(value = "质检方式")
    private String qcType;

    @ApiModelProperty(value = "质检单单据状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "创建人")
    private String qcPerson;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    //================以下为质检单详情需要前端带过去的信息（还有上面的部分）=========================

    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;

    @ApiModelProperty(value = " 每个来源单质检完成总数量")
    private Integer qcFinishTotalNumber;

    @ApiModelProperty(value = "采购人")
    private String buyer;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
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

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getQcPerson() {
        return qcPerson;
    }

    public void setQcPerson(String qcPerson) {
        this.qcPerson = qcPerson;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(Integer plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public Integer getQcFinishTotalNumber() {
        return qcFinishTotalNumber;
    }

    public void setQcFinishTotalNumber(Integer qcFinishTotalNumber) {
        this.qcFinishTotalNumber = qcFinishTotalNumber;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
}
