package com.brandslink.cloud.inbound.service;

import java.util.List;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.PutawayDetailsResDto;
import com.brandslink.cloud.inbound.dto.PutawayListReqDto;
import com.brandslink.cloud.inbound.entity.PutawayException;
import com.brandslink.cloud.inbound.entity.PutawayList;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 上架业务层
 * @date 2019/6/14 11:19
 */
public interface PutawayListService {

    /**
     * @description: 查询上架单（分页）
     * @param putawayListReqDto
     * @return
     */
    Page<List<PutawayList>> selectByPutawayObj(PutawayListReqDto putawayListReqDto);

    /**
     * @description: 查询上架详情
     * @param putawayId
     * @return
     */
    PutawayDetailsResDto selectPutawayDetailsByPutawayId(String putawayId);
    
    
    
    /**
     * 添加上架异常信息
     * @param putawayException
     */
    void addPutawayException(PutawayException putawayException);

    /**
     * @description: 根据上架单号查询上架异常明细
     * @param putawayId：上架单号
     * @return
     */
    List<PutawayException> getExceptionDetailsByPutawayId(String putawayId);
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
