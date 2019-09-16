package com.brandslink.cloud.inbound.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.RemoteUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.InboundStandingBookReqDto;
import com.brandslink.cloud.inbound.dto.InboundStandingBookResDto;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.QualityControlListMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.remote.RemoteUserService;
import com.brandslink.cloud.inbound.service.InboundStatisticsService;
import com.brandslink.cloud.inbound.utils.InventoryUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 入库统计实现层
 * @date 2019/7/8 15:19
 */
@Service
public class InboundStatisticsServiceImpl implements InboundStatisticsService {

    @Resource
    private QualityControlListMapper qualityControlListMapper;

    @Autowired
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    private static Logger logger = LoggerFactory.getLogger(InboundStatisticsServiceImpl.class);

    @Override
    public Page<InboundStandingBookResDto> getStandingBook(InboundStandingBookReqDto inboundRequest) {
        if (StringUtils.isNotEmpty(inboundRequest.getPageNum()) && StringUtils.isNotEmpty(inboundRequest.getPageSize())) {
            Page.builder(inboundRequest.getPageNum(), inboundRequest.getPageSize());
        }

        String[] strings = checkWarehouseCode(inboundRequest);
        inboundRequest.setWarehouseCodeArray(strings);
        //调用基础服务查询本地分类和仓库名称
       /* List<InboundStandingBookResDto> data = qualityControlListMapper.getStandingBookByObJ(inboundRequest).stream().filter(x -> x.getQcGoodsNum() != 0 || x.getPutawayNum() != 0).collect(Collectors.toList());*/
        List<InboundStandingBookResDto> data = qualityControlListMapper.getStandingBookByObJ(inboundRequest);
       /* data.forEach(x -> {
            String localType = "";
            if (StringUtils.isNotEmpty(x.getSku())) {
                localType = Utils.returnRemoteResultDataString(remoteCenterService.getCategoryBySku(x.getSku()), "基础数据接口异常");
            }
            JSONObject jsonObject1 = JSONObject.parseObject(Utils.returnRemoteResultDataString(remoteCenterService.getWarehouseByCode(x.getWarehouseId()), "基础数据接口异常"));
            String warehouseName = null;
            if (null != jsonObject1) {
                warehouseName = jsonObject1.getString("warehouseName");
            }
            String sourceType = null;
            JSONObject jsonObject = JSONObject.parseObject(Utils.returnRemoteResultDataString(remoteCenterService.getDictionaryDataDetail("put_type", x.getSourceType()), "基础数据接口异常"));
            if (null != jsonObject) {
                sourceType = jsonObject.getString("dataName");
            }
            x.setSourceType(sourceType);
            x.setLocalType(localType);
            x.setWarehouseId(warehouseName);
        });
        //调用用户服务查询客户和货主
        List<String> customerList = new ArrayList<>(4);
        for (InboundStandingBookResDto inboundStandingBookResDto : data) {
            customerList.add(inboundStandingBookResDto.getGoodsOwner());
        }
        JSONObject jsonObject = JSON.parseObject(userService.getShipperDetail(customerList));
        RemoteUtil.invoke(jsonObject);
        if (jsonObject != null && ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(RemoteUtil.getErrorCode())) {
            List<JSONObject> jsonObjectList = RemoteUtil.getList();
            data.stream().map(staticDto -> jsonObjectList.stream().filter(jsonObject1 ->
                    StringUtils.equals(staticDto.getGoodsOwner(), jsonObject1.getString("dataCode"))
            ).findFirst().map(jsonObject2 -> {
                staticDto.setGoodsOwner(jsonObject2.getString("dataName"));
                return staticDto;
            }).orElse(staticDto)).collect(Collectors.toList());

        }*/
        PageInfo<InboundStandingBookResDto> pageInfo = new PageInfo(data);
        return new Page<>(pageInfo);
    }


    @Override
    public InboundStandingBookResDto getStandingBookCount(InboundStandingBookReqDto inboundStandingBookReqDto) {

        String[] strings = checkWarehouseCode(inboundStandingBookReqDto);
        inboundStandingBookReqDto.setWarehouseCodeArray(strings);
        return qualityControlListMapper.getStandingBookCount(inboundStandingBookReqDto);
    }


    /**
     * 校验仓库权限
     * @param inboundRequest
     * @return
     */
    private String[] checkWarehouseCode(InboundStandingBookReqDto inboundRequest){
        //获取当前登录用户下的仓库
        String[] requestWarehouseCode = null;
        String[] warehouseCodeArray = getUserDetailInfoUtil.getUserDetailInfo().getWarehouseCode().split(",");
        logger.info("获取当前登录用户下的仓库编码======》开始: 仓库编码{}", JSON.toJSONString(warehouseCodeArray));
        if (null == warehouseCodeArray || warehouseCodeArray.length == 0) {
            logger.info("当前用户下没有获取到仓库信息，权限不足!");
            throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "当前用户下没有获取到仓库信息，权限不足!");
        }
        //判断请求参数  仓库编码
        if (StringUtils.isBlank(inboundRequest.getWarehouseCode())) {  //返回用户下的仓库数据
            requestWarehouseCode = warehouseCodeArray;
        } else {
            //校验请求仓库  是否是用户名下的
            boolean b = InventoryUtil.arrayContainsElement(warehouseCodeArray, inboundRequest.getWarehouseCode());
            if (b) {
                requestWarehouseCode = new String[]{inboundRequest.getWarehouseCode()};
            } else {
                logger.info("用户获取其他仓库的权限不足，请联系管理员!");
                throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "当前用户下没有获取到仓库信息，权限不足!");
            }
        }
        return requestWarehouseCode;
    }
}
