package com.brandslink.cloud.inbound.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.entity.InboundWorkloadInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.DateUtils;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.*;
import com.brandslink.cloud.inbound.entity.*;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.*;
import com.brandslink.cloud.inbound.rabbitmq.InboundStatisticsSender;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.QualityControlService;
import com.brandslink.cloud.inbound.utils.MapSortUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 质检持久层
 * @date 2019/6/3 9:29
 */
@Service
public class QualityControlServiceImpl implements QualityControlService {

    private final Logger LOGGER = LoggerFactory.getLogger(QualityControlServiceImpl.class);

    @Resource
    private QualityControlDetailsMapper qualityControlDetailsMapper;

    @Resource
    private QualityExceptionControlMapper qualityExceptionControlMapper;

    @Resource
    private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;

    @Resource
    private QualityControlListMapper qualityControlListMapper;

    @Resource
    private PutawayStrategyMapper putawayStrategyMapper;

    @Resource
    private RemoteCenterService remoteCenterService;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Resource
    private SellingBackMapper sellingBackMapper;

    @Resource
    private SellingBackDetailMapper sellingBackDetailMapper;

    @Resource
    private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;

    @Resource
    private ReceiveCardBoardCreateMapper receiveCardBoardCreateMapper;

    @Resource
    private QualityControlAssistantsMapper qualityControlAssistantsMapper;

    @Resource
    private PutawayShelfMapper putawayShelfMapper;

    @Resource
    private PutawayListMapper putawayListMapper;

    @Resource
    private QualityBoxPutawayMapper qualityBoxPutawayMapper;

    @Resource
    private QualityBoxAssistantsMapper qualityBoxAssistantsMapper;

    @Override
    public QcListResponseDto<SellingBackDetailDto> getSellingBackByWaybillNum(String warehouseCode, String waybillNum, AssistantDto assistants) {
        String waybillNumUpperCase = waybillNum.toUpperCase();
        QcListResponseDto<SellingBackDetailDto> result = new QcListResponseDto<>();
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        if (null == userInfo){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(),ResponseCodeEnum.RETURN_CODE_100406.getMsg());
        }

        List<SellingBackDetailDto> sellingBackDetails = sellingBackDetailMapper.selectByWaybillId(waybillNumUpperCase);
        if (CollectionUtils.isEmpty(sellingBackDetails)){
            LOGGER.error("销退质检单不存在运单号：【{}】", waybillNumUpperCase);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "运单号：【"+waybillNum+"】未销退接收，不可进行销退质检，请检查！！");
        } else if (!warehouseCode.equals(sellingBackDetails.get(0).getWarehouse())){
            JSONObject jsonObject = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(warehouseCode), "基础数据","基础数据接口异常！！");
            JSONObject resultParseObj = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(sellingBackDetails.get(0).getWarehouse()), "基础数据","基础数据接口异常！！");
            LOGGER.error("质检选择仓库与到货通知单仓库【{}】不匹配！！", jsonObject.getString("warehouseName"));
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "质检选择仓库【"+jsonObject.getString("warehouseName")+"】与到货通知单仓库【"+resultParseObj.getString("warehouseName")+"】不匹配！！");
        }

        //查询基础数据，sku信息
        List<String> skuParamList = new ArrayList<>();
        for (SellingBackDetailDto sellingBackDetail : sellingBackDetails){
            skuParamList.add(sellingBackDetail.getSku());
        }
        JSONArray skuInfoArray = Utils.resultParseArray(remoteCenterService.getSkuPictureBySku(skuParamList),"基础数据","根据sku获取详情接口异常");
        if (CollectionUtils.isEmpty(skuInfoArray)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "未维护sku:"+skuParamList.toString()+"的本地分类，请维护本地分类！！");
        }
        List<JSONObject> jsonObjectList = skuInfoArray.toJavaList(JSONObject.class);
        Map<String, String> skuPictureMap = new HashMap<>();
        Map<String, String> skuNameMap = new HashMap<>();
        for (JSONObject jsonObj : jsonObjectList){
            skuPictureMap.put(jsonObj.getString("productSku"), jsonObj.getString("productPictures"));
            skuNameMap.put(jsonObj.getString("productSku"), jsonObj.getString("productName"));
        }
        SellingBackDetailDto sellingBack = sellingBackDetails.get(0);

        //生成质检单号及销退质检单详情
        createSellQcOrderNum(sellingBack.getSourceId(), waybillNumUpperCase, assistants, sellingBackDetails, skuPictureMap);

        //根据运单查询销退详情
        List<SellingBackDetailDto> waybillList = sellingBackDetailMapper.getSellingBackDetailsByWaybillNum(waybillNumUpperCase);
        List<SellingBackDetailDto> qcWaybillInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(waybillList)){
            LOGGER.info("存在运单：【{}】,销退运单信息",waybillNum);
            if(StringUtils.isNotBlank(sellingBack.getSourceId())){
                result.setSourceOrderNumber(sellingBack.getSourceId());
            }
            result.setSellingBackType(sellingBack.getSellingBackType());
            result.setQcType("1");

            //拿到相同运单的分组集合
            Map<String, List<SellingBackDetailDto>> listMap = waybillList.stream().collect(Collectors.groupingBy(SellingBackDetailDto::getWaybillId));

            listMap.keySet().forEach(k->{
                SellingBackDetailDto<SellingBackDetailDto> sellingBackDetailDto = new SellingBackDetailDto<>();
                List<SellingBackDetailDto> skuDiff = new ArrayList<>();
                List<SellingBackDetailDto> backDetailDtos = listMap.get(k);
                backDetailDtos.forEach(i->{
                    i.setGoodsName(skuNameMap.get(i.getSku()));
                    skuDiff.add(i);
                });
                sellingBackDetailDto.setWaybillId(k);
                sellingBackDetailDto.setQualityControlOrderNumber(backDetailDtos.get(0).getQualityControlOrderNumber());
                sellingBackDetailDto.setWaybillList(skuDiff);
                qcWaybillInfoList.add(sellingBackDetailDto);
            });
            result.setWaybillInfoDtoList(qcWaybillInfoList);
        } else {
            LOGGER.error("不存在销退运单信息【{}】",waybillNum);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "运单号：【"+waybillNum+"】未销退接收，不可进行销退质检，请检查！！");
        }
        return result;
    }

    @Override
    public QcListResponseDto<SellingBackDetailDto> getSellingBackBySourceNumber(String warehouseCode, String sourceNumber, AssistantDto assistants) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        if (null == userInfo){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(),ResponseCodeEnum.RETURN_CODE_100406.getMsg());
        }

        List<SellingBackDetailDto> sellingBackDetailList = sellingBackDetailMapper.selectBySourceId(sourceNumber);
        List<SellingBack> sellingBackList = sellingBackMapper.selectSellListBySourceId(sourceNumber);
        if (CollectionUtils.isEmpty(sellingBackDetailList)){
            LOGGER.info("销退收货单，不存在来源单号：【{}】，",sourceNumber);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "采购单号："+ sourceNumber +"下的运单未销退接收，不可进行销退质检，请检查！");
        }  else if (!warehouseCode.equals(sellingBackDetailList.get(0).getWarehouse())){
            JSONObject jsonObject = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(warehouseCode), "基础数据","基础数据接口异常！！");
            JSONObject resultParseObj = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(sellingBackDetailList.get(0).getWarehouse()), "基础数据","基础数据接口异常！！");
            LOGGER.error("质检选择仓库与到货通知单仓库【{}】不匹配！！", jsonObject.getString("warehouseName"));
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "质检选择仓库【"+jsonObject.getString("warehouseName")+"】与到货通知单仓库【"+resultParseObj.getString("warehouseName")+"】不匹配！！");
        }

        //查询基础数据，sku信息
        List<String> skuParamList = new ArrayList<>();
        for (SellingBackDetailDto sellingBackDetail : sellingBackDetailList){
            skuParamList.add(sellingBackDetail.getSku());
        }
        JSONArray skuInfoArray = Utils.resultParseArray(remoteCenterService.getSkuPictureBySku(skuParamList),"基础数据","根据sku获取详情接口异常");
        if (CollectionUtils.isEmpty(skuInfoArray)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "未维护sku:"+skuParamList.toString()+"的本地分类，请维护本地分类！！");
        }
        List<JSONObject> jsonObjectList = skuInfoArray.toJavaList(JSONObject.class);
        Map<String, String> skuPictureMap = new HashMap<>();
        Map<String, String> skuNameMap = new HashMap<>();
        for (JSONObject jsonObj : jsonObjectList){
            skuPictureMap.put(jsonObj.getString("productSku"), jsonObj.getString("productPictures"));
            skuNameMap.put(jsonObj.getString("productSku"), jsonObj.getString("productName"));
        }

        for (SellingBack sellingBack: sellingBackList){
            String waybillId = sellingBack.getWaybillId();
            List<SellingBackDetailDto> sellingBackDetails = sellingBackDetailMapper.selectByWaybillId(waybillId);
            if (CollectionUtils.isEmpty(sellingBackDetails)){
                LOGGER.error("销退质检单不存在运单号：【{}】", waybillId);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "运单号：【"+waybillId+"】未销退接收，不可进行销退质检，请检查！！");
            }
            //生成质检单号
            createSellQcOrderNum(sourceNumber, sellingBack.getWaybillId(), assistants, sellingBackDetails, skuPictureMap);
        }

        QcListResponseDto<SellingBackDetailDto> result = new QcListResponseDto<>();
        List<SellingBackDetailDto> qcWaybillInfoList = new ArrayList<>();
        List<SellingBackDetailDto> waybillList = sellingBackDetailMapper.getSellingBackBySourceNumber(sourceNumber);
        if (CollectionUtils.isNotEmpty(waybillList)){
            LOGGER.info("存在采购单：【{}】，销退运单信息",sourceNumber);
            SellingBackDetailDto sellingBack = waybillList.get(0);
            result.setSourceOrderNumber(sellingBack.getSourceId());
            result.setSellingBackType(sellingBack.getSellingBackType());
            result.setQcType("0");

            Map<String, List<SellingBackDetailDto>> listMap = waybillList.stream().collect(Collectors.groupingBy(SellingBackDetailDto::getWaybillId));
            LOGGER.info("存在【{}】组运单信息",listMap.size());

            listMap.keySet().forEach(k->{
                List<SellingBackDetailDto> skuDiff = new ArrayList<>();
                List<SellingBackDetailDto> backDetailDto = listMap.get(k);
                SellingBackDetailDto<SellingBackDetailDto> sellingBackDetailDto = new SellingBackDetailDto<>();
                backDetailDto.forEach(i->{
                    i.setGoodsName(skuNameMap.get(i.getSku()));
                    skuDiff.add(i);
                });
                sellingBackDetailDto.setWaybillId(k);
                sellingBackDetailDto.setWaybillList(skuDiff);
                sellingBackDetailDto.setQualityControlOrderNumber(skuDiff.get(0).getQualityControlOrderNumber());
                qcWaybillInfoList.add(sellingBackDetailDto);
            });
            result.setWaybillInfoDtoList(qcWaybillInfoList);
        } else {
            LOGGER.error("采购单号：【{}】下的运单未销退接收，不可进行销退质检，请检查！!", sourceNumber);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "采购单号："+sourceNumber+"下的运单未销退接收，不可进行销退质检，请检查！！");
        }
        return result;
    }

    @Override
    public QcListResponseDto<QcWaybillInfoDto> getWaybillInfoByCardBoard(String warehouseCode, String cardBoardNum, String waybillNum, AssistantDto assistants, String flag) {
        String cardBoardUpperCase = cardBoardNum.toUpperCase();
        String waybillNumUpperCase = waybillNum.toUpperCase();
        List<ReceiveCardBoardCreate> receiveCardBoardList = receiveCardBoardCreateMapper.selectByCardBoardId(cardBoardUpperCase, null, null);
        if (CollectionUtils.isEmpty(receiveCardBoardList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "卡板号：【" + cardBoardUpperCase + "】不存在,请检查！！");
        } else if (StringUtils.isBlank(receiveCardBoardList.get(0).getWarehouseCode())){
            if ("1".equals(flag)){
                LOGGER.info("卡板号:【{}】或者运单号:【{}】不存在",cardBoardUpperCase,waybillNumUpperCase);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "此包裹运单号：【"+waybillNum+"】,未与卡板号：【"+cardBoardUpperCase+"】绑定，请检查!!");
            } else if ("2".equals(flag)){
                LOGGER.info("卡板号:【{}】不存在",cardBoardUpperCase);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "此采购单号：【"+waybillNumUpperCase+"】下的运单号未与卡板号："+cardBoardUpperCase+"绑定，请检查!!");
            }
        } else if (!warehouseCode.equals(receiveCardBoardList.get(0).getWarehouseCode())){
            JSONObject jsonObject = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(warehouseCode), "基础数据","基础数据接口异常！！");
            JSONObject resultParseObj = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(receiveCardBoardList.get(0).getWarehouseCode()), "基础数据","基础数据接口异常！！");
            LOGGER.error("质检选择仓库与到货通知单仓库【{}】不匹配！！", jsonObject.getString("warehouseName"));
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "质检选择仓库【"+jsonObject.getString("warehouseName")+"】与卡板号："+cardBoardNum+"对应仓库【"+resultParseObj.getString("warehouseName")+"】不匹配！！");
        }
        QcListResponseDto<QcWaybillInfoDto> result = new QcListResponseDto<>();
        List<QcWaybillInfoDto> qcWaybillList;
        switch (flag){
            //flag = 1 waybillNumUpperCase为运单号
            case "1":
                //生成质检单号及质检单详情
                String sourceId = null;
                for (ReceiveCardBoardCreate receiveCardBoardCreate : receiveCardBoardList){
                    if (receiveCardBoardCreate.getWaybillId().equals(waybillNumUpperCase)){
                        sourceId = receiveCardBoardCreate.getSourceId();
                    }
                }
                createQcOrderNum(sourceId, cardBoardUpperCase, waybillNumUpperCase, assistants);
                qcWaybillList = qualityControlDetailsMapper.getWaybillInfoByCardBoardAndWaybillNum(cardBoardUpperCase, waybillNumUpperCase);
                if (CollectionUtils.isNotEmpty(qcWaybillList)){
                    QcWaybillInfoDto infoDto = qcWaybillList.get(0);
                    result.setSourceOrderNumber(infoDto.getSourceId());
                    result.setSourceType(infoDto.getSourceType());
                    result.setBuyer(infoDto.getCreater());
                    result.setQcType(infoDto.getQcType());

                    List<QcWaybillInfoDto> qcWaybillInfoList = new ArrayList<>();
                    QcWaybillInfoDto<QcWaybillInfoDto> qcWaybillInfoDto = new QcWaybillInfoDto<>();
                    QcWaybillInfoDto waybillInfoDto = qcWaybillList.get(0);
                    qcWaybillInfoDto.setCustomerName(waybillInfoDto.getCustomerName());
                    qcWaybillInfoDto.setWaybillId(waybillInfoDto.getWaybillId());
                    qcWaybillInfoDto.setQualityControlOrderNumber(infoDto.getQualityControlOrderNumber());
                    qcWaybillInfoDto.setQcWaybillInfoDtoList(qcWaybillList);
                    qcWaybillInfoList.add(qcWaybillInfoDto);
                    result.setWaybillInfoDtoList(qcWaybillInfoList);
                } else {
                    LOGGER.info("卡板号:【{}】或者运单号:【{}】不存在",cardBoardUpperCase,waybillNumUpperCase);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "此包裹运单号：【"+waybillNum+"】,未与卡板号：【"+cardBoardUpperCase+"】绑定，请检查!!");
                }
                break;
            //flag = 2 waybillNumUpperCase为来源单号
            case "2":
                // 这里的waybillNumUpperCase为来源单号
                List<ReceiveGoodDetails> receiveGoodDetailsList = receiveGoodDetailsMapper.selectByCardBoardIdAndSourceId(cardBoardUpperCase, waybillNumUpperCase);
                if (CollectionUtils.isEmpty(receiveGoodDetailsList)){
                    LOGGER.info("卡板号:【{}】不存在",cardBoardUpperCase);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "此采购单号：【"+waybillNumUpperCase+"】下的运单号未与卡板号："+cardBoardUpperCase+"绑定，请检查!!");
                }
                for (ReceiveGoodDetails receiveGoodDetail: receiveGoodDetailsList){
                    //生成质检单号
                    createQcOrderNum(receiveGoodDetail.getSourceId(), cardBoardUpperCase, receiveGoodDetail.getWaybillId(), assistants);
                }

                qcWaybillList = qualityControlDetailsMapper.getWaybillInfoByCardBoardAndSourceNum(cardBoardUpperCase,waybillNumUpperCase);
                QcWaybillInfoDto qcWaybillInfo;
                if (CollectionUtils.isNotEmpty(qcWaybillList)){
                    qcWaybillInfo = qcWaybillList.get(0);
                    result.setSourceOrderNumber(qcWaybillInfo.getSourceId());
                    result.setSourceType(qcWaybillInfo.getSourceType());
                    result.setBuyer(qcWaybillInfo.getCreater());
                    result.setQcType(qcWaybillInfo.getQcType());

                    //拿到相同运单的分组集合
                    Map<String, List<QcWaybillInfoDto>> listMap = qcWaybillList.stream().collect(Collectors.groupingBy(QcWaybillInfoDto::getWaybillId));

                    //最外层属性集合
                    List<QcWaybillInfoDto> waybillList = new ArrayList<>();

                    //遍历分组
                    listMap.keySet().forEach(k->{
                        //内层集合对象
                        QcWaybillInfoDto<QcWaybillInfoDto> qcWaybillInfoDto = new QcWaybillInfoDto<>();
                        //拿到单次分组,添加到集合
                        List<QcWaybillInfoDto> resultList = listMap.get(k);

                        //遍历单次分组的运单信息，并将它添加到不同sku集合
                        List<QcWaybillInfoDto> skuDiff = new ArrayList<>(resultList);
                        //将不同的sku集合放入运单集合
                        qcWaybillInfoDto.setQcWaybillInfoDtoList(skuDiff);

                        QcWaybillInfoDto info = resultList.get(0);
                        qcWaybillInfoDto.setCustomerName(info.getCustomerName());
                        qcWaybillInfoDto.setWaybillId(info.getWaybillId());
                        qcWaybillInfoDto.setFoodsFloor(info.getFoodsFloor());
                        qcWaybillInfoDto.setQualityControlOrderNumber(info.getQualityControlOrderNumber());
                        qcWaybillInfoDto.setSealBoxUnfinishNumber(info.getSealBoxUnfinishNumber());
                        //添加至最外层集合
                        waybillList.add(qcWaybillInfoDto);
                    });
                    result.setWaybillInfoDtoList(waybillList);

                } else {
                    LOGGER.info("卡板号:【{}】不存在",cardBoardUpperCase);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "此采购单号：【"+waybillNumUpperCase+"】下的运单号未与卡板号："+cardBoardUpperCase+"绑定，请检查!!");
                }
                break;
        }
        return result;
    }

    /**
     * @description: 生成销退质检单号
     * @param sourceOrderNumber: 来源单号
     * @param waybillNumUpperCase: 运单号（大写）
     * @param assistants: 协同人对象
     * @param sellingBackDetails: 销退详情对象
     * @param skuPictureMap: sku图片集合
     */
    private void createSellQcOrderNum(String sourceOrderNumber, String waybillNumUpperCase, AssistantDto assistants, List<SellingBackDetailDto> sellingBackDetails, Map<String, String> skuPictureMap) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        Map<String,String> qcOrderMap = new HashMap<>();
        Integer waybillPlanCount = sellingBackDetailMapper.selectCountByWaybillId(waybillNumUpperCase);
        if (null == waybillPlanCount){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "消退质检通知单详情，不存在该运单："+ waybillNumUpperCase);
        }


        QualityControlList qc = qualityControlListMapper.selectQcListByWaybillId(waybillNumUpperCase);
        if (null == qc){
            QualityControlList qcInsert = new QualityControlList();
            synchronized (this){
                String qcOrder = qualityControlListMapper.selectLastData();
                String order = genOrder("QC", qcOrder);
                qcOrderMap.put(waybillNumUpperCase,order);
                qcInsert.setQualityControlOrderNumber(order);
                if(StringUtils.isNotBlank(sourceOrderNumber)){
                    qcInsert.setSourceOrderNumber(sourceOrderNumber);
                }
                qcInsert.setOrderStatus(Byte.valueOf("1"));
                qcInsert.setWaybillNumber(waybillNumUpperCase);
                qcInsert.setQcPlanTotalNumber(waybillPlanCount);
                qcInsert.setCreateId(Long.valueOf(userInfo.getId()));
                qcInsert.setCreateBy(userInfo.getName());
                qcInsert.setUpdateId(Long.valueOf(userInfo.getId()));
                qcInsert.setUpdateBy(userInfo.getName());
                qcInsert.setAssistants(assistants.getAssistants());
                qcInsert.setVersion(0);
                qualityControlListMapper.insert(qcInsert);
            }

            for (SellingBackDetailDto sellingBackDetail : sellingBackDetails){
                QualityControlDetails param = new QualityControlDetails();
                param.setQualityControlOrderNumber(qcOrderMap.get(sellingBackDetail.getWaybillId()));
                if (skuPictureMap.containsKey(sellingBackDetail.getSku())){
                    param.setPictureUrl(skuPictureMap.get(sellingBackDetail.getSku()));
                }
                param.setSku(sellingBackDetail.getSku());
                param.setWaybillNumber(sellingBackDetail.getWaybillId());
                param.setSourceOrderNumber(sourceOrderNumber);
                param.setSkuStatus(Byte.valueOf("1"));
                qualityControlDetailsMapper.insertSelective(param);
            }
        }

    }

    /**
     * @description: 生成质检单号
     * @param sourceOrderNumber： 来源单号
     * @param cardBoardUpperCase： 卡板号（大写）
     * @param waybillNumUpperCase： 运单号(大写)
     * @param assistants：协助人对象
     */
    private void createQcOrderNum(String sourceOrderNumber, String cardBoardUpperCase, String waybillNumUpperCase, AssistantDto assistants) {
            UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
            List<ReceiveGoodDetails> receiveGoodDetails = receiveGoodDetailsMapper.selectByCardBoardIdAndWaybillNum(cardBoardUpperCase, waybillNumUpperCase);
            Map<String,String> qcOrderMap = new HashMap<>();
            Integer waybillPlanCount = receiveGoodDetailsMapper.selectCountByWaybillId(waybillNumUpperCase);
            if (null == waybillPlanCount || CollectionUtils.isEmpty(receiveGoodDetails)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "运单号："+waybillNumUpperCase+",未绑定卡板号:"+cardBoardUpperCase+"，请检查");
            }
            List<String> skuParamList = new ArrayList<>();
            for (ReceiveGoodDetails receiveGoodDetail : receiveGoodDetails){
                skuParamList.add(receiveGoodDetail.getSku());
            }

            JSONArray skuInfoArray = Utils.resultParseArray(remoteCenterService.getSkuPictureBySku(skuParamList),"基础数据","根据sku获取详情接口异常");
            if (CollectionUtils.isEmpty(skuInfoArray)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "未维护sku:"+skuParamList.toString()+"的本地分类，请维护本地分类！！");
            }

            List<JSONObject> jsonObjectList = skuInfoArray.toJavaList(JSONObject.class);
            Map<String, String> skuPictureMap = new HashMap<>();
            for (JSONObject jsonObj : jsonObjectList){
                skuPictureMap.put(jsonObj.getString("productSku"), jsonObj.getString("productPictures"));
            }

            Set<String> skuKeySet = skuPictureMap.keySet();
            for (String sku : skuParamList){
                if (!skuKeySet.contains(sku)){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "未维护sku:"+sku+"的本地分类，请维护本地分类！！");
                }
            }

            QualityControlList qc = qualityControlListMapper.selectQcListByWaybillId(waybillNumUpperCase);
            if (null == qc){
                synchronized (this){
                    QualityControlList qcInsert = new QualityControlList();
                    String qcOrder = qualityControlListMapper.selectLastData();
                    String order = genOrder("QC", qcOrder);
                    qcOrderMap.put(waybillNumUpperCase,order);
                    qcInsert.setQualityControlOrderNumber(order);
                    if(StringUtils.isNotBlank(sourceOrderNumber)){
                        qcInsert.setSourceOrderNumber(sourceOrderNumber);
                    }
                    qcInsert.setOrderStatus(Byte.valueOf("1"));
                    qcInsert.setWaybillNumber(waybillNumUpperCase);
                    qcInsert.setQcPlanTotalNumber(waybillPlanCount);
                    qcInsert.setCreateId(Long.valueOf(userInfo.getId()));
                    qcInsert.setCreateBy(userInfo.getName());
                    qcInsert.setUpdateId(Long.valueOf(userInfo.getId()));
                    qcInsert.setUpdateBy(userInfo.getName());
                    qcInsert.setAssistants(assistants.getAssistants());
                    qcInsert.setVersion(0);
                    qualityControlListMapper.insert(qcInsert);
                }

                for (ReceiveGoodDetails receiveGoodDetail : receiveGoodDetails){
                    QualityControlDetails param = new QualityControlDetails();
                    param.setQualityControlOrderNumber(qcOrderMap.get(receiveGoodDetail.getWaybillId()));
                    if (skuPictureMap.containsKey(receiveGoodDetail.getSku())){
                        param.setPictureUrl(skuPictureMap.get(receiveGoodDetail.getSku()));
                    }
                    param.setSku(receiveGoodDetail.getSku());
                    param.setWaybillNumber(receiveGoodDetail.getWaybillId());
                    param.setSourceOrderNumber(receiveGoodDetail.getSourceId());
                    param.setSkuStatus(Byte.valueOf("1"));
                    qualityControlDetailsMapper.insertSelective(param);
                }
            }

    }

    @Override
    public InboundWorkloadInfo addQCWaybillFinish(QcWaybillRequestDto qcWaybillRequestDto) {
        String flag = qcWaybillRequestDto.getFlag();
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        Integer upPackCount = qualityControlDetailsMapper.selectUnpackByWaybill(qcWaybillRequestDto.getWaybillNumber());
        //查询运单对应的sku详情
        QualityControlDetails qcDetails = qualityControlDetailsMapper.selectQCWaybillDetails(qcWaybillRequestDto.getQualityControlOrderNumber(),qcWaybillRequestDto.getWaybillNumber(), qcWaybillRequestDto.getSku());
        if (null != qcDetails){
            if (qcWaybillRequestDto.getGoodProductNumber() == 0){
                LOGGER.error("QC完成数量不能为0！！");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "QC完成数量不能为0！！");
            } else if (!qcWaybillRequestDto.getQualityControlFinishNumber().equals(qcDetails.getQualityControlFinishNumber())){
                LOGGER.error("页面数据与数据库数据不统一！！");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "页面存在缓存，请刷新质检页面！！");
            }
        } else {
            LOGGER.error("质检详情不存在，请检查质检单是否生成对应的sku：【{}】信息", qcWaybillRequestDto.getSku());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "质检详情不存在该SKU:"+qcWaybillRequestDto.getSku()+"，请检查！！");
        }

        JSONArray skuInfoArray = Utils.resultParseArray(remoteCenterService.getSkuInfoBySku(new String[]{qcWaybillRequestDto.getSku()}),"基础数据","根据sku获取详情接口异常");
        if (CollectionUtils.isEmpty(skuInfoArray)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "未维护sku:"+qcWaybillRequestDto.getSku()+"的本地分类，请维护本地分类！！");
        }
        JSONObject skuObject = skuInfoArray.getJSONObject(0);

        Map<String, String> positionAndFloorMap;
        try {
            //上架策略分配
            positionAndFloorMap = strategyDistribution(qcWaybillRequestDto, skuObject);
        } catch (Exception e){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), e.getMessage());
        }

        //查询未封箱并更新质检详情及协同人关系表
        String skuFloor = positionAndFloorMap.get("areaFloor");
        List<String> skuFloors = putawayShelfMapper.selectUnSealFloor(qcWaybillRequestDto.getWaybillNumber(), qcWaybillRequestDto.getSku());
        if (CollectionUtils.isNotEmpty(skuFloors)){
            if (skuFloors.contains(skuFloor)){
                QualityControlAssistants qcAssistant = new QualityControlAssistants();
                qcAssistant.setWaybillNumber(qcWaybillRequestDto.getWaybillNumber());
                qcAssistant.setSku(qcWaybillRequestDto.getSku());
                qcAssistant.setSkuRemainNum(qcWaybillRequestDto.getGoodProductNumber());
                qcAssistant.setQcNumber(qcWaybillRequestDto.getGoodProductNumber());
                qcAssistant.setRecommendedLocationCode(positionAndFloorMap.get("positionCode"));
                qcAssistant.setQcStationId(qcWaybillRequestDto.getQcStationId());
                qcAssistant.setAssistants(qcWaybillRequestDto.getAssistants());
                qcAssistant.setCreateId(Long.valueOf(userInfo.getId()));
                qcAssistant.setCreateName(userInfo.getName());
                qcAssistant.setVersion(0);
                qualityControlAssistantsMapper.insert(qcAssistant);
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "质检失败，因SKU："+qcWaybillRequestDto.getSku()+",预分配至“"+skuFloors.get(0)+"”与当前未封箱的货物楼层“"+skuFloor+"”不一致，请将未封箱货物先封箱；");
            }
        } else {
                QualityControlAssistants qcAssistant = new QualityControlAssistants();
                qcAssistant.setWaybillNumber(qcWaybillRequestDto.getWaybillNumber());
                qcAssistant.setSku(qcWaybillRequestDto.getSku());
                qcAssistant.setSkuRemainNum(qcWaybillRequestDto.getGoodProductNumber());
                qcAssistant.setQcNumber(qcWaybillRequestDto.getGoodProductNumber());
                qcAssistant.setRecommendedLocationCode(positionAndFloorMap.get("positionCode"));
                qcAssistant.setQcStationId(qcWaybillRequestDto.getQcStationId());
                qcAssistant.setAssistants(qcWaybillRequestDto.getAssistants());
                qcAssistant.setCreateId(Long.valueOf(userInfo.getId()));
                qcAssistant.setCreateName(userInfo.getName());
                qcAssistant.setVersion(0);
                qualityControlAssistantsMapper.insert(qcAssistant);
        }
        Integer skuNum = qcDetails.getQualityControlFinishNumber();
        Integer goodProductNumber = qcWaybillRequestDto.getGoodProductNumber();
        int qcNum = goodProductNumber+qcWaybillRequestDto.getQualityControlFinishNumber();
        qcDetails.setQualityControlFinishNumber(qcNum);
        qcDetails.setGoodProductNumber(qcDetails.getGoodProductNumber()+goodProductNumber);
        qcDetails.setSkuFloor(skuFloor);
        qcDetails.setSealBoxUnfinishNumber(qcDetails.getSealBoxUnfinishNumber() + goodProductNumber);
        if (qcNum>=qcWaybillRequestDto.getSkuPlanNumber()){
            qcDetails.setSkuStatus(Byte.valueOf("3"));
        } else {
            qcDetails.setSkuStatus(Byte.valueOf("2"));
        }
        qcDetails.setSkuUpdateTime(new Date());
        int affectCount = qualityControlDetailsMapper.updateByPrimaryKeySelective(qcDetails);
        if (affectCount < 1){
            LOGGER.error("版本号:{},冲突，需要重试！！", qcDetails.getVersion());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
        }

        //更新质检详情和到货通知单状态
        updateQcAndArrivalStatus(qcWaybillRequestDto.getWaybillNumber(), qcWaybillRequestDto.getFlag());


        LOGGER.info("更新上架分配表");
        PutawayShelf putawayShelf = new PutawayShelf();
        putawayShelf.setWaybillNumber(qcWaybillRequestDto.getWaybillNumber());
        putawayShelf.setSku(qcWaybillRequestDto.getSku());
        putawayShelf.setPlannedPutawayNumber(qcWaybillRequestDto.getGoodProductNumber());
        putawayShelf.setWarehouseCode(qcWaybillRequestDto.getWarehouseCode());
        putawayShelf.setSkuFloor(skuFloor);
        putawayShelf.setRecommendedLocationCode(positionAndFloorMap.get("positionCode"));
        PutawayShelf shelfSelect = putawayShelfMapper.selectByObj(putawayShelf);
        if (null == shelfSelect){
            putawayShelf.setSkuRemainNum(qcWaybillRequestDto.getGoodProductNumber());
            putawayShelfMapper.insertSelective(putawayShelf);
        } else {
            shelfSelect.setPlannedPutawayNumber(shelfSelect.getPlannedPutawayNumber() + qcWaybillRequestDto.getGoodProductNumber());
            shelfSelect.setSkuRemainNum(shelfSelect.getSkuRemainNum() + qcWaybillRequestDto.getGoodProductNumber());
            int count = putawayShelfMapper.updateRemainNumByPrimaryKey(shelfSelect);
            if (count < 1){
                LOGGER.error("版本号:{},冲突，需要重试！！", qcDetails.getVersion());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
            }
        }

        //库存更新
        JSONObject dictionaryData;
        if ("1".equals(flag)){
            dictionaryData = Utils.resultParseObj(remoteCenterService.getDictionaryDataDetail("put_type", "2"), "基础数据", "码表接口异常！！");
        } else {
            ReceiveArrivalNotice arrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(qcWaybillRequestDto.getSourceOrderNumber());
            dictionaryData = Utils.resultParseObj(remoteCenterService.getDictionaryDataDetail("put_type", arrivalNotice.getSourceType()), "基础数据", "码表接口异常！！");
        }
        String businessType = null;
        if (null != dictionaryData){
            businessType = dictionaryData.getString("dictCode")+"/"+dictionaryData.getString("dataCode");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessType", businessType);
        param.put("goodsSku", qcWaybillRequestDto.getSku());
        param.put("shelfLocationId", positionAndFloorMap.get("positionId"));
        param.put("skuNumbers", qcWaybillRequestDto.getGoodProductNumber());
        param.put("sourceOrderNumber", qcWaybillRequestDto.getSourceOrderNumber());
        param.put("updateBy", userInfo.getName());
        param.put("warehouseCode", qcWaybillRequestDto.getWarehouseCode());


        //更新拆包
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> paramUnpack = new HashMap<>();
        paramUnpack.put("warehouseCode", qcWaybillRequestDto.getWarehouseCode());
        paramUnpack.put("operationType", 1);
        if (0 == upPackCount){
            paramUnpack.put("number", 1);
        } else {
            paramUnpack.put("number", 0);
        }
        paramList.add(paramUnpack);

        //先更新拆包
        LOGGER.info("新增拆包数：{}",paramUnpack.get("number"));
        try {
            Utils.getResultData(remoteCenterService.insertInventoryData(paramList), SystemConstants.nameType.SYS_CENTER, "更新拆包数接口异常！！");
        } catch (Exception e){
            LOGGER.error("基础数据，拆包数更新失败！！");
            e.printStackTrace();
        }
        //后更新库存
        LOGGER.info("推送货位预上库存,sku:{},货位：{},新增数量:{}", qcWaybillRequestDto.getSku(), positionAndFloorMap.get("positionCode"), qcWaybillRequestDto.getGoodProductNumber());
        String str = Utils.getResultData(remoteCenterService.qcFinishUpdateInventory(param), SystemConstants.nameType.SYS_CENTER,"库存更新接口异常！！");
      if (null == str){
            LOGGER.error("基础数据，预上库存更新失败！！");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"基础数据，预上库存更新失败！！");
        }

        //再入库统计消息发送
        InboundWorkloadInfo info = new InboundWorkloadInfo();
        info.setWarehouseCode(qcWaybillRequestDto.getWarehouseCode());
        ReceiveArrivalNotice receiveArrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(qcWaybillRequestDto.getWaybillNumber());
        if ("0".equals(flag)){
            info.setCustomer(String.valueOf(receiveArrivalNotice.getCustomerName()));
            info.setPutType(Integer.valueOf(receiveArrivalNotice.getSourceType()));//入库类型
        } else {
            info.setCustomer(skuObject.getString("customerName"));
            info.setPutType(2);//入库类型
        }
        info.setWaybillId(qcWaybillRequestDto.getWaybillNumber());
        info.setOperationType(2);
        info.setWorkingPeople(getUserDetailInfoUtil.getUserDetailInfo().getName());
        info.setSynergyPeople(qcWaybillRequestDto.getAssistants());
        if (0 == skuNum){
            info.setQcTimeNum(1);
        } else {
            info.setQcTimeNum(0);
        }
        info.setQcNum(qcWaybillRequestDto.getGoodProductNumber());
        info.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return info;
    }

    /**
     * @description: 更新质检单状态
     * @param waybillNumber： 运单号
     * @param flag:
     */
    private void updateQcAndArrivalStatus(String waybillNumber, String flag) {
        QualityControlList qcSelect = new QualityControlList();
        qcSelect.setWaybillNumber(waybillNumber);
        if("0".equals(flag)){
            Integer qcFinishCount = qualityControlDetailsMapper.selectCountByWaybillId(waybillNumber);
            Integer waybillPlanSumCount = receiveGoodDetailsMapper.selectCountByWaybillId(waybillNumber);
            List<String> statusList = qualityControlDetailsMapper.selectStatusByWaybillId(waybillNumber);
            Integer version = qualityControlListMapper.selectVersionByWaybillId(waybillNumber);
            if (qcFinishCount >= waybillPlanSumCount && !statusList.contains("1") && !statusList.contains("2")){
                qcSelect.setOrderStatus(Byte.valueOf("3"));
                qcSelect.setFinishTime(new Date());
                qcSelect.setQcFinishTotalNumber(qcFinishCount);
                qcSelect.setVersion(version);
                int updateCount = qualityControlListMapper.updateSelective(qcSelect);
                if (updateCount < 1){
                    LOGGER.error("版本号:{},冲突，需要重试！！", version);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
                }

            } else {
                qcSelect.setOrderStatus(Byte.valueOf("2"));
                qcSelect.setQcFinishTotalNumber(qcFinishCount);
                qcSelect.setVersion(version);
                int updateCount = qualityControlListMapper.updateSelective(qcSelect);
                if (updateCount < 1){
                    LOGGER.error("版本号:{},冲突，需要重试！！", version);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
                }
            }
        }else if ("1".equals(flag)){
            Integer qcFinishCount = qualityControlDetailsMapper.selectCountByWaybillId(waybillNumber);
            Integer waybillPlanSumCount = sellingBackDetailMapper.selectCountByWaybillId(waybillNumber);
            List<String> statusList = qualityControlDetailsMapper.selectStatusByWaybillId(waybillNumber);
            Integer version = qualityControlListMapper.selectVersionByWaybillId(waybillNumber);
            if (qcFinishCount >= waybillPlanSumCount && !statusList.contains("1") && !statusList.contains("2")){
                qcSelect.setOrderStatus(Byte.valueOf("3"));
                qcSelect.setFinishTime(new Date());
                qcSelect.setQcFinishTotalNumber(qcFinishCount);
                qcSelect.setVersion(version);
                int updateCount = qualityControlListMapper.updateSelective(qcSelect);
                if (updateCount < 1){
                    LOGGER.error("版本号:{},冲突，需要重试！！", version);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
                }
            } else {
                qcSelect.setOrderStatus(Byte.valueOf("2"));
                qcSelect.setQcFinishTotalNumber(qcFinishCount);
                qcSelect.setVersion(version);
                int updateCount = qualityControlListMapper.updateSelective(qcSelect);
                if (updateCount < 1){
                    LOGGER.error("版本号:{},冲突，需要重试！！", version);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "质检失败，请重试");
                }
            }
        }
    }

    /**
     * @description: 上架策略分配
     * @param qcWaybillRequestDto: qc参数对象
     * @param skuObject: sku信息对象
     */
    private Map<String, String> strategyDistribution(QcWaybillRequestDto qcWaybillRequestDto, JSONObject skuObject) {
        Map<String, String> positionAndFloorMap = new HashMap<>();
        String sku = qcWaybillRequestDto.getSku();
        Double skuVolume = skuObject.getDouble("packageHeight") * skuObject.getDouble("packageLength") * skuObject.getDouble("packageWidth");
        //首先根据仓库查询上架策略,测试使用启用状态为2;
        List<PutawayStrategy> strategyList = putawayStrategyMapper.selectByWareHouse(qcWaybillRequestDto.getWarehouseCode(), "1");
        if (CollectionUtils.isNotEmpty(strategyList)){
            if (strategyList.size() > 1){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"该仓库存在多个策略，请检查策略管理！！！");
            }
            LOGGER.info("策略管理，已存在维护上架策略，策略数={}", strategyList.size());
            String strategyRule = strategyList.get(0).getStrategyRule();
            if ("2".equals(strategyRule)){
                LOGGER.info("执行策略【按商品分类】【strategyRule】={}", strategyRule);
                //查询当前仓库下的库区的货位信息
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("warehouseCode",qcWaybillRequestDto.getWarehouseCode());
                paramMap.put("categoryCode",skuObject.getString("categoryCode"));
                paramMap.put("flag", "1");

                //查询是否存在同sku，同分类库区，同分类货位
                LOGGER.info("查询出同分类库区，同分类货位;");
                JSONArray areaPositionArray = Utils.resultParseArray(remoteCenterService.getAreaPositionStock(paramMap),"基础数据","根据仓库编码和分类编码或sku获取库区下货位体积库存等信息接口异常");
                if (CollectionUtils.isEmpty(areaPositionArray)){
                    //同分类库区且同分类货位且空货位，不存在，查询同分类货位，筛选出是否存在同sku货位?
                    positionAndFloorMap = checkExistsSameTypeSkuPosition(qcWaybillRequestDto, skuObject, skuVolume);
                } else {
                    LOGGER.info("筛选出同分类库区，同分类货位，同sku货位，是否存在货位？");
                    paramMap.put("sku", sku);
                    JSONArray jsonArray = Utils.resultParseArray(remoteCenterService.getAreaPositionStock(paramMap),"基础数据","根据仓库编码和分类编码或sku获取库区下货位体积库存等信息接口异常");
                    Map<String, Double> mapPositions = new HashMap<>();
                    Map<String, String> mapFloor = new HashMap<>();
                    Map<String, String> mapSelectId = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(jsonArray)){
                        for (Object base : jsonArray) {
                            JSONObject jsonObj = (JSONObject) JSON.toJSON(base);
                            if (!"1".equals(jsonObj.getString("lockState"))){
                                //可分配货位的体积差,可分配货位体积差，越小越符合
                                Double readyVolume  = skuVolume * qcWaybillRequestDto.getGoodProductNumber();
                                double volumeAvailable = jsonObj.getDouble("remainVolume") - readyVolume;
                                String positionCode = jsonObj.getString("positionCode");
                                //体积差>=0的可选货位
                                if (volumeAvailable >= 0){
                                    mapPositions.put(positionCode, volumeAvailable);
                                    mapFloor.put(positionCode,jsonObj.getString("areaFloor"));
                                    mapSelectId.put(positionCode, jsonObj.getString("positionId"));
                                }
                            }
                        }
                    //是，存在多个sku货位体积差>=0
                    if (mapPositions.size() > 1){
                        LOGGER.info("存在，多个体积差大于等于0的同sku货位可分配货位;");
                        //体积差最小的升序排列
                        Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(mapPositions);
                        List<String> seriesList = minSeriesValue(stringIntegerMap);
                        List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
                        String recommendPosition = collect.get(0);
                        LOGGER.info("推荐货位【{}】", recommendPosition);
                        //分配货位
                        positionAndFloorMap.put("positionCode", recommendPosition);
                        positionAndFloorMap.put("areaFloor", mapFloor.get(recommendPosition));
                        positionAndFloorMap.put("positionId", mapSelectId.get(recommendPosition));
                    } else if(mapPositions.size() == 1) {
                        LOGGER.info("存在单个体积差大于等于0的可分配货位;");
                        //分配货位
                        Set<String> keySet = mapPositions.keySet();
                        String recommendPosition = keySet.iterator().next();
                        positionAndFloorMap.put("positionCode", recommendPosition);
                        positionAndFloorMap.put("areaFloor", mapFloor.get(recommendPosition));
                        positionAndFloorMap.put("positionId", mapSelectId.get(recommendPosition));
                    } else {
                        //同分类库区，同分类货位，同sku货位，不存在可用货位，筛选同分类库区，同分类货位,是否存在可用空货位？
                        positionAndFloorMap = checkExistsTwoSameTypePosition(areaPositionArray ,qcWaybillRequestDto, skuObject, skuVolume);
                    }
                } else {
                        //同分类库区，同分类货位，同sku货位，不存在可用货位，筛选同分类库区，同分类货位,是否存在可用空货位？
                        positionAndFloorMap = checkExistsTwoSameTypePosition(areaPositionArray ,qcWaybillRequestDto, skuObject, skuVolume);
                }
            }
        } else {
                LOGGER.info("策略管理，不存在维护上架策略，策略数={}", strategyList.size());
                LOGGER.info("执行策略【默认策略】");
                //分配货位
                positionAndFloorMap = defaultDistributePolicy(qcWaybillRequestDto, skuObject, skuVolume);
            }
        } else {
            LOGGER.info("策略管理，不存在维护上架策略，策略数={}", strategyList.size());
            LOGGER.info("执行策略【默认策略】");
            //分配货位
            positionAndFloorMap = defaultDistributePolicy(qcWaybillRequestDto, skuObject, skuVolume);
        }
        return positionAndFloorMap;
    }

    /**
     * @description: 筛选同分类库区，同分类货位,是否存在可用货位
     * @param areaPositionArray: 库区货位集合
     * @param qcWaybillRequestDto: qc请求参数对象
     * @param skuObject: sku对象信息
     */
    private Map<String, String> checkExistsTwoSameTypePosition(JSONArray areaPositionArray, QcWaybillRequestDto qcWaybillRequestDto, JSONObject skuObject, Double skuVolume) {
        LOGGER.info("同分类库区，同分类货位，同sku货位，不存在可用货位，筛选同分类库区，同分类货位,是否存在可用空货位？");
        Map<String, String> positionAndFloorMap = new HashMap<>();
        Map<String, Double> twoSameTypeIsNullMapPositions = new HashMap<>();
        Map<String, String> twoSameTypeMapFloor = new HashMap<>();
        Map<String, String> twoSameTypeMapId = new HashMap<>();
        for (Object base : areaPositionArray) {
            JSONObject jsonObj = (JSONObject) JSON.toJSON(base);
            if (!"1".equals(jsonObj.getString("lockState"))){
                //可分配货位的体积差,可分配货位体积差，越小越符合
                Double readyVolume  = skuVolume * qcWaybillRequestDto.getGoodProductNumber();
                double volumeAvailable = jsonObj.getDouble("remainVolume") - readyVolume;
                String positionCode = jsonObj.getString("positionCode");
                //当前货区可选的空货位
                if (jsonObj.getDouble("volume").equals(jsonObj.getDouble("remainVolume"))) {
                    twoSameTypeIsNullMapPositions.put(positionCode, Math.abs(volumeAvailable));
                }
                twoSameTypeMapFloor.put(positionCode,jsonObj.getString("areaFloor"));
                twoSameTypeMapId.put(positionCode, jsonObj.getString("positionId"));
            }
        }
        if (twoSameTypeIsNullMapPositions.size() > 1){
            LOGGER.info("同分类库区，同分类货位，存在多个空货位;");
            Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(twoSameTypeIsNullMapPositions);
            List<String> seriesList = minSeriesValue(stringIntegerMap);
            List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
            String recommendPosition = collect.get(0);
            //分配货位
            positionAndFloorMap.put("positionCode", recommendPosition);
            positionAndFloorMap.put("areaFloor", twoSameTypeMapFloor.get(recommendPosition));
            positionAndFloorMap.put("positionId", twoSameTypeMapId.get(recommendPosition));
        } else if (twoSameTypeIsNullMapPositions.size() == 1) {
            LOGGER.info("同分类库区，同分类货位，存在单个空货位;");
            Set<String> keySet = twoSameTypeIsNullMapPositions.keySet();
            String recommendPosition = keySet.iterator().next();
            positionAndFloorMap.put("positionCode", recommendPosition);
            positionAndFloorMap.put("areaFloor", twoSameTypeMapFloor.get(recommendPosition));
            positionAndFloorMap.put("positionId", twoSameTypeMapId.get(recommendPosition));
        } else {
            // 同分类库区，空货位，不存在，查询同分类货位，筛选出同sku货位？
            positionAndFloorMap = checkExistsSameTypeSkuPosition(qcWaybillRequestDto, skuObject, skuVolume);
        }
        return positionAndFloorMap;
    }


    /**
     * @description: 同分类库区，同分类货位且空货位不存在，查询同分类货位，筛选出同sku货位
     * @param qcWaybillRequestDto： qc请求参数集合
     * @param skuObject: sku对象信息
     */
    private Map<String, String> checkExistsSameTypeSkuPosition(QcWaybillRequestDto qcWaybillRequestDto, JSONObject skuObject, Double skuVolume) {
            String sku = qcWaybillRequestDto.getSku();
            Map<String, String> positionAndFloorMap = new HashMap<>();
            LOGGER.info("同分类库区，空货位，不存在，查询同分类货位，筛选出同sku货位？");
            Map<String,String> sameTypeParamMap = new HashMap<>();
            sameTypeParamMap.put("warehouseCode",qcWaybillRequestDto.getWarehouseCode());
            sameTypeParamMap.put("categoryCode",skuObject.getString("categoryCode"));
            sameTypeParamMap.put("sku", sku);
            JSONArray sameTypePositionArray = Utils.resultParseArray(remoteCenterService.getPositionStock(sameTypeParamMap), "基础数据", "根据仓库编码和分类编码或sku获取库区下货位体积库存等信息接口异常");
            List<JSONObject> list = sameTypePositionArray.toJavaList(JSONObject.class);
            Map<String, Double> twoSameTypePositionMap = new HashMap<>();
            Map<String, String> twoSameTypePositionFloor = new HashMap<>();
            Map<String, String> twoSameTypePositionMapId = new HashMap<>();
            for (JSONObject base : list) {
                if (!"1".equals(base.getString("lockState"))){
                    Double remainVolume = base.getDouble("remainVolume");
                    //可分配货位体积差,可分配货位体积差，越小越符合
                    double volumeAvailable = remainVolume -(skuVolume * qcWaybillRequestDto.getGoodProductNumber());
                    String  positionCode = base.getString("positionCode");
                    if (volumeAvailable >= 0){
                        twoSameTypePositionMap.put(positionCode, volumeAvailable);
                    }
                    twoSameTypePositionMapId.put(positionCode, base.getString("positionId"));
                    twoSameTypePositionFloor.put(positionCode, base.getString("areaFloor"));
                }
            }
            if (twoSameTypePositionMap.size() > 1){
                LOGGER.info("存在多个同分类，同sku货位;");
                Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(twoSameTypePositionMap);
                List<String> seriesList = minSeriesValue(stringIntegerMap);
                List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
                String recommendPosition = collect.get(0);
                //分配货位
                positionAndFloorMap.put("positionCode", recommendPosition);
                positionAndFloorMap.put("areaFloor", twoSameTypePositionFloor.get(recommendPosition));
                positionAndFloorMap.put("positionId", twoSameTypePositionMapId.get(recommendPosition));
            } else if (twoSameTypePositionMap.size() == 1){
                LOGGER.info("存在单个同分类，同sku货位;");
                Set<String> keySet = twoSameTypePositionMap.keySet();
                String recommendPosition = keySet.iterator().next();
                positionAndFloorMap.put("positionCode", recommendPosition);
                positionAndFloorMap.put("areaFloor", twoSameTypePositionFloor.get(recommendPosition));
                positionAndFloorMap.put("positionId", twoSameTypePositionMapId.get(recommendPosition));
            } else {
                //同分类，同sku货位，不存在，筛选同分类空货位;
                positionAndFloorMap = checkSameTypeNullPostion(qcWaybillRequestDto, skuObject, skuVolume);
            }
            return positionAndFloorMap;
    }

    /**
     * @description: 同分类，同sku货位，不存在，筛选同分类空货位
     * @param qcWaybillRequestDto： qc请求参数对象
     * @param skuObject: sku对象信息
     */
    private Map<String, String> checkSameTypeNullPostion(QcWaybillRequestDto qcWaybillRequestDto, JSONObject skuObject, Double skuVolume) {
        LOGGER.info("同分类，同sku货位，不存在，筛选同分类空货位;");
        Map<String,String> sameTypeParamMap = new HashMap<>();
        Map<String, String> positionAndFloorMap = new HashMap<>();
        sameTypeParamMap.put("warehouseCode",qcWaybillRequestDto.getWarehouseCode());
        sameTypeParamMap.put("categoryCode",skuObject.getString("categoryCode"));
        JSONArray sameTypePositionArray = Utils.resultParseArray(remoteCenterService.getPositionStock(sameTypeParamMap), "基础数据", "根据仓库编码和分类编码或sku获取库区下货位体积库存等信息接口异常");
        List<JSONObject> list = sameTypePositionArray.toJavaList(JSONObject.class);
        Map<String, Double> sameTypeNullPositionMap = new HashMap<>();
        Map<String, String> sameTypeNullFloor = new HashMap<>();
        Map<String, String> sameTypeNullMapId = new HashMap<>();
        for (JSONObject base : list){
            if (!"1".equals(base.getString("lockState"))){
                Double remainVolume = base.getDouble("remainVolume");
                //可分配货位体积差,可分配货位体积差，越小越符合
                double volumeAvailable = remainVolume -(skuVolume * qcWaybillRequestDto.getGoodProductNumber());
                String  positionCode = base.getString("positionCode");
                if (base.getDouble("volume").equals(base.getDouble("remainVolume"))) {
                    sameTypeNullPositionMap.put(positionCode, Math.abs(volumeAvailable));
                    sameTypeNullMapId.put(positionCode, base.getString("positionId"));
                    sameTypeNullFloor.put(positionCode, base.getString("areaFloor"));
                }
            }
        }
         if (sameTypeNullPositionMap.size() != 0){
            LOGGER.info("同分类，同sku货位，不存在，筛选同分类空货位;");
            Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(sameTypeNullPositionMap);
            List<String> seriesList = minSeriesValue(stringIntegerMap);
            List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
            String recommendPosition = collect.get(0);
            //分配货位
            positionAndFloorMap.put("positionCode", recommendPosition);
            positionAndFloorMap.put("areaFloor", sameTypeNullFloor.get(recommendPosition));
            positionAndFloorMap.put("positionId", sameTypeNullMapId.get(recommendPosition));
        } else {
             LOGGER.info("同分类空货位，不存在，走默认策略，查询同sku货位；");
             positionAndFloorMap = defaultDistributePolicy(qcWaybillRequestDto, skuObject, skuVolume);
         }
        return positionAndFloorMap;
    }

    /**
     * @description: 同分类空货位，不存在，执行默认策略，查询是否存在同sku货位
     * @param qcWaybillRequestDto: qc请求参数对象
     * @param skuObject: sku信息对象
     */
    private Map<String, String> defaultDistributePolicy(QcWaybillRequestDto qcWaybillRequestDto, JSONObject skuObject, Double skuVolume) {
        LOGGER.info("同分类空货位，不存在，执行默认策略，查询是否存在同sku货位？");
        String sku = qcWaybillRequestDto.getSku();
        Map<String, String> positionAndFloorMap = new HashMap<>();
        List<String> skuList = new ArrayList<>();
        skuList.add(sku);
        JSONArray mixArray = Utils.resultParseArray(remoteCenterService.getMixDetailBySku(skuList), "基础数据", "根据SKU查询仓库库区货位等信息接口异常");
        if (CollectionUtils.isNotEmpty(mixArray)){
            List<JSONObject> mixPositionLit = mixArray.toJavaList(JSONObject.class);
            Map<String, Double> mixPositionMap = new HashMap<>();
            Map<String, String> mixPositionMapId = new HashMap<>();
            Map<String, String> mixPositionFloor = new HashMap<>();
            for (JSONObject base : mixPositionLit){
                if (!"1".equals(base.getString("lockState")) && StringUtils.equals(base.getString("warehouseCode"), qcWaybillRequestDto.getWarehouseCode())
                && "1".equals(base.getString("areaState")) && "1".equals(base.getString("rowState"))){
                    Double remainVolume = base.getDouble("remainVolume");
                    //可分配货位体积差,可分配货位体积差，越小越符合
                    double volumeAvailable = remainVolume -(skuVolume * qcWaybillRequestDto.getGoodProductNumber());
                    String  positionCode = base.getString("positionCode");
                    if (volumeAvailable >= 0){
                        mixPositionMap.put(positionCode, volumeAvailable);
                    }
                    mixPositionMapId.put(positionCode, base.getString("positionId"));
                    mixPositionFloor.put(positionCode, base.getString("areaFloor"));
                }
            }
            if (mixPositionMap.size() != 0){
                LOGGER.info("存在一个或多个同sku货位");
                Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(mixPositionMap);
                List<String> seriesList = minSeriesValue(stringIntegerMap);
                List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
                String recommendPosition = collect.get(0);
                //分配货位
                positionAndFloorMap.put("positionCode", recommendPosition);
                positionAndFloorMap.put("areaFloor", mixPositionFloor.get(recommendPosition));
                positionAndFloorMap.put("positionId", mixPositionMapId.get(recommendPosition));
            } else {
                //同sku货位，不存在，找仓库下的空货位；
                positionAndFloorMap = checkNullPosition(qcWaybillRequestDto, skuVolume);
            }
        } else {
            //同sku货位，不存在，找仓库下的空货位；
            positionAndFloorMap = checkNullPosition(qcWaybillRequestDto, skuVolume);
        }
        return positionAndFloorMap;
    }

    /**
     * @description: 同sku货位，不存在，找仓库下的空货位
     * @param qcWaybillRequestDto: qc请求参数对象
     * @param skuVolume: sku体积
     */
    private Map<String, String> checkNullPosition(QcWaybillRequestDto qcWaybillRequestDto, Double skuVolume) {
        LOGGER.info("同sku货位，不存在，找仓库下的空货位；");
        Map<String, String> positionAndFloorMap = new HashMap<>();
        Map<String, Double> mixNullPositionMap = new HashMap<>();
        Map<String, String> mixNullPositionMapId = new HashMap<>();
        Map<String, String> mixNullPositionFloor = new HashMap<>();
        JSONArray availablePositionArray = Utils.resultParseArray(remoteCenterService.queryAllAllowPosition(qcWaybillRequestDto.getWarehouseCode()), "基础数据", "根据仓库编码查询所有可用的货位信息接口异常");
        if (CollectionUtils.isNotEmpty(availablePositionArray)){
            List<JSONObject> availablePositionList = availablePositionArray.toJavaList(JSONObject.class);
            for (JSONObject jsonObj : availablePositionList) {
                if (!"1".equals(jsonObj.getString("lockState"))){
                    Double readyVolume  = skuVolume * qcWaybillRequestDto.getGoodProductNumber();
                    double volumeAvailable = jsonObj.getDouble("remainVolume") - readyVolume;
                    String positionCode = jsonObj.getString("positionCode");
                    //当前货区可选的空货位
                    if (jsonObj.getDouble("volume").equals(jsonObj.getDouble("remainVolume"))) {
                        mixNullPositionMap.put(positionCode, Math.abs(volumeAvailable));
                    }
                    mixNullPositionFloor.put(positionCode,jsonObj.getString("areaFloor"));
                    mixNullPositionMapId.put(positionCode, jsonObj.getString("positionId"));
                }
            }
            if (mixNullPositionMap.size() != 0){
                Map<String, Double> stringIntegerMap = MapSortUtil.ascSortByValue(mixNullPositionMap);
                List<String> seriesList = minSeriesValue(stringIntegerMap);
                List<String> collect = seriesList.stream().sorted(StringUtils::compare).collect(Collectors.toList());
                String recommendPosition = collect.get(0);
                positionAndFloorMap.put("positionCode", recommendPosition);
                positionAndFloorMap.put("areaFloor", mixNullPositionFloor.get(recommendPosition));
                positionAndFloorMap.put("positionId", mixNullPositionMapId.get(recommendPosition));
            } else {
                LOGGER.info("仓库内库区开启and货列开启and未锁定的空货位数为：{}，未找到可用货位！！！",mixNullPositionMap.size());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未找到可用货位,请维护！！！");
            }
        } else {
            LOGGER.info("仓库内库区开启and货列开启and未锁定的空货位数为：{}，未找到可用货位！！！",availablePositionArray.size());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未找到可用货位,请维护！！！");
        }
        return positionAndFloorMap;
    }

    @Override
    public String addQCBoxFinish(List<QcSealBoxReqDto> qcSealBoxReqList) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        //获取基础数据箱号状态及本地占用状态;
        QcSealBoxReqDto reqDto = qcSealBoxReqList.get(0);
        if (StringUtils.isBlank(reqDto.getSealBoxSerialNumber())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "容器号不能为空");
        } else if (StringUtils.isBlank(reqDto.getSkuFloor())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "楼层信息不能为空");
        } else if (StringUtils.isBlank(reqDto.getWarehouseCode())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "仓库编码不能为空");
        }
        String containerInfo = remoteCenterService.getContainerByCode(reqDto.getWarehouseCode(), reqDto.getSealBoxSerialNumber());
        JSONObject jsonObject = Utils.resultParseObj(containerInfo, "基础数据","查询质检箱状态接口异常");

        if (null == jsonObject){
            LOGGER.info("容器号：【{}】,基础配置不存在该箱号", reqDto.getSealBoxSerialNumber());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "不存在该容器号："+reqDto.getSealBoxSerialNumber()+",请维护!!!");
        } else if ("0".equals(jsonObject.getString("usageState"))){
            LOGGER.info("容器号：【{}】,基础配置未开启", reqDto.getSealBoxSerialNumber());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "该容器号："+reqDto.getSealBoxSerialNumber()+",未开启");
        } else if ("0".equals(jsonObject.getString("workState"))){
            LOGGER.info("容器号：【{}】,已被占用请更换质检箱",reqDto.getSealBoxSerialNumber());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "该容器号："+reqDto.getSealBoxSerialNumber()+",已被占用请更换质检箱");
        }

        //上架单号
        String putawayId = null;
        for (QcSealBoxReqDto qcSealBoxReqDto : qcSealBoxReqList) {
            //查询qc详情表
            QcWaybillInfoDto qcDetail = qualityControlDetailsMapper.selectDetailByPrimaryKey(Long.valueOf(qcSealBoxReqDto.getSid()));

            if (null == qcDetail){
                LOGGER.error("需要封箱的sid=【{}】的商品SKU不存在", qcSealBoxReqDto.getSid());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"需要封箱的sid="+qcSealBoxReqDto.getSid()+"的商品SKU不存在");
            } else if (qcDetail.getSealBoxUnfinishNumber() == 0){
                LOGGER.error("该运单:【{}】,可封箱数量为0,不能执行封箱", qcDetail.getWaybillId());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"该运单:"+qcDetail.getWaybillId()+",可封箱数量为0,不能执行封箱");
            }

            List<PutawayShelf> putawayShelfList = putawayShelfMapper.selectDistributed(qcSealBoxReqDto.getWarehouseCode(), qcDetail.getWaybillId(), qcDetail.getSKU(), qcDetail.getSkuFloor());
            if (CollectionUtils.isEmpty(putawayShelfList)){
                LOGGER.error( "分配的sku：【{}】分配的货位数据不存在！！", qcDetail.getSKU());
               throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402.getCode(), "分配的sku：【"+qcDetail.getSKU()+"】分配的货位数据不存在！！");
            }

            //预封箱分配数量
            Integer predistributionNum;
            //剩余数量
            Integer remainNum;
            //协助人预封箱数
            Integer assistantsPreNum;
            if (StringUtils.isNotBlank(qcSealBoxReqDto.getSkuDefinedNumber())){
                LOGGER.info("本次为多容器绑定");
                //场景1：自定义封箱数：1，单货位库存：10，多货位总和：30，假设sku有3个货位，与分配货位
                //场景2：自定义封箱数:11，单货位库存：10，多货位总和：30，假设sku有3个货位，与分配货位(优先最复杂的情况)
                predistributionNum = Integer.valueOf(qcSealBoxReqDto.getSkuDefinedNumber());
                assistantsPreNum = predistributionNum;
                if (predistributionNum > qcDetail.getSealBoxUnfinishNumber()){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "多容器绑定数量不能超过该SKU未封箱数量！！");
                }
                //计划上架数量
                Integer planPutawayNum = predistributionNum;
                for (PutawayShelf putawayShelf : putawayShelfList){
                    if (0 == putawayShelf.getSkuRemainNum()){
                        continue;
                    }
                    //剩余sku数量
                    remainNum = putawayShelf.getSkuRemainNum() - predistributionNum;
                    if (remainNum < 0){
                        remainNum = 0;
                        predistributionNum = predistributionNum - putawayShelf.getSkuRemainNum();
                        planPutawayNum = putawayShelf.getSkuRemainNum();
                    } else {
                        predistributionNum = predistributionNum - (putawayShelf.getSkuRemainNum() - remainNum);
                    }


                    putawayShelf.setSkuRemainNum(remainNum);
                    int affectCount = putawayShelfMapper.updateRemainNumByPrimaryKey(putawayShelf);
                    if (affectCount < 1){
                        LOGGER.error("版本号:{},冲突，需要重试！！", putawayShelf.getVersion());
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                    }
                    QualityBoxPutaway qcBox = new QualityBoxPutaway();
                    qcBox.setSealBoxSerialNumber(qcSealBoxReqDto.getSealBoxSerialNumber());
                    qcBox.setSku(qcDetail.getSKU());
                    qcBox.setWaybillNumber(qcDetail.getWaybillId());
                    qcBox.setSkuFloor(qcSealBoxReqDto.getSkuFloor());
                    qcBox.setWarehouseCode(reqDto.getWarehouseCode());
                    qcBox.setRecommendedLocationCode(putawayShelf.getRecommendedLocationCode());
                    qcBox.setStatus(0);
                    qcBox.setPlannedPutawayNumber(planPutawayNum);
                    qcBox.setActualPutawayNumber(0);

                    //生成上架单
                    if (StringUtils.isBlank(putawayId)){
                        synchronized (this){
                            PutawayList putaway = new PutawayList();
                            String putawayOrder = putawayListMapper.selectLastData();
                            putawayId = genOrder("US", putawayOrder);
                            putaway.setWarehouseCode(String.valueOf(qcSealBoxReqDto.getWarehouseCode()));
                            putaway.setPutawayId(putawayId);
                            putaway.setCustomerName(qcDetail.getCustomerName());
                            putaway.setSourceOrderNumber(qcDetail.getSourceId());
                            putaway.setSourceType(null == qcDetail.getSourceType() ? 2 : qcDetail.getSourceType());
                            putaway.setWaybillNumber(qcDetail.getWaybillId());
                            putaway.setPutawayStatus(1);
                            putaway.setCreaterId(Long.valueOf(userInfo.getId()));
                            putaway.setCreaterBy(userInfo.getName());
                            putaway.setIsException(Byte.valueOf("0"));
                            putawayListMapper.insert(putaway);
                        }
                    }
                    //更新封箱货位表
                    qcBox.setPutawayId(putawayId);
                    qualityBoxPutawayMapper.insertSelective(qcBox);

                    boolean assistantsFlag = false;
                    List<QualityControlAssistants> assistantsList = qualityControlAssistantsMapper.selectAssistants(putawayShelf.getWaybillNumber(), putawayShelf.getSku(), putawayShelf.getRecommendedLocationCode());
                    for (QualityControlAssistants assistants : assistantsList) {
                        Integer planNum = assistantsPreNum;
                        if (assistantsFlag){
                            continue;
                        }
                        int remainAsSkuNum = assistants.getSkuRemainNum() - assistantsPreNum;
                        if (remainAsSkuNum < 0){
                            remainAsSkuNum = 0;
                            planNum = assistants.getSkuRemainNum();
                            assistantsPreNum = assistantsPreNum - assistants.getSkuRemainNum();
                        } else {
                            assistantsPreNum = assistantsPreNum - (assistants.getSkuRemainNum() - remainAsSkuNum);
                        }
                        assistants.setSkuRemainNum(remainAsSkuNum);
                        int count = qualityControlAssistantsMapper.updateByPrimaryKeySelective(assistants);
                        if (count < 1){
                            LOGGER.error("版本号:{},冲突，需要重试！！", assistants.getVersion());
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                        }

                        synchronized (this){
                            List<QualityBoxAssistants> qualityBoxAssistantsList = qualityBoxAssistantsMapper.selectByZeroAndAssistantsSid(assistants.getSid());

                            QualityBoxAssistants qcBoxAssistants = new QualityBoxAssistants();
                            qcBoxAssistants.setBoxSid(qcBox.getSid());
                            qcBoxAssistants.setAssistantsSid(assistants.getSid());
                            qcBoxAssistants.setSealNum(planNum);
                            if (remainAsSkuNum > 0 && CollectionUtils.isEmpty(qualityBoxAssistantsList)) {
                                qualityBoxAssistantsMapper.insertSelective(qcBoxAssistants);
                                qcBoxAssistants.setBoxSid(0L);
                                qcBoxAssistants.setSealNum(remainAsSkuNum);
                                qualityBoxAssistantsMapper.insertSelective(qcBoxAssistants);
                            } else if (CollectionUtils.isEmpty(qualityBoxAssistantsList)){
                                qualityBoxAssistantsMapper.insertSelective(qcBoxAssistants);
                            } else if (remainAsSkuNum > 0 && CollectionUtils.isNotEmpty(qualityBoxAssistantsList)){
                                qcBoxAssistants.setVersion(qualityBoxAssistantsList.get(0).getVersion());
                                int updateCount = qualityBoxAssistantsMapper.updateByZeroAndAssistantsSid(qcBoxAssistants);
                                if (updateCount < 1){
                                    LOGGER.error("版本号:{},冲突，需要重试！！", assistants.getVersion());
                                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                                }
                                qcBoxAssistants.setBoxSid(0L);
                                qcBoxAssistants.setSealNum(remainAsSkuNum);
                                qualityBoxAssistantsMapper.insertSelective(qcBoxAssistants);
                            } else {
                                qcBoxAssistants.setVersion(qualityBoxAssistantsList.get(0).getVersion());
                                int updateCount = qualityBoxAssistantsMapper.updateByZeroAndAssistantsSid(qcBoxAssistants);
                                if (updateCount < 1){
                                    LOGGER.error("版本号:{},冲突，需要重试！！", assistants.getVersion());
                                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                                }
                            }
                        }

                        if (assistantsPreNum <= 0){
                            assistantsFlag = true;
                        }
                    }
                    if (assistantsPreNum <= 0){
                        break;
                    }
                }
                QualityControlDetails details = new QualityControlDetails();
                details.setSid(Long.valueOf(qcDetail.getId()));
                details.setSealBoxFinishNumber(qcDetail.getSealBoxFinishNumber() + Integer.valueOf(qcSealBoxReqDto.getSkuDefinedNumber()));
                details.setSealBoxUnfinishNumber(qcDetail.getSealBoxUnfinishNumber() - Integer.valueOf(qcSealBoxReqDto.getSkuDefinedNumber()));
                details.setVersion(qcDetail.getVersion());
                //更新质检详情
                int updateCount = qualityControlDetailsMapper.updateByPrimaryKeySelective(details);
                if (updateCount < 1){
                    LOGGER.error("版本号:{},冲突，需要重试！！", details.getVersion());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                }

            } else {
                LOGGER.info("本次为单容器全部绑定");
                //全封箱：3种sku，每个存在单货位库存：10，各个多货位总和都是：30，假设每个sku有3个货位与分配货位(优先最复杂的情况)
                predistributionNum = qcDetail.getSealBoxUnfinishNumber();
                Integer planSealingNum = predistributionNum;
                Integer planOnceNum;
                for (PutawayShelf putawayShelf : putawayShelfList){
                    if (0 == putawayShelf.getSkuRemainNum()){
                        continue;
                    }
                    //剩余sku数量
                    predistributionNum = predistributionNum - putawayShelf.getSkuRemainNum();
                    planOnceNum = putawayShelf.getSkuRemainNum();
                    putawayShelf.setSkuRemainNum(0);
                    int updateCount = putawayShelfMapper.updateRemainNumByPrimaryKey(putawayShelf);
                    if (updateCount < 1){
                        LOGGER.error("版本号:{},冲突，需要重试！！", putawayShelf.getVersion());
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                    }
                    QualityBoxPutaway qcBox = new QualityBoxPutaway();
                    qcBox.setSealBoxSerialNumber(qcSealBoxReqDto.getSealBoxSerialNumber());
                    qcBox.setSku(qcDetail.getSKU());
                    qcBox.setWaybillNumber(qcDetail.getWaybillId());
                    qcBox.setSkuFloor(qcSealBoxReqDto.getSkuFloor());
                    qcBox.setWarehouseCode(reqDto.getWarehouseCode());
                    qcBox.setRecommendedLocationCode(putawayShelf.getRecommendedLocationCode());
                    qcBox.setStatus(0);
                    qcBox.setPlannedPutawayNumber(planOnceNum);
                    qcBox.setActualPutawayNumber(0);
                    //生成上架单
                    if (StringUtils.isBlank(putawayId)){
                        synchronized (this){
                            PutawayList putaway = new PutawayList();
                            String putawayOrder = putawayListMapper.selectLastData();
                            putawayId = genOrder("US", putawayOrder);
                            putaway.setWarehouseCode(String.valueOf(qcSealBoxReqDto.getWarehouseCode()));
                            putaway.setPutawayId(putawayId);
                            putaway.setCustomerName(qcDetail.getCustomerName());
                            putaway.setSourceOrderNumber(qcDetail.getSourceId());
                            putaway.setSourceType(null == qcDetail.getSourceType() ? 2 : qcDetail.getSourceType());
                            putaway.setWaybillNumber(qcDetail.getWaybillId());
                            putaway.setPutawayStatus(1);
                            putaway.setCreaterId(Long.valueOf(userInfo.getId()));
                            putaway.setCreaterBy(userInfo.getName());
                            putaway.setIsException(Byte.valueOf("0"));
                            putawayListMapper.insert(putaway);
                        }
                    }
                    //更新封箱货位表
                    qcBox.setPutawayId(putawayId);
                    qualityBoxPutawayMapper.insertSelective(qcBox);

                    List<QualityControlAssistants> assistantsList = qualityControlAssistantsMapper.selectAssistants(putawayShelf.getWaybillNumber(), putawayShelf.getSku(), putawayShelf.getRecommendedLocationCode());
                    for (QualityControlAssistants assistants : assistantsList) {
                        assistants.setSkuRemainNum(0);
                        int affectCount = qualityControlAssistantsMapper.updateByPrimaryKeySelective(assistants);
                        if (affectCount < 1){
                            LOGGER.error("版本号:{},冲突，需要重试！！", assistants.getVersion());
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                        }

                        synchronized (this){
                            List<QualityBoxAssistants> qualityBoxAssistantsList = qualityBoxAssistantsMapper.selectByZeroAndAssistantsSid(assistants.getSid());
                            QualityBoxAssistants qcBoxAssistants = new QualityBoxAssistants();
                            qcBoxAssistants.setBoxSid(qcBox.getSid());
                            qcBoxAssistants.setAssistantsSid(assistants.getSid());
                            if (CollectionUtils.isEmpty(qualityBoxAssistantsList)){
                                qcBoxAssistants.setSealNum(assistants.getQcNumber());
                                qualityBoxAssistantsMapper.insertSelective(qcBoxAssistants);
                            } else {
                                qcBoxAssistants.setSealNum(qualityBoxAssistantsList.get(0).getSealNum());
                                qcBoxAssistants.setVersion(qualityBoxAssistantsList.get(0).getVersion());
                               int count = qualityBoxAssistantsMapper.updateByZeroAndAssistantsSid(qcBoxAssistants);
                               if (count < 1){
                                   LOGGER.error("版本号:{},冲突，需要重试！！", qcBoxAssistants.getVersion());
                                   throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402, "封箱失败，请重试");
                               }
                            }
                        }
                    }

                }
                QualityControlDetails details = new QualityControlDetails();
                details.setSid(Long.valueOf(qcDetail.getId()));
                details.setSealBoxFinishNumber(qcDetail.getSealBoxFinishNumber() + planSealingNum);
                details.setSealBoxUnfinishNumber(qcDetail.getSealBoxUnfinishNumber() - planSealingNum);
                //更新质检详情
                qualityControlDetailsMapper.updateByPrimaryKeySelective(details);

            }
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("warehouseCode", reqDto.getWarehouseCode());
        paramMap.put("containerCode", reqDto.getSealBoxSerialNumber());
        paramMap.put("usageBy", userInfo.getName());
        paramMap.put("usageTime", DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
        paramMap.put("workState", "0");
        //更新容器状态
        String updateResult = Utils.getResultData(remoteCenterService.altWorkState(paramMap), SystemConstants.nameType.SYS_CENTER,"更新容器状态异常");
        if("false".equals(updateResult)){
            LOGGER.info("更新基础数据容器号:【{}】状态失败", reqDto.getSealBoxSerialNumber());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "基础数据更新容器状态失败");
        }
        return "success";
    }


    @Override
    public String addQCException(QcReportExceptionRequestDto qcReportExceptionRequestDto) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();

        String exceptionBoxNumber = qcReportExceptionRequestDto.getExceptionBoxNumber();

        JSONObject containerObj = Utils.resultParseObj(remoteCenterService.getContainerByCode(qcReportExceptionRequestDto.getWarehouseCode(), exceptionBoxNumber), "基础数据","查询容器信息异常！！");
        if (null == containerObj){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "容器编号【"+exceptionBoxNumber+"】不存在，请维护！！！");
        } else if("0".equals(containerObj.getString("usageState"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "容器编号【"+exceptionBoxNumber+"】已被禁用，请更换容器！！！");
        } else if ("0".equals(containerObj.getString("workState"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "容器编号【"+exceptionBoxNumber+"】已被占用，请更换容器！！！");
        }

        QualityExceptionControl qualityExceptionControl = new QualityExceptionControl();
        BeanUtils.copyProperties(qcReportExceptionRequestDto,qualityExceptionControl);
        qualityExceptionControl.setExceptionReporterId(Long.valueOf(userInfo.getId()));
        qualityExceptionControl.setExceptionReporterBy(userInfo.getName());
        qualityExceptionControlMapper.insertSelective(qualityExceptionControl);

        //更新详情表异常状态
        QualityControlDetails qalityControlDetails = new QualityControlDetails();
        qalityControlDetails.setSid(qcReportExceptionRequestDto.getDetailId());
        qalityControlDetails.setIsExceptionCase(Byte.valueOf("1"));
        qualityControlDetailsMapper.updateByPrimaryKeySelective(qalityControlDetails);

        //推送异常信息
        Map<String, Object> param = new HashMap<>();
        param.put("boxNo", exceptionBoxNumber);
        param.put("createBy", userInfo.getAccount());
        param.put("exNumber", qcReportExceptionRequestDto.getExceptionSkuCount());
        param.put("exReason", qcReportExceptionRequestDto.getExceptionCauseId());
        param.put("goodsSku", qcReportExceptionRequestDto.getSku());
        param.put("sourceNo", qcReportExceptionRequestDto.getSourceOrderNumber());
        param.put("sourceType", "1");
        param.put("warehouseCode", qcReportExceptionRequestDto.getWarehouseCode());
        param.put("wayBillNo", qcReportExceptionRequestDto.getWaybillNumber());

        Utils.resultParseObj(remoteCenterService.addEx(param), "基础数据","异常上报接口异常！！");
        return "success";
    }

    @Override
    public Page<QcExceptionDetailResDto> getQCException(String waybillNumber, String pageNum, String row) {
        if (StringUtils.isNotBlank(pageNum) && StringUtils.isNotBlank(row)){
            Page.builder(pageNum, row);
        }
        List<QcExceptionDetailResDto> result = qualityExceptionControlMapper.selectQCExceptionBywaybillNumber(waybillNumber);
        PageInfo<QcExceptionDetailResDto> pageInfo = new PageInfo(result);
        return new Page<>(pageInfo);
    }

    @Override
    public Page<QcListResponseDto> getQCList(QcListRequestDto qcListRequest) {
        UserDetailInfo userInfo = getUserDetailInfoUtil.getUserDetailInfo();
        List<QcListResponseDto> result = null;
        if ( 0 == qcListRequest.getFlag()){
            if (CollectionUtils.isEmpty(qcListRequest.getWarehouse())){
                qcListRequest.setWarehouse(Arrays.asList(userInfo.getWarehouseCode().split(",")));
            }
            result = qualityControlListMapper.getQCList(qcListRequest);
        } else if (1 == qcListRequest.getFlag()){
            if (StringUtils.isBlank(qcListRequest.getQualityControlOrderNumber())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(),"质检单号不能为空");
            }
            if (CollectionUtils.isEmpty(qcListRequest.getWarehouse())){
                qcListRequest.setWarehouse(Arrays.asList(userInfo.getWarehouseCode().split(",")));
            }
            result = qualityControlDetailsMapper.getQCListDetails(qcListRequest.getQualityControlOrderNumber());
        }
        PageInfo<QcListResponseDto> pageInfo = new PageInfo(result);
        return new Page<>(pageInfo);
    }

    /**
     * @description: 生成单号（前缀+yyMMdd+6位流水号）
     * @param pre：单号前缀
     * @param Order: 单号
     * @return
     */
    public String genOrder(String pre, String Order){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String newDate = sdf.format(new Date());
        if (StringUtils.isNotBlank(Order)){
            String dateAndSerialNum = Order.substring(pre.length());
            String orderDate = dateAndSerialNum.substring(0,6);
            Integer orderNum = Integer.valueOf(dateAndSerialNum.substring(6));
            if (!newDate.equals(orderDate)){
                orderNum = 0;
            }
            DecimalFormat df = new DecimalFormat("000000");
            orderNum++;
            String resultOrder = df.format(orderNum);
            return pre.concat(newDate.concat(resultOrder));
        } else {
            Integer orderNum = 0;
            DecimalFormat df = new DecimalFormat("000000");
            orderNum++;
            String resultOrder = df.format(orderNum);
            return pre.concat(newDate.concat(resultOrder));
        }
    }

    /**
     * @description: 获取map集合相等的最小值对应的key
     * @param map
     * @return
     */
    private List<String> minSeriesValue(Map<String,Double> map){
        //map的value
        Collection<Double> s = map.values();
        List<Double> list = new ArrayList<>(s);
        int b = 0;
        for (int i = 0; i<list.size()-1; i++){
            if (!list.get(i).equals(list.get(0))){
                break;
            }
            b++;
        }
        List<String> resultList = new ArrayList<>();
        for(Map.Entry<String,Double> str : map.entrySet()) {
            if(str.getValue().equals(list.get(0))){
                resultList.add(str.getKey());
            }
            if (resultList.size() > b){
                break;
            }
        }
        return resultList;
    }
}