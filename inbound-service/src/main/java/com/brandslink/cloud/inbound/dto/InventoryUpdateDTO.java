package com.brandslink.cloud.inbound.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author lijianjun
 * @Classname InventoryDTO
 * @Description 库存实体类
 * @Date 2019/6/17 18:32
 */
public class InventoryUpdateDTO implements Serializable,Cloneable {

    @ApiModelProperty(value = "推荐货位id")
    private Long shelfLocationId;

    @ApiModelProperty(value = "当前货位id")
    private Long currentShelfLocationId;

    @ApiModelProperty(value = "SKU",required = true)
    @NotBlank(message = "参数字段goodsSku不能为空")
    private String goodsSku;

    @ApiModelProperty(value = "创建/修改人",required = true)
    @NotBlank(message = "参数字段updateBy不能为空")
    private String updateBy;

    @ApiModelProperty(value = "业务类型 = 类型编码/类型值编码(码表) 例:采购入库 = put_type/02",required = true)
    @NotBlank(message = "参数字段businessType不能为空")
    private String businessType;

    @ApiModelProperty(value = "来源单号=到货通知单号/质检单号",required = true)
    @NotBlank(message = "参数字段sourceOrderNumber不能为空")
    private String sourceOrderNumber;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "仓库编码",required = true)
    @NotBlank(message = "参数字段warehouseCode不能为空")
    private String warehouseCode;

    @ApiModelProperty(value = "SKU变动数量")
    @NotNull(message = "参数字段skuNumbers不能为空")
    private Integer skuNumbers;

    @ApiModelProperty(value = "版本号",hidden = true)
    private Long version;

    /**
     * clone 效率较高
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object object = super.clone();
        return object;
    }

	public Long getShelfLocationId() {
		return shelfLocationId;
	}

	public void setShelfLocationId(Long shelfLocationId) {
		this.shelfLocationId = shelfLocationId;
	}

	public Long getCurrentShelfLocationId() {
		return currentShelfLocationId;
	}

	public void setCurrentShelfLocationId(Long currentShelfLocationId) {
		this.currentShelfLocationId = currentShelfLocationId;
	}

	public String getGoodsSku() {
		return goodsSku;
	}

	public void setGoodsSku(String goodsSku) {
		this.goodsSku = goodsSku;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getSourceOrderNumber() {
		return sourceOrderNumber;
	}

	public void setSourceOrderNumber(String sourceOrderNumber) {
		this.sourceOrderNumber = sourceOrderNumber;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public Integer getSkuNumbers() {
		return skuNumbers;
	}

	public void setSkuNumbers(Integer skuNumbers) {
		this.skuNumbers = skuNumbers;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
    
    
    
    
    
    
}
