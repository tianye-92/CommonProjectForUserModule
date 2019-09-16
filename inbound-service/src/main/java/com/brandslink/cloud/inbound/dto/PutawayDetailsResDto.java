package com.brandslink.cloud.inbound.dto;

import com.brandslink.cloud.inbound.entity.PutawayDetails;
import com.brandslink.cloud.inbound.entity.PutawayException;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 上架单详情返回信息
 * @date 2019/6/14 14:27
 */
public class PutawayDetailsResDto<T> {

    @ApiModelProperty(value = "计划上架总数")
    private Integer planedPutawaySumNum;

    @ApiModelProperty(value = "实际上架总数")
    private Integer actualPutawaySumNum;

    @ApiModelProperty(value = "差异数")
    private Integer diffNum;

    @ApiModelProperty(value = "上架明细")
    private List<T> PutawayDetailsList;

    public Integer getPlanedPutawaySumNum() {
        return planedPutawaySumNum;
    }

    public void setPlanedPutawaySumNum(Integer planedPutawaySumNum) {
        this.planedPutawaySumNum = planedPutawaySumNum;
    }

    public Integer getActualPutawaySumNum() {
        return actualPutawaySumNum;
    }

    public void setActualPutawaySumNum(Integer actualPutawaySumNum) {
        this.actualPutawaySumNum = actualPutawaySumNum;
    }

    public Integer getDiffNum() {
        return diffNum;
    }

    public void setDiffNum(Integer diffNum) {
        this.diffNum = diffNum;
    }

    public List<T> getPutawayDetailsList() {
        return PutawayDetailsList;
    }

    public void setPutawayDetailsList(List<T> putawayDetailsList) {
        PutawayDetailsList = putawayDetailsList;
    }
}
