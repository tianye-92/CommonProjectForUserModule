package com.brandslink.cloud.inbound.mapper;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.inbound.dto.PutawayListReqDto;
import com.brandslink.cloud.inbound.entity.PutawayList;

import java.util.List;

public interface PutawayListMapper extends BaseMapper<PutawayList> {
    /**
     * @description: 查询上架单（分页）
     * @param putawayListReqDto
     * @return
     */
    List<PutawayList> selectByPutawayObj(PutawayListReqDto putawayListReqDto);

    /**
     * @description:
     * @return
     */
    String selectLastData();
    
    
    Integer updateFinishTime(PutawayList putawayList);
    
    
    /**
     * 查询来源单下的上架单上架状态
     * @param sourceOrderNumber
     * @return
     */
    List<Integer> selectPutawayStatus(String sourceOrderNumber);
}