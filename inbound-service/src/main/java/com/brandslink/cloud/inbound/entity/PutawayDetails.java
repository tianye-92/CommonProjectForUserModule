package com.brandslink.cloud.inbound.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 实体类对应的数据表为：  t_putaway_details
 * @author zhaojiaxing
 * @date 2019-06-14 16:01:32
 */
@ApiModel(value ="PutawayDetails")
public class PutawayDetails implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long sid;

    @ApiModelProperty(value = "上架单号")
    private String putawayId;

    @ApiModelProperty(value = "运单号")
    private String waybillNumber;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "sku计划上架数")
    private Integer plannedPutawayNumber;

    @ApiModelProperty(value = "sku实际上架数")
    private Integer actualPutawayNumber;

    @ApiModelProperty(value = "单据状态（0:待上架；1:上架中;2:上架完成）")
    private Integer skuPutawayStatus;

    @ApiModelProperty(value = "推荐货位")
    private String recommendedLocation;

    @ApiModelProperty(value = "实际上架货位")
    private String actualLocation;

    @ApiModelProperty(value = "上架人id")
    private Long createrId;

    @ApiModelProperty(value = "上架人")
    private String createrBy;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "上架时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date putawayTime;

    @ApiModelProperty(value = "生产日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date produceTime;

    @ApiModelProperty(value = "保质期")
    private String qualityTime;

    @ApiModelProperty(value = "失效日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_putaway_details
     *
     * @mbg.generated 2019-06-14 16:01:32
     */
    private static final long serialVersionUID = 1L;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getPutawayId() {
        return putawayId;
    }

    public void setPutawayId(String putawayId) {
        this.putawayId = putawayId == null ? null : putawayId.trim();
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber == null ? null : waybillNumber.trim();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public Integer getPlannedPutawayNumber() {
        return plannedPutawayNumber;
    }

    public void setPlannedPutawayNumber(Integer plannedPutawayNumber) {
        this.plannedPutawayNumber = plannedPutawayNumber;
    }

    public Integer getActualPutawayNumber() {
        return actualPutawayNumber;
    }

    public void setActualPutawayNumber(Integer actualPutawayNumber) {
        this.actualPutawayNumber = actualPutawayNumber;
    }

    public Integer getSkuPutawayStatus() {
        return skuPutawayStatus;
    }

    public void setSkuPutawayStatus(Integer skuPutawayStatus) {
        this.skuPutawayStatus = skuPutawayStatus;
    }

    public String getRecommendedLocation() {
        return recommendedLocation;
    }

    public void setRecommendedLocation(String recommendedLocation) {
        this.recommendedLocation = recommendedLocation == null ? null : recommendedLocation.trim();
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation == null ? null : actualLocation.trim();
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public String getCreaterBy() {
        return createrBy;
    }

    public void setCreaterBy(String createrBy) {
        this.createrBy = createrBy == null ? null : createrBy.trim();
    }

    public Date getCreaterTime() {
        return createrTime;
    }

    public void setCreaterTime(Date createrTime) {
        this.createrTime = createrTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getPutawayTime() {
        return putawayTime;
    }

    public void setPutawayTime(Date putawayTime) {
        this.putawayTime = putawayTime;
    }

    public Date getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(Date produceTime) {
        this.produceTime = produceTime;
    }

    public String getQualityTime() {
        return qualityTime;
    }

    public void setQualityTime(String qualityTime) {
        this.qualityTime = qualityTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}