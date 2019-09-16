package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.PutawayDetailsResDto;
import com.brandslink.cloud.inbound.entity.PutawayDetails;

import java.util.List;

public interface PutawayDetailsMapper extends BaseMapper<PutawayDetails> {
    /**
     * @description: 根据上架单号查询上架详情及生产日期
     * @param putawayId
     * @return
     */
    List<PutawayDetails> selectByPutawayId(String putawayId);
    
    
    void updateSelective(PutawayDetails putawayDetails);

    /**
     * @description: 根据上架单号查询上架详情
     * @param putawayId
     * @return
     */
    List<Integer> selectListByPutawayId(String putawayId);
}