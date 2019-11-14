package me.mizhoux.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用服务商关联表
 * 实体类对应的数据表为：  t_app_facilitator_info
 * @author tianye
 * @date 2019-11-14 22:12:04
 */
@ApiModel(value ="AppFacilitatorInfoEntity")
public class AppFacilitatorInfoEntity implements Serializable {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Integer appId;

    @ApiModelProperty(value = "服务商id")
    private Integer facilitatorId;

    @ApiModelProperty(value = "服务代码（仓库代码）")
    private String facilitatorCode;

    @ApiModelProperty(value = "权重，优先使用最高值")
    private Byte weight;

    @ApiModelProperty(value = "状态 0：可用 1：不可用")
    private Byte state;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getFacilitatorId() {
        return facilitatorId;
    }

    public void setFacilitatorId(Integer facilitatorId) {
        this.facilitatorId = facilitatorId;
    }

    public String getFacilitatorCode() {
        return facilitatorCode;
    }

    public void setFacilitatorCode(String facilitatorCode) {
        this.facilitatorCode = facilitatorCode == null ? null : facilitatorCode.trim();
    }

    public Byte getWeight() {
        return weight;
    }

    public void setWeight(Byte weight) {
        this.weight = weight;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}