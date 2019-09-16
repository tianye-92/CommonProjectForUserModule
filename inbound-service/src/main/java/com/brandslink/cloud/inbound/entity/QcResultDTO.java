package com.brandslink.cloud.inbound.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * TODO
 *
 * @ClassName QcResultDTO
 * @Author tianye
 * @Date 2019/6/11 14:36
 * @Version 1.0
 */
public class QcResultDTO {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Integer id;
    /**
     * 卡板号
     */
    @ApiModelProperty(value = "卡板号")
    private String cardBoardId;
    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private String warehouse;
    /**
     * 质检台(前端数据)
     */
    @ApiModelProperty(value = "质检台(前端数据)")
    private String qcStation;
    /**
     * 来源单号
     */
    @ApiModelProperty(value = "来源单号")
    private String sourceId;
    /**
     * 来源类型
     */
    @ApiModelProperty(value = "来源类型")
    private Integer sourceType;
    /**
     * 采购员
     */
    @ApiModelProperty(value = "采购员")
    private String creater;
    /**
     * 质检方式
     */
    @ApiModelProperty(value = "质检方式")
    private String qcType;

    @ApiModelProperty(value = "运单集合")
    private List<YunDan> list;

    public List<YunDan> getList() {
        return list;
    }

    public void setList(List<YunDan> list) {
        this.list = list;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardBoardId() {
        return cardBoardId;
    }

    public void setCardBoardId(String cardBoardId) {
        this.cardBoardId = cardBoardId;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getQcStation() {
        return qcStation;
    }

    public void setQcStation(String qcStation) {
        this.qcStation = qcStation;
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

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }
}
