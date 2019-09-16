package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 运单详情返回信息
 * @date 2019/6/17 14:18
 */
public class QcDetailsResDto<T> {

    @ApiModelProperty(value = "来源单号")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;

    @ApiModelProperty(value = "签到人")
    private String signInCreater;

    @ApiModelProperty(value = "质检方式")
    private String qcType;

    @ApiModelProperty(value = "来源单对应的运单号信息集合）")
    private List<T> waybillInfoDtoList;

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

    public String getSignInCreater() {
        return signInCreater;
    }

    public void setSignInCreater(String signInCreater) {
        this.signInCreater = signInCreater;
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public List<T> getWaybillInfoDtoList() {
        return waybillInfoDtoList;
    }

    public void setWaybillInfoDtoList(List<T> waybillInfoDtoList) {
        this.waybillInfoDtoList = waybillInfoDtoList;
    }
}
