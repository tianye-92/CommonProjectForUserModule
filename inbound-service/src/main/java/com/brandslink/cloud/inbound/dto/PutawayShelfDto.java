package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value ="PutawayShelfDto")
public class PutawayShelfDto {
	
	@ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "计划上架数")
    private Integer plannedPutawayNumber;

    @ApiModelProperty(value = "实际上架数")
    private Integer actualPutawayNumber;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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
	
	
	
    
    
    
    
    
    
	
	
	

}
