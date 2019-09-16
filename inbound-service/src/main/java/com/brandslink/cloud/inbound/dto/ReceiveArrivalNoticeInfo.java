package com.brandslink.cloud.inbound.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 到货通知单表 实体类对应的数据表为： receive_arrival_notice
 * 
 * @author zc
 * @date 2019-07-08 09:17:39
 */
@ApiModel(value = "ReceiveArrivalNoticeInfo")
public class ReceiveArrivalNoticeInfo implements Serializable {
	private static final long serialVersionUID = 807707771369588937L;

	@ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "到货通知单号")
    private String arrivalNoticeId;

    @ApiModelProperty(value = "来源单号  ( 关联详情表    查询商品详情信息)")
    private String sourceId;

    @ApiModelProperty(value = "货主")
    private String shipper;

    @ApiModelProperty(value = "客户code")
    private String customer;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "来源类型 (1.采购入库  2. 销退入库 3. 调拨入库)")
    private String sourceType;

    @ApiModelProperty(value = "质检方式 (1.全检  2.抽检  3. 免检)")
    private String qualityType;

    @ApiModelProperty(value = "仓库id")
    private String warehouse;

    @ApiModelProperty(value = "单据状态 (1.待收货 2. 收货中 3. 部分收货 4. 收货完成)")
    private String status;

    @ApiModelProperty(value = "计划数量")
    private Integer plannedQuantity;

    @ApiModelProperty(value = "收货数量")
    private Integer deliveryQuantity;

    @ApiModelProperty(value = "包裹数")
    private Integer parcelQuantity;

    @ApiModelProperty(value = "差异数")
    private Integer differQuantity;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "创建人id")
    private Integer createrId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "预到仓时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String plannedTime;

    @ApiModelProperty(value = "实到仓时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalTime;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "是否卸货  （0 否  1 是）")
    private Integer isUnload;

    @ApiModelProperty(value = "最后修改人")
    private String lastUpdateBy;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "确认人")
    private String affirmBy;

    @ApiModelProperty(value = "确认时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date affirmTime;

    @ApiModelProperty(value = "确认状态   (0 待确认 1 已确认)")
    private Integer affirmStatus;

    @ApiModelProperty(value = "时间类型  1 创建时间 2 预到仓时间 3 实到仓时间")
	private String dateType;

    @ApiModelProperty(value = "排序时间类型  1 创建时间 2 预到仓时间 3 实到仓时间 4 更新时间  5 确认时间")
    private String orderDateType;
    
	@ApiModelProperty(value = "排序类型  asc 升序 desc 降序 ")
	private String orderStatus;

	@ApiModelProperty(value = "查询 开始时间")
	private String beginTime;

	@ApiModelProperty(value = "查询 结束时间")
	private String afterTime;

	@ApiModelProperty(value = "运单号")
	private String wayBillId;

	@ApiModelProperty(value = "仓库id集合")
	private List<String>  warehouseCodes;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getArrivalNoticeId() {
		return arrivalNoticeId;
	}

	public void setArrivalNoticeId(String arrivalNoticeId) {
		this.arrivalNoticeId = arrivalNoticeId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getShipper() {
		return shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
	}
	
	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getQualityType() {
		return qualityType;
	}

	public void setQualityType(String qualityType) {
		this.qualityType = qualityType;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getParcelQuantity() {
		return parcelQuantity;
	}

	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}

	public Integer getDifferQuantity() {
		return differQuantity;
	}

	public void setDifferQuantity(Integer differQuantity) {
		this.differQuantity = differQuantity;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Integer getCreaterId() {
		return createrId;
	}

	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(String plannedTime) {
		this.plannedTime = plannedTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getAfterTime() {
		return afterTime;
	}

	public void setAfterTime(String afterTime) {
		this.afterTime = afterTime;
	}

	public String getWayBillId() {
		return wayBillId;
	}

	public void setWayBillId(String wayBillId) {
		this.wayBillId = wayBillId;
	}

	public String getOrderDateType() {
		return orderDateType;
	}

	public void setOrderDateType(String orderDateType) {
		this.orderDateType = orderDateType;
	}

	public List<String> getWarehouseCodes() {
		return warehouseCodes;
	}

	public void setWarehouseCodes(List<String> warehouseCodes) {
		this.warehouseCodes = warehouseCodes;
	}

	public Integer getIsUnload() {
		return isUnload;
	}

	public void setIsUnload(Integer isUnload) {
		this.isUnload = isUnload;
	}

	public String getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAffirmBy() {
		return affirmBy;
	}

	public void setAffirmBy(String affirmBy) {
		this.affirmBy = affirmBy;
	}

	public Date getAffirmTime() {
		return affirmTime;
	}

	public void setAffirmTime(Date affirmTime) {
		this.affirmTime = affirmTime;
	}

	public Integer getAffirmStatus() {
		return affirmStatus;
	}

	public void setAffirmStatus(Integer affirmStatus) {
		this.affirmStatus = affirmStatus;
	}
}