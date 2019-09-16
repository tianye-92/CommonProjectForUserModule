package com.brandslink.cloud.inbound.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 质检单列表
 * 实体类对应的数据表为：  t_quality_control_list
 * @author zc
 * @date 2019-07-08 13:53:16
 */
@ApiModel(value ="QualityControlList")
public class QualityControlList implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "质检单号（系统自动生成，QC+年（后两位）+月+日+六位流水）")
    private String qualityControlOrderNumber;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "运单号")
    private String waybillNumber;

    @ApiModelProperty(value = "运单计划质检数")
    private Integer qcPlanTotalNumber;

    @ApiModelProperty(value = "质检完成总数")
    private Integer qcFinishTotalNumber;

    @ApiModelProperty(value = "质检单单据状态(1:待质检；2:质检中；3:质检完成)")
    private Byte orderStatus;

    @ApiModelProperty(value = "创建人id")
    private Long createId;

    @ApiModelProperty(value = "创建人名称")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新人id")
    private Long updateId;

    @ApiModelProperty(value = "更新人名称")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "完成时间（状态为质检完成的质检单）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    @ApiModelProperty(value = "协助人信息")
    private String assistants;

    @ApiModelProperty(value = "预留字段1")
    private String reserved;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_quality_control_list
     *
     * @mbg.generated 2019-07-08 13:53:16
     */
    private static final long serialVersionUID = 1L;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getQualityControlOrderNumber() {
        return qualityControlOrderNumber;
    }

    public void setQualityControlOrderNumber(String qualityControlOrderNumber) {
        this.qualityControlOrderNumber = qualityControlOrderNumber == null ? null : qualityControlOrderNumber.trim();
    }

    public String getSourceOrderNumber() {
        return sourceOrderNumber;
    }

    public void setSourceOrderNumber(String sourceOrderNumber) {
        this.sourceOrderNumber = sourceOrderNumber == null ? null : sourceOrderNumber.trim();
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber == null ? null : waybillNumber.trim();
    }

    public Integer getQcPlanTotalNumber() {
        return qcPlanTotalNumber;
    }

    public void setQcPlanTotalNumber(Integer qcPlanTotalNumber) {
        this.qcPlanTotalNumber = qcPlanTotalNumber;
    }

    public Integer getQcFinishTotalNumber() {
        return qcFinishTotalNumber;
    }

    public void setQcFinishTotalNumber(Integer qcFinishTotalNumber) {
        this.qcFinishTotalNumber = qcFinishTotalNumber;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getAssistants() {
        return assistants;
    }

    public void setAssistants(String assistants) {
        this.assistants = assistants == null ? null : assistants.trim();
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}