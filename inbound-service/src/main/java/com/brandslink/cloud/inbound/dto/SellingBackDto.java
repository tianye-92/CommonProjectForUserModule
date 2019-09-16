package com.brandslink.cloud.inbound.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.entity.SellingBackDetail;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 实体类对应的数据表为：  selling_back
 * @author zc
 * @date 2019-06-26 10:10:52
 */
@ApiModel(value ="SellingBackDto")
public class SellingBackDto implements Serializable {

    private SellingBack sellingBack;
	
    private List<SellingBackDetail> sellingBackDetail;
    

	public SellingBack getSellingBack() {
		return sellingBack;
	}

	public void setSellingBack(SellingBack sellingBack) {
		this.sellingBack = sellingBack;
	}

	public List<SellingBackDetail> getSellingBackDetail() {
		return sellingBackDetail;
	}

	public void setSellingBackDetail(List<SellingBackDetail> sellingBackDetail) {
		this.sellingBackDetail = sellingBackDetail;
	}
    
	
    
}