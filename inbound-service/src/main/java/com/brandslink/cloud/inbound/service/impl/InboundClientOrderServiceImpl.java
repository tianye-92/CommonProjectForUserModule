package com.brandslink.cloud.inbound.service.impl;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.utils.ExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.entity.CustomerUserDetailInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.AttestationSignUtil;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsInfo;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.remote.RemoteCenterService;
import com.brandslink.cloud.inbound.service.InboundClientOrderService;
import com.brandslink.cloud.inbound.utils.MUtils;
import com.brandslink.cloud.inbound.utils.SimpleExcelUtil;
import com.github.pagehelper.PageInfo;

/**
 * @author Mir ZHANG
 * @version 1.0
 * @description: 入库客户端业务层实现
 * @date 2019/8/27 10:47
 */
@Service
public class InboundClientOrderServiceImpl implements InboundClientOrderService {
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	@Resource
	private RemoteCenterService remoteCenterService;
	@Resource
	private MUtils mUtils;
	@Resource
	private AttestationSignUtil attestationSignUtil;
	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;

	private final Logger logger = LoggerFactory.getLogger(InboundClientOrderServiceImpl.class);
	
	
	/**
	 * 添加入库订单信息
	 */

	public boolean insertReceiveArrivalNotice(ReceiveArrivalNotice notice) throws SQLIntegrityConstraintViolationException {
		CustomerUserDetailInfo customerUserDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
		CustomerInfoEntity customerInfoEntity = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity();
		List<ReceiveGoodDetailsInfo> details = notice.getReceiveGoodDetails();

		if(StringUtils.isBlank(notice.getWaybillId())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号不能为空");
		}
		if(receiveGoodDetailsMapper.checkWayBillIdAmount(notice.getWaybillId())>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407,"运单号"+notice.getWaybillId()+"已存在,请检查后再试!");
		}
		if(StringUtils.isBlank(notice.getPlannedTime())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"预到仓时间不能为空");
		}

		/**-------------------- 校验商品详情信息是否为空及sku是否存在----------------------------------- */
		ArrayList<String> listForCheckSku = new ArrayList<>();
		details.stream().forEach(r->{
			if(StringUtils.isBlank(r.getSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"商品sku不能为空");
			}
			if(listForCheckSku.contains(r.getSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"商品详情信息中sku: "+r.getSku()+" 重复,不能入库 请检查后再试!");
			}
			if(r.getPlannedQuantity() == null || r.getPlannedQuantity() <= 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"商品计划数量错误");
			}
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if(skuInfoBySku == null || "[]".equals(skuInfoBySku))
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"客户端系统中无sku:"+r.getSku()+"的对应信息,不能入库 请检查");
			listForCheckSku.add(r.getSku());
		});


		ArrayList<Integer> plannedNumber = new ArrayList<>();
		for (ReceiveGoodDetailsInfo r : details) {
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if ((StringUtils.isNotBlank(skuInfoBySku)) && !"[]".equals(skuInfoBySku)) {
				JSONArray parseArray = JSONObject.parseArray(skuInfoBySku);
				JSONObject object = (JSONObject) parseArray.get(0);
				String categoryName = object.getString("categoryName");
				String productName = object.getString("productName");
				r.setGoodName(productName);
				r.setGoodType(categoryName);
			}
			r.setCreateTime(new Date());
			r.setWaybillId(notice.getWaybillId().toUpperCase());
			r.setSourceId(notice.getSourceId().toUpperCase());
			receiveGoodDetailsMapper.insertSelective(r);
			plannedNumber.add(r.getPlannedQuantity());
		}


		/**-------------------- 添加到货通知单信息----------------------------------- */
		int suma =  plannedNumber.stream().mapToInt(Integer::intValue).sum();
		notice.setStatus("1");
        notice.setAffirmStatus(0);		
		notice.setCreater(customerUserDetailInfo.getName());
		notice.setCreaterId(Long.valueOf(customerUserDetailInfo.getCustomerId()));
		notice.setCustomer(customerInfoEntity.getCustomerCode());
		notice.setCustomerName(customerInfoEntity.getChineseName());
		notice.setPlannedQuantity(suma);
		notice.setCreateTime(new Date());
		notice.setSourceId(notice.getSourceId().toUpperCase());
		notice.setArrivalNoticeId(notice.getWarehouse() + setReceiveArrivalNoticeId());//设置流水号
		receiveArrivalNoticeMapper.insertSelective(notice);
		return true;
	}
	
	
	
	/**
	 * 修改入库订单信息
	 */
	public boolean updateReceiveArrivalNotice(ReceiveArrivalNotice notice) {
		CustomerUserDetailInfo customerUserDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
		List<ReceiveGoodDetailsInfo> details = notice.getReceiveGoodDetails();
		if(CollectionUtils.isEmpty(details))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"商品信息不能为空");
		/**----------------------------校验到货通知单信息是否为空----------------------------------- */
		if(StringUtils.isBlank(notice.getWaybillId())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"运单号不能为空");
		}
		if(receiveGoodDetailsMapper.checkWayBillIdAmountWhenUpdate(notice.getWaybillId(),notice.getSourceId())>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407,"运单号"+notice.getWaybillId()+"已存在,请检查后再试!");
		}
		if(StringUtils.isBlank(notice.getPlannedTime())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"预到仓时间不能为空");
		}
		
		/**-------------------- 校验商品详情信息是否为空及sku是否存在----------------------------------- */
		ArrayList<String> listForCheckSku = new ArrayList<>();
		details.stream().forEach(r->{
			if(StringUtils.isBlank(r.getSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"商品sku不能为空");
			}
			if(listForCheckSku.contains(r.getSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"商品详情信息中sku: "+r.getSku()+" 重复,不能入库 请检查后再试!");
			}
			if(r.getPlannedQuantity() == null || r.getPlannedQuantity() <= 0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"商品计划数量错误");
			}
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if(skuInfoBySku == null || "[]".equals(skuInfoBySku)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"客户端系统中无sku:"+r.getSku()+"的对应信息,不能入库 请检查");
			}
			listForCheckSku.add(r.getSku());
		});
		
		
		/**-------------------- 添加到货商品详情信息----------------------------------- */
		ArrayList<Integer> plannedNumber = new ArrayList<>();
		receiveGoodDetailsMapper.deleteUnconfirmedByWaybillId(notice.getWaybillId());
		details.forEach(r->{
			String skuInfoBySku = mUtils.getSkuInfoBySku(new String[]{r.getSku()});
			if(StringUtils.isNotBlank(skuInfoBySku) && !"[]".equals(skuInfoBySku)){
				JSONArray parseArray = JSONObject.parseArray(skuInfoBySku);
				JSONObject object = (JSONObject)parseArray.get(0);
				String categoryName = object.getString("categoryName");
				String productName = object.getString("productName");
				r.setGoodName(productName);
				r.setGoodType(categoryName);
			}
			r.setSourceId(notice.getSourceId());
			r.setWaybillId(notice.getWaybillId().toUpperCase());
			receiveGoodDetailsMapper.insertUpdateSelective(r);
			plannedNumber.add(r.getPlannedQuantity());
		});


		/**-------------------- 添加到货通知单信息----------------------------------- */
		int suma =  plannedNumber.stream().mapToInt(Integer::intValue).sum();
		notice.setPlannedQuantity(suma); 
		notice.setSourceId(notice.getSourceId().toUpperCase());
		notice.setLastUpdateBy(customerUserDetailInfo.getName());
		notice.setUpdateTime(new Date());
		receiveArrivalNoticeMapper.updateByPrimaryKeySelective(notice);
		return true;
	}
	
	


	/**
	 * 删除入库订单
	 */
	public void deleteOrders(List<Integer> list) {
		if(CollectionUtils.isEmpty(list)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"入库单号不能为空");
		}

		for (Integer id : list) {
			ReceiveArrivalNotice notice = receiveArrivalNoticeMapper.selectByPrimaryKey(id);
			if( 1 == notice.getAffirmStatus()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"入库单: "+notice.getSourceId()+"为已确认状态，不可删除!");
			}
		}
		receiveArrivalNoticeMapper.deleteOrders(list);
	}


	/**
	 * 客户端到货通知单信息查询
	 */
	public Page<ReceiveArrivalNotice> findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice) {
		String customerCode = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity().getCustomerCode();
		receiveArrivalNotice.setCustomer(customerCode);
		List<ReceiveArrivalNotice> findAllClient = receiveArrivalNoticeMapper.findAllClient(receiveArrivalNotice);
		for (ReceiveArrivalNotice receiveArrivalNotice2 : findAllClient) {
			String name = mUtils.selectWareHouseName(receiveArrivalNotice2.getWarehouse());
			receiveArrivalNotice2.setWarehouse(name);
			List<String> list = receiveGoodDetailsMapper.selectWayBillIdBySourceId(receiveArrivalNotice2.getSourceId());
			logger.info("来源单号：{}，对应的详情：{}", receiveArrivalNotice2.getSourceId(), list);
			receiveArrivalNotice2.setWaybillId(list.get(0));
		}
		PageInfo pageInfo = new PageInfo(findAllClient);
		return new Page(pageInfo);
	}



	/**
	 * 订单确认
	 */
	public void affirmOrder(List<String> list) {
		for (String str : list) {
			if(StringUtils.isBlank(str)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"入库单号不能为空");
			}
			//调用库存接口  添加入库库存信息
			ReceiveArrivalNotice selectNotice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(str);
			if(null == selectNotice){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"没有此入库单号的订单信息");
			} else if (0 != selectNotice.getAffirmStatus()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"订单已确认，请勿重复确认");
			}
			String businessType = "";
			switch (selectNotice.getSourceType()) {
			case "1":
				businessType = "来料入库";
				break;
			case "2":
				businessType = "销退入库";
				break;
			}
			List<ReceiveGoodDetails> info = receiveGoodDetailsMapper.selectInfoBySourceId(str);
			ArrayList<Map<String, Object>> listMap = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			for (ReceiveGoodDetails receiveGoodDetails : info) {
				map.put("warehouseCode", selectNotice.getWarehouse());
				map.put("sourceOrderNumber", selectNotice.getSourceId());
				map.put("updateBy", getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName());
				map.put("businessType", businessType);
				map.put("goodsSku", receiveGoodDetails.getSku());
				map.put("skuNumbers", receiveGoodDetails.getPlannedQuantity());
				listMap.add(map);
			}
			logger.info("[订单确认修改库存参数]"+ listMap.toString());
			String updateResult = Utils.getResultData(remoteCenterService.submitOrdersUpdateInventory(listMap), SystemConstants.nameType.SYS_CENTER,"更新客户端入库提交订单异常");
			logger.info("[订单确认修改库存返回结果]"+ updateResult);
			//修改到货通知单
			ReceiveArrivalNotice notice = new ReceiveArrivalNotice();
			notice.setAffirmTime(new Date());
			notice.setAffirmBy(getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName());
			notice.setAffirmStatus(1);
			notice.setSourceId(str);
			receiveArrivalNoticeMapper.updateSelective(notice);
		}
	}




	/**
	 * 生成到货流水号
	 * @return
	 */
	public String setReceiveArrivalNoticeId(){
		String format = DateFormatUtils.format(new Date(), "yyyyMMdd");
		String substring = format.substring(2, format.length());//时间字符串

		synchronized (this) {
			//查询当天时间内的最后一位流水号
			List<String> selectArrivalNoticeIdByDate = receiveArrivalNoticeMapper.selectArrivalNoticeIdByDate(substring);

			String SerialNumber = "";
			List<Integer> list = new ArrayList<>();
			if(!CollectionUtils.isEmpty(selectArrivalNoticeIdByDate)){
				selectArrivalNoticeIdByDate.stream().forEach(s->{
					String sub = s.substring(s.lastIndexOf(substring) + 6, s.length());
					Integer valueOf = Integer.valueOf(sub);
					list.add(valueOf);
				});
				Integer max = Collections.max(list);
				int newParseInt = 10000 + max + 1;
				SerialNumber = subStr(""+ newParseInt,1);
			}else{
				SerialNumber = "0001";
			}
			return substring + SerialNumber;
		}
	}
	
	
	/**
	 * 生成来源单号流水号
	 * @return
	 */
	public String setSourceId(){
		String format = DateFormatUtils.format(new Date(), "yyyyMMdd");
		String substring = format.substring(2, format.length());//时间字符串
		
		synchronized (this) {
			//查询当天时间内的最后一位流水号
			List<String> selectArrivalNoticeIdByDate = receiveArrivalNoticeMapper.selectSourceIdByDate(substring);
			
			String SerialNumber = "";
			List<Integer> list = new ArrayList<>();
			if(!CollectionUtils.isEmpty(selectArrivalNoticeIdByDate)){
				selectArrivalNoticeIdByDate.stream().forEach(s->{
					String sub = s.substring(s.lastIndexOf(substring) + 6, s.length());
					Integer valueOf = Integer.valueOf(sub);
					list.add(valueOf);
				});
				Integer max = Collections.max(list);
				int newParseInt = 10000 + max + 1;
				SerialNumber = subStr(""+ newParseInt,1);
			}else{
				SerialNumber = "0001";
			}
			return substring + SerialNumber;
		}
	}
	

	public String subStr(String str, int start) {
        if (str == null || str.equals("") || str.length() == 0)
            return "";
        if (start < str.length()) {
            return str.substring(start);
        } else {
            return "";
        }
	}





    @Override
    public String orderImport(MultipartFile file, String flag) {
		CustomerUserDetailInfo customerUserDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
		CustomerInfoEntity customerInfoEntity = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity();
        String originalFilename = file.getOriginalFilename();
        //按行获取数据
        List<ArrayList<String>> arrayLists;
        if (StringUtils.isEmpty(originalFilename)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "文件不可为空");
        } else if (originalFilename.endsWith("xlsx")) {
            arrayLists = SimpleExcelUtil.readXlsx(file);//Excel 2007
        } else if (originalFilename.endsWith("xls")) {
            arrayLists = SimpleExcelUtil.readXls(file);//Excel 2003throw
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "请上传标准Excel文件");
        }
        if (CollectionUtils.isEmpty(arrayLists)) {
        	logger.error("读取失败，读取文件信息为：{}", arrayLists);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "上传失败请重试！！");
        }

        //定义详情属性及失败数据集合
        List<String> boxIdList = new ArrayList<>();
        List<String> skuList = new ArrayList<>();
        List<Integer> deliverNumList = new ArrayList<>();
        List<String> boxIdNulList = new ArrayList<>();
        List<String> fatherFailList = new ArrayList<>();
        Map<Integer, List<String>> failDetailMap = new HashMap<>();
		List<String> warehouseList  = Arrays.asList(getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity().getWarehouseName().split(","));
		//校验主表数据 且 需要仓库是否属于当前用户（暂未处理）
        ArrayList<String> fatherList = arrayLists.get(1);
        String warehouseName = fatherList.get(1);
        if (!warehouseList.contains(warehouseName)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "上传失败，此账号："+customerUserDetailInfo.getAccount()+"，无该仓库权限："+warehouseName);
		}
        String qcType = fatherList.get(2);
        String qcTypeNum;
        switch (qcType) {
            case "全检":
                qcTypeNum = "1";
                break;
            case "抽检":
                qcTypeNum = "2";
                break;
            case "免检":
                qcTypeNum = "3";
                break;
            default:
                qcTypeNum = null;
                break;
        }
        String isUnload = null;
        Integer isUnloadNum = null;
        String waybillNum;
        String advanceTime;
        if ("2".equals(flag)){
            waybillNum = fatherList.get(3);
            advanceTime = fatherList.get(4);
        } else {
            isUnload = fatherList.get(3);
            switch (isUnload) {
                case "是":
                    isUnloadNum = 0;
                    break;
                case "否":
                    isUnloadNum = 1;
                    break;
                default:
                    isUnloadNum = null;
                    break;
            }
             waybillNum = fatherList.get(4);
             advanceTime = fatherList.get(5);
        }


        //判断主表字段是否有值
        if (StringUtils.isBlank(warehouseName)) {
            fatherFailList.add("送货仓库不能为空");
            failDetailMap.put(1, fatherFailList);
        }
        if (StringUtils.isBlank(qcType)) {
            fatherFailList.add("质检方式不能为空");
            if (failDetailMap.size() == 0) {
                failDetailMap.put(1, fatherFailList);
            } else {
                failDetailMap.get(1).add("质检方式不能为空");
            }
        }
        if ("1".equals(flag)){
            if (StringUtils.isBlank(isUnload)) {
                fatherFailList.add("是否卸货不能为空");
                if (failDetailMap.size() == 0) {
                    failDetailMap.put(1, fatherFailList);
                } else {
                    failDetailMap.get(1).add("是否卸货不能为空");
                }
            }
        }

        if (StringUtils.isBlank(waybillNum)) {
            fatherFailList.add("运单号不能为空");
            if (failDetailMap.size() == 0) {
                failDetailMap.put(1, fatherFailList);
            } else {
                failDetailMap.get(1).add("运单号不能为空");
            }
        }
        if (StringUtils.isBlank(advanceTime)) {
            fatherFailList.add("预到仓不能为空");
            if (failDetailMap.size() == 0) {
                failDetailMap.put(1, fatherFailList);
            } else {
                failDetailMap.get(1).add("预到仓不能为空");
            }
        }
        String warehouseCode = Utils.getResultData(remoteCenterService.getWarehouseByName(warehouseName), SystemConstants.nameType.SYS_CENTER, "系统操作异常");
        if (StringUtils.isBlank(warehouseCode)){
            fatherFailList.add("送货仓库不存在，请维护");
            if (CollectionUtils.isEmpty(failDetailMap.get(1))){
                failDetailMap.put(1, fatherFailList);
            } else {
                failDetailMap.get(1).add("送货仓库不存在，请维护");
            }
        }
        if (failDetailMap.size() != 0) {
            return failDetailMap.toString();
        }

        //校验详情表数据
        Set<String> skuSet = new HashSet<>();
        for (int i = 3; i < arrayLists.size(); i++) {
            List<String> failDetailList = new ArrayList<>();
            if (StringUtils.isNotBlank(arrayLists.get(i).get(1))) {
                boxIdList.add(arrayLists.get(i).get(1));
            } else {
                boxIdNulList.add(arrayLists.get(i).get(1));
            }
            if (StringUtils.isNotBlank(arrayLists.get(i).get(2))) {
				String sku;
            	if ("2".equals(flag)){
					sku = arrayLists.get(i).get(1).trim();
					skuList.add(sku);
					skuSet.add(sku);
				} else {
					sku = arrayLists.get(i).get(2).trim();
					skuList.add(sku);
					skuSet.add(sku);
				}

            } else {
                failDetailList.add("sku不能为空");
                failDetailMap.put(i, failDetailList);
            }
            if ("1".equals(flag)){
                if (StringUtils.isNotBlank(arrayLists.get(i).get(3))) {
                    deliverNumList.add(Integer.valueOf(arrayLists.get(i).get(3)));
                } else {
                    if (CollectionUtils.isEmpty(failDetailMap.get(i))) {
                        failDetailMap.put(i, failDetailList);
                    } else {
                        failDetailMap.get(i).add("送货数量不能为空");
                    }
                }
            } else {
				if (StringUtils.isNotBlank(arrayLists.get(i).get(2))) {
					deliverNumList.add(Integer.valueOf(arrayLists.get(i).get(2)));
				} else {
					if (CollectionUtils.isEmpty(failDetailMap.get(i))) {
						failDetailMap.put(i, failDetailList);
					} else {
						failDetailMap.get(i).add("送货数量不能为空");
					}
				}

			}
        }
        if (failDetailMap.size() != 0) {
            return failDetailMap.toString();
        }

        JSONArray jsonArray = Utils.resultParseArray(remoteCenterService.getSkuInfoBySku(skuSet.toArray(new String[skuSet.size()])), "基础服务", "系统操作异常");

        //处理实际的详情数据
        int dataNum = arrayLists.size() - 3;
        if (boxIdList.size() == dataNum) {
            for (int i = 0; i < boxIdList.size(); i++) {
                List<String> failDetailList = new ArrayList<>();
                String sku = skuList.get(i).trim();
                JSONObject jsonObject = null;
                boolean flag1 = false;
                for (Object obj : jsonArray) {
                    jsonObject = (JSONObject) obj;
                    if (StringUtils.equalsIgnoreCase(sku, jsonObject.getString("productSku"))) {
                        flag1 = true;
                        break;
                    }
                }
                if (flag1) {
					Integer waybillSum = receiveGoodDetailsMapper.selectCountByWaybillId(waybillNum);
					String sourceId = "RK"+setSourceId();
                    ReceiveArrivalNotice receiveArrivalNotice = new ReceiveArrivalNotice();
                    Integer deliverNum = deliverNumList.get(i);
                    receiveArrivalNotice.setArrivalNoticeId(warehouseCode + setReceiveArrivalNoticeId());
                    receiveArrivalNotice.setSourceId(waybillNum);
					receiveArrivalNotice.setCustomer(customerInfoEntity.getCustomerCode());
					receiveArrivalNotice.setCustomerName(customerUserDetailInfo.getName());
					receiveArrivalNotice.setSourceType(String.valueOf(1));
                    receiveArrivalNotice.setWarehouse(warehouseCode);
                    receiveArrivalNotice.setQualityType(qcTypeNum);
                    receiveArrivalNotice.setStatus("1");
					receiveArrivalNotice.setPlannedQuantity(waybillSum+deliverNum);
                    receiveArrivalNotice.setIsUnload(isUnloadNum);
                    receiveArrivalNotice.setWaybillId(waybillNum);
                    receiveArrivalNotice.setPlannedTime(setDate(advanceTime));
					receiveArrivalNotice.setCreater(customerUserDetailInfo.getName());
					receiveArrivalNotice.setCreaterId(Long.valueOf(customerInfoEntity.getCustomerAppId()));
					receiveArrivalNotice.setCreateTime(new Date());
					if ("2".equals(flag)){
						receiveArrivalNotice.setIsUnload(0);
					} else if ("1".equals(flag)) {
						receiveArrivalNotice.setIsUnload(StringUtils.equals(arrayLists.get(2).get(3), "是") ? 1:0);
					}
					receiveArrivalNotice.setAffirmStatus(0);
					try {
						receiveArrivalNoticeMapper.insertSelective(receiveArrivalNotice);
						ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
						receiveGoodDetails.setSourceId(waybillNum);
						receiveGoodDetails.setBoxId(boxIdList.get(i));
						receiveGoodDetails.setWaybillId(waybillNum);
						receiveGoodDetails.setSku(sku);
						receiveGoodDetails.setGoodName(jsonObject.getString("productName"));
						receiveGoodDetails.setGoodType(jsonObject.getString("categoryName"));
						receiveGoodDetails.setPlannedQuantity(deliverNum);
						receiveGoodDetails.setExceptionMark(0);
						receiveGoodDetailsMapper.insertSelective(receiveGoodDetails);
					} catch (DuplicateKeyException e) {
						failDetailList.add("运单号：" + waybillNum + "存在已存在，请检查");
						failDetailMap.put(i + 5, failDetailList);
					}
                } else {
                	if (!sku.matches("^[\\w,-]+$")){
						failDetailList.add("商品sku只能包含字母，数字，中划线和下划线，请检查");
						failDetailMap.put(i + 5, failDetailList);
					} else {
						failDetailList.add("商品sku不存在");
						failDetailMap.put(i + 5, failDetailList);
					}
                }

            }
        } else if (boxIdNulList.size() == 0) {
            for (int i = 0; i < skuList.size(); i++) {
                List<String> failDetailList = new ArrayList<>();
                String sku = skuList.get(i).trim();
                JSONObject jsonObject = null;
                boolean flag2 = false;
                for (Object obj : jsonArray) {
                    jsonObject = (JSONObject) obj;
                    if (StringUtils.equalsIgnoreCase(sku, jsonObject.getString("productSku"))) {
                        flag2 = true;
                        break;
                    }
                }
                if (flag2 || null != jsonObject) {
					Integer waybillSum = receiveGoodDetailsMapper.selectCountByWaybillId(waybillNum);
					String sourceId = "XT" + setSourceId();
                    ReceiveArrivalNotice receiveArrivalNotice = new ReceiveArrivalNotice();
                    Integer deliverNum = deliverNumList.get(i);
                    receiveArrivalNotice.setArrivalNoticeId(warehouseCode + setReceiveArrivalNoticeId());
                    receiveArrivalNotice.setSourceId(sourceId);
					receiveArrivalNotice.setCustomer(customerInfoEntity.getCustomerCode());
					receiveArrivalNotice.setCustomerName(customerUserDetailInfo.getName());
					receiveArrivalNotice.setSourceType(String.valueOf(2));
                    receiveArrivalNotice.setWarehouse(warehouseCode);
                    receiveArrivalNotice.setQualityType(qcTypeNum);
                    receiveArrivalNotice.setStatus("1");
					receiveArrivalNotice.setPlannedQuantity(waybillSum+deliverNum);
                    receiveArrivalNotice.setIsUnload(isUnloadNum);
                    receiveArrivalNotice.setWaybillId(waybillNum);
                    receiveArrivalNotice.setPlannedTime(setDate(advanceTime));
					receiveArrivalNotice.setCreater(customerUserDetailInfo.getName());
					receiveArrivalNotice.setCreaterId(Long.valueOf(customerInfoEntity.getCustomerAppId()));
					receiveArrivalNotice.setCreateTime(new Date());
					receiveArrivalNotice.setIsUnload(StringUtils.equals(arrayLists.get(2).get(3), "是") ? 1:0);
					receiveArrivalNotice.setAffirmStatus(0);
                    try {
						receiveArrivalNoticeMapper.insertSelective(receiveArrivalNotice);
						ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
						receiveGoodDetails.setSourceId(sourceId);
						receiveGoodDetails.setWaybillId(waybillNum);
						receiveGoodDetails.setSku(sku);
						receiveGoodDetails.setGoodName(jsonObject.getString("productName"));
						receiveGoodDetails.setGoodType(jsonObject.getString("categoryName"));
						receiveGoodDetails.setPlannedQuantity(deliverNum);
						receiveGoodDetails.setExceptionMark(0);
						receiveGoodDetailsMapper.insertSelective(receiveGoodDetails);
					} catch (DuplicateKeyException e) {
						failDetailList.add("运单号：" + waybillNum + "存在已存在，请检查");
						failDetailMap.put(i + 5, failDetailList);
					}
                } else {
					if (!sku.matches("^[\\w,-]+$")){
						failDetailList.add("商品sku只能包含字母，数字，中划线和下划线，请检查");
						failDetailMap.put(i + 5, failDetailList);
					} else {
						failDetailList.add("商品sku不存在");
						failDetailMap.put(i + 5, failDetailList);
					}
                }
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "上传失败，箱号字段要么全部有参数，要么全部无参数，请检查！！");
        }
        return failDetailMap.toString();
    }

    private String setDate(String ben) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //将从单元格中获取到的string类型的数字转化为double类型
        Double d = Double.parseDouble(ben);
        //将double类型在通过HSSFDateUtil类将其转化为Date类型
        Date date = HSSFDateUtil.getJavaDate(d);
        return sdf.format(date);
    }

    @Override
    public String orderExport(String sourceId, HttpServletResponse response) {
        ReceiveArrivalNotice arrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(sourceId);
        List<ReceiveGoodDetails> receiveGoodDetailsList = receiveGoodDetailsMapper.selectInfoBySourceId(sourceId);
        //定义表头
        String[] fatherHeader = new String[]{"入库单号", "送货仓库", "质检方式", "送货总数量", "运单号", "预到仓时间", "订单状态", "新添人", "新添时间", "最后修改人",
                "修改时间", "订单确认人", "订单确定时间"};
        String[] childrenHeader = new String[]{"箱号", "SKU", "送货数量", "实收数量"};
        //定义标题
        String bigTitle = "入库订单";

        //创建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建工作表sheet
        HSSFSheet sheet = workbook.createSheet();

        //合并格字体及合并类型
        HSSFFont mergeFont = workbook.createFont();
        mergeFont.setFontName("仿宋_GB2312");
        mergeFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        mergeFont.setFontHeightInPoints((short) 11);
        mergeFont.setBold(true);
        HSSFCellStyle mergeStyle = workbook.createCellStyle();
        mergeStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergeStyle.setAlignment(HorizontalAlignment.CENTER);
        mergeStyle.setFont(mergeFont);

        //设置标题字体及设置标题样式
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontName("仿宋_GB2312");
        titleFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFont(titleFont);

        //设置表头字体及设置表头样式
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("仿宋_GB2312");
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(headerFont);

        //设置内容字体及设置内容样式
        HSSFFont dataFont = workbook.createFont();
        dataFont.setFontName("仿宋_GB2312");
        dataFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        dataFont.setFontHeightInPoints((short) 11);
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setFont(dataFont);

        //创建标题
        HSSFRow bigRow = sheet.createRow(0);
        HSSFCell titleCell = bigRow.createCell(0);
        //将标题样式设置进去
        titleCell.setCellStyle(titleStyle);
        //合并单元格居中(start row, last row, start column, last column)
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fatherHeader.length));
        //创建标题第一行
        titleCell.setCellValue(bigTitle);

        //分别合并第2行和第3行的第一个单元格；第4行和第5行的第一个单元格
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 0, 0));

        //创建合并格表头
        HSSFRow fatherRow = sheet.createRow(1);
        HSSFCell fatherCell = fatherRow.createCell(0);
        sheet.setColumnWidth(0, 5000);
        fatherCell.setCellStyle(mergeStyle);
        fatherCell.setCellValue("到货通知单");
        HSSFCell cell = null;
        for (int i = 1; i < fatherHeader.length; i++) {
            if (6 == i || 9 == i) {
                sheet.setColumnWidth(i, 6000);
            } else {
                sheet.setColumnWidth(i, 5000);
            }
            cell = fatherRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(fatherHeader[i - 1]);
        }

        //创建主表行数据
        HSSFRow fatherDataRow = sheet.createRow(2);
        Object[] fatherObj = new Object[fatherHeader.length];
        JSONObject jsonObject = Utils.resultParseObj(remoteCenterService.getWarehouseByCode(arrivalNotice.getWarehouse()), "中心服务", "操作异常");
        if (null == jsonObject){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "来源单号："+sourceId+",仓库名称不存在，请检查");
		}
        String qcTypeName;
        switch (arrivalNotice.getQualityType()) {
            case "1":
                qcTypeName = "全检";
                break;
            case "2":
                qcTypeName = "抽检";
                break;
            case "3":
                qcTypeName = "免检";
                break;
            default:
                qcTypeName = null;
                break;
        }
        String statusName;
        switch (arrivalNotice.getStatus()) {
            case "1":
                statusName = "待收货";
                break;
            case "2":
                statusName = "收货中";
                break;
            case "3":
                statusName = "部分收货";
                break;
            case "4":
                statusName = "收货完成";
                break;
            default:
                statusName = null;
                break;
        }

        fatherObj[0] = arrivalNotice.getArrivalNoticeId();
        fatherObj[1] = jsonObject.getString("warehouseName");
        fatherObj[2] = qcTypeName;
        fatherObj[3] = arrivalNotice.getPlannedQuantity();
        fatherObj[4] = receiveGoodDetailsList.get(0).getWaybillId();
        fatherObj[5] = arrivalNotice.getPlannedTime();
        fatherObj[6] = statusName;
        fatherObj[7] = arrivalNotice.getCreater();
        fatherObj[8] = arrivalNotice.getCreateTime();
        fatherObj[9] = arrivalNotice.getLastUpdateBy();
        fatherObj[10] = arrivalNotice.getUpdateTime();
        fatherObj[11] = StringUtils.isBlank(arrivalNotice.getAffirmBy())? "" : arrivalNotice.getAffirmBy();
        fatherObj[12] = arrivalNotice.getAffirmTime() == null ? "" :  arrivalNotice.getAffirmTime();
        for (int j = 1; j <= fatherHeader.length; j++) {
            HSSFCell fatherDataCell = fatherDataRow.createCell(j);
            fatherDataCell.setCellStyle(dataStyle);
            fatherDataCell.setCellValue(String.valueOf(fatherObj[j - 1]));
        }

        //创建第二行表头及设置表头
        HSSFRow childrenRow = sheet.createRow(3);
        HSSFCell childrenCell = childrenRow.createCell(0);
        childrenCell.setCellStyle(mergeStyle);
        childrenCell.setCellValue("到货详情");
        HSSFCell cell2 = null;
        for (int i = 1; i < childrenHeader.length + 1; i++) {
            cell2 = childrenRow.createCell(i);
            cell2.setCellStyle(headerStyle);
            cell2.setCellValue(childrenHeader[i - 1]);
        }

        //创建子表数据
        Object[] objs;
        for (int i = 4; i < receiveGoodDetailsList.size() + 4; i++) {
            HSSFRow childrenDataRow = sheet.createRow(i);
            ReceiveGoodDetails receiveGoodDetail = receiveGoodDetailsList.get(i - 4);
            objs = new Object[childrenHeader.length];
            objs[0] = StringUtils.isBlank(receiveGoodDetail.getBoxId())? "": receiveGoodDetail.getBoxId();
            objs[1] = receiveGoodDetail.getSku();
            objs[2] = receiveGoodDetail.getPlannedQuantity();
            objs[3] = receiveGoodDetail.getDeliveryQuantity();
            for (int j = 1; j < childrenHeader.length + 1; j++) {
                HSSFCell childrenDataCell = childrenDataRow.createCell(j);
                childrenDataCell.setCellStyle(dataStyle);
                childrenDataCell.setCellValue(String.valueOf(objs[j - 1]));
            }
        }

        try {
            ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("到货详情导出", "UTF-8"));
            workbook.write(response.getOutputStream());
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @Override
    public void downloadTemplate(String flag, HttpServletRequest request, HttpServletResponse response) {
		String warehouseName = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity().getWarehouseName();
		String[] warehouseNameArray = warehouseName.split(",");
        InputStream fis = null;
		ServletOutputStream out = null;
        try {
            String fileName;
            if ("2".equals(flag)){
				fileName = "销退订单导入模板";
                fis = new ClassPathResource("excel/SellbackTemplate.xlsx").getInputStream();
            } else {
				fileName = "入库订单导入模板";
                fis = new ClassPathResource("excel/ReceiveTemplate.xlsx").getInputStream();
            }
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet fromsheet = workbook.getSheetAt(0);
            SimpleExcelUtil.setValidationData(fromsheet, 2, 2, 1, 1, warehouseNameArray);
			out = response.getOutputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workbook.write(os);
			byte[] bytes = os.toByteArray();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".xlsx", "UTF-8"));
			workbook.write(response.getOutputStream());
			out.write(bytes);
			out.flush();
		} catch (Exception e) {
            logger.error("下载模板失败,失败原因：{}",e.getMessage());
        }finally{
            try {
                if(fis != null){
                    fis.close();
                }
				if(out != null) {
					out.close();
				}

			} catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}