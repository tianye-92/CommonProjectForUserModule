package com.brandslink.cloud.inbound.dto;

import com.brandslink.cloud.inbound.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 上架单请求参数
 * @date 2019/6/14 10:54
 */
@ApiModel(value ="PutawayListReqDto")
public class PutawayListReqDto extends BaseEntity {
    @ApiModelProperty(value = "仓库编号")
    private  List<String> warehouseCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "上架单号")
    private String putawayId;

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "上架状态（0:待上架；1：上架中；2：上架完成）")
    private Byte putawayStatus;

    @ApiModelProperty(value = "来源类型")
    private Byte sourceType;

    @ApiModelProperty(value = "创建时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;

    @ApiModelProperty(value = "完成时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String finishedTimeStart;

    @ApiModelProperty(value = "完成时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String finishedTimeEnd;

    public List<String> getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(List<String> warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPutawayId() {
        return putawayId;
    }

    public void setPutawayId(String putawayId) {
        this.putawayId = putawayId;
    }

    public String getSourceOrderNumber() {
        return sourceOrderNumber;
    }

    public void setSourceOrderNumber(String sourceOrderNumber) {
        this.sourceOrderNumber = sourceOrderNumber;
    }

    public Byte getPutawayStatus() {
        return putawayStatus;
    }

    public void setPutawayStatus(Byte putawayStatus) {
        this.putawayStatus = putawayStatus;
    }

    public Byte getSourceType() {
        return sourceType;
    }

    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
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

    public String getFinishedTimeStart() {
        return finishedTimeStart;
    }

    public void setFinishedTimeStart(String finishedTimeStart) {
        this.finishedTimeStart = finishedTimeStart;
    }

    public String getFinishedTimeEnd() {
        return finishedTimeEnd;
    }

    public void setFinishedTimeEnd(String finishedTimeEnd) {
        this.finishedTimeEnd = finishedTimeEnd;
    }

}
