package com.brandslink.cloud.inbound.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.inbound.controller.PutawayController;
import com.brandslink.cloud.inbound.entity.QualityBoxPutaway;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.QualityBoxPutawayMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.inbound.dto.PutawayDetailsResDto;
import com.brandslink.cloud.inbound.dto.PutawayListReqDto;
import com.brandslink.cloud.inbound.entity.PutawayDetails;
import com.brandslink.cloud.inbound.entity.PutawayException;
import com.brandslink.cloud.inbound.entity.PutawayList;
import com.brandslink.cloud.inbound.mapper.PutawayDetailsMapper;
import com.brandslink.cloud.inbound.mapper.PutawayExceptionMapper;
import com.brandslink.cloud.inbound.mapper.PutawayListMapper;
import com.brandslink.cloud.inbound.service.PutawayListService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description:
 * @date 2019/6/14 11:21
 */
@Service
public class PutawayListServiceImpl implements PutawayListService {

    private final Logger LOGGER = LoggerFactory.getLogger(PutawayListServiceImpl.class);

    @Resource
    private PutawayListMapper putawayListMapper;

    @Resource
    private PutawayExceptionMapper putawayExceptionMapper;

    @Resource
    private QualityBoxPutawayMapper qualityBoxPutawayMapper;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;


    @Override
    public Page<List<PutawayList>> selectByPutawayObj(PutawayListReqDto putawayListReqDto) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        if (StringUtils.isNotBlank(putawayListReqDto.getPageSize()) && StringUtils.isNotBlank(putawayListReqDto.getPageNum())){
            Page.builder(putawayListReqDto.getPageNum(),putawayListReqDto.getPageSize());
        }
        if (CollectionUtils.isEmpty(putawayListReqDto.getWarehouseCode())){
            putawayListReqDto.setWarehouseCode(Arrays.asList(userInfo.getWarehouseCode().split(",")));
        }
        List<PutawayList> putawayLists = putawayListMapper.selectByPutawayObj(putawayListReqDto);
        for (PutawayList putawayList : putawayLists) {
        	if(2 == putawayList.getPutawayStatus()){
        		List<Integer> status = qualityBoxPutawayMapper.selectPutawayStatusByPutawayId(putawayList.getPutawayId(),putawayList.getWarehouseCode());
        		if(!status.contains(0)){
        			PutawayList putawayList2 = new PutawayList();
        			putawayList2.setPutawayStatus(3);
        			putawayList2.setPutawayId(putawayList.getPutawayId());
        			putawayListMapper.updateByPrimaryKeySelective(putawayList2);
        		}
        	}

			if(1 == putawayList.getPutawayStatus()){
				List<Integer> status = qualityBoxPutawayMapper.selectPutawayStatusByPutawayId(putawayList.getPutawayId(),putawayList.getWarehouseCode());
                if (!status.contains(0)) {
                    PutawayList putawayList2 = new PutawayList();
                    putawayList2.setPutawayStatus(3);
                    putawayList2.setPutawayId(putawayList.getPutawayId());
                    putawayListMapper.updateByPrimaryKeySelective(putawayList2);
                } else if(status.contains(1)){
					PutawayList putawayList2 = new PutawayList();
					putawayList2.setPutawayStatus(2);
					putawayList2.setPutawayId(putawayList.getPutawayId());
					putawayListMapper.updateByPrimaryKeySelective(putawayList2);
				}
			}

		}
        List<PutawayList> putawayLists2 = putawayListMapper.selectByPutawayObj(putawayListReqDto);
        PageInfo<List<PutawayList>> pageInfo = new PageInfo(putawayLists2);
        return new Page<>(pageInfo);
    }

    @Override
    public PutawayDetailsResDto selectPutawayDetailsByPutawayId(String putawayId) {
        PutawayDetailsResDto<QualityBoxPutaway> result = new PutawayDetailsResDto<>();
        List<QualityBoxPutaway> putawayDetailsList = qualityBoxPutawayMapper.selectByPutawayId(putawayId);
        Integer exceptionCount = putawayExceptionMapper.selectExcCountByPutawayId(putawayId);
        Integer planedSum = putawayDetailsList.stream().mapToInt(QualityBoxPutaway::getPlannedPutawayNumber).sum();
        Integer actualSum = putawayDetailsList.stream().mapToInt(QualityBoxPutaway::getActualPutawayNumber).sum();
        result.setPlanedPutawaySumNum(planedSum);
        result.setActualPutawaySumNum(actualSum + exceptionCount);
        result.setDiffNum(actualSum + exceptionCount - planedSum);
        result.setPutawayDetailsList(putawayDetailsList);
        return result;
    }

    @Override
    public List<PutawayException> getExceptionDetailsByPutawayId(String putawayId) {
        try {
            return putawayExceptionMapper.selectExceptionDetailsByPutawayId(putawayId);
        } catch (GlobalException e){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402,ResponseCodeEnum.RETURN_CODE_100402.getMsg());
        } catch (Exception e){
            LOGGER.error("业务异常:{}",e.getMessage());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,ResponseCodeEnum.RETURN_CODE_100500.getMsg());
        }
    }


	@Override
    public void addPutawayException(PutawayException putawayException) {
		int insertSelective = putawayExceptionMapper.insertSelective(putawayException);
	}
}
