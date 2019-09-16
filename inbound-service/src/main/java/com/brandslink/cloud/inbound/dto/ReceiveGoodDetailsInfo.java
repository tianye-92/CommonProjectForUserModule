package com.brandslink.cloud.inbound.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 到货商品详情表
 * 实体类对应的数据表为：  receive_good_details
 * @author zc
 * @date 2019-07-08 09:17:39
 */
@ApiModel(value ="ReceiveGoodDetailsInfo")
public class ReceiveGoodDetailsInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "来源单号(关联到货单 查询来源信息)")
    private String sourceId;

    @ApiModelProperty(value = "送货箱号")
    private String boxId;

    @ApiModelProperty(value = "运单号")
    private String waybillId;

    @ApiModelProperty(value = "商品 sku")
    private String sku;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品类型")
    private String goodType;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "生产日期")
    private String produceTime;

    @ApiModelProperty(value = "保质期")
    private String qualityTime;

    @ApiModelProperty(value = "过期时间")
    private String expirationTime;

    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;

    @ApiModelProperty(value = "收货数量")
    private Integer deliveryQuantity;

    @ApiModelProperty(value = "差异数量")
    private Integer differQuantity;

    @ApiModelProperty(value = "接收员")
    private String receiver;

    @ApiModelProperty(value = "接收员 id")
    private Integer receiverId;

    @ApiModelProperty(value = "接收时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    @ApiModelProperty(value = "商品详情单 创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "卡板号")
    private String cardBoardId;

    @ApiModelProperty(value = "异常标记 为1")
    private Integer exceptionMark;

    @ApiModelProperty(value = "优先级")
    private Short priority;

    @ApiModelProperty(value = "物流商")
    private String logistics;
    
    private String supplier;
    
    private String type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getBoxId() {
		return boxId;
	}

	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}

	public String getWaybillId() {
		return waybillId;
	}

	public void setWaybillId(String waybillId) {
		this.waybillId = waybillId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public String getGoodType() {
		return goodType;
	}

	public void setGoodType(String goodType) {
		this.goodType = goodType;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getProduceTime() {
		return produceTime;
	}

	public void setProduceTime(String produceTime) {
		this.produceTime = produceTime;
	}

	public String getQualityTime() {
		return qualityTime;
	}

	public void setQualityTime(String qualityTime) {
		this.qualityTime = qualityTime;
	}

	public String getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Integer getPlannedQuantity() {
		return plannedQuantity;
	}

	public void setPlannedQuantity(Integer plannedQuantity) {
		this.plannedQuantity = plannedQuantity;
	}

	public Integer getDeliveryQuantity() {
		return deliveryQuantity;
	}

	public void setDeliveryQuantity(Integer deliveryQuantity) {
		this.deliveryQuantity = deliveryQuantity;
	}

	public Integer getDifferQuantity() {
		return differQuantity;
	}

	public void setDifferQuantity(Integer differQuantity) {
		this.differQuantity = differQuantity;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCardBoardId() {
		return cardBoardId;
	}

	public void setCardBoardId(String cardBoardId) {
		this.cardBoardId = cardBoardId;
	}

	public Integer getExceptionMark() {
		return exceptionMark;
	}

	public void setExceptionMark(Integer exceptionMark) {
		this.exceptionMark = exceptionMark;
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public String getLogistics() {
		return logistics;
	}

	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "ReceiveGoodDetailsInfo{" +
				"id=" + id +
				", sourceId='" + sourceId + '\'' +
				", boxId='" + boxId + '\'' +
				", waybillId='" + waybillId + '\'' +
				", sku='" + sku + '\'' +
				", goodName='" + goodName + '\'' +
				", goodType='" + goodType + '\'' +
				", weight=" + weight +
				", produceTime='" + produceTime + '\'' +
				", qualityTime='" + qualityTime + '\'' +
				", expirationTime='" + expirationTime + '\'' +
				", plannedQuantity=" + plannedQuantity +
				", deliveryQuantity=" + deliveryQuantity +
				", differQuantity=" + differQuantity +
				", receiver='" + receiver + '\'' +
				", receiverId=" + receiverId +
				", receiveTime=" + receiveTime +
				", createTime=" + createTime +
				", cardBoardId='" + cardBoardId + '\'' +
				", exceptionMark=" + exceptionMark +
				", priority=" + priority +
				", logistics='" + logistics + '\'' +
				", supplier='" + supplier + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}