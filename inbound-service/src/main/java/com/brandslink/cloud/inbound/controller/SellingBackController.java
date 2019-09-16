package com.brandslink.cloud.inbound.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.inbound.dto.SellingBackDto;
import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.remote.RemoteOutBoundService;
import com.brandslink.cloud.inbound.service.SellingBackService;
import com.brandslink.cloud.inbound.utils.EasyPoiUtil;
import com.brandslink.cloud.inbound.utils.SimpleExcelUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "销退收货单接口")
@RestController
@RequestMapping("/sellingBack")
public class SellingBackController extends CommonController{
	
	@Resource
	private SellingBackService sellingBackService;
	
	@Resource
	private RemoteOutBoundService remoteOutBoundService;
	
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	
	
	private final Logger logger = LoggerFactory.getLogger(SellingBackController.class);
	
	
	@ApiOperation(value = "查询详情信息", notes = "")
	@GetMapping("/selectDetailInfo")
	public SellingBackDto selectDetailInfo(String wayBillId){
		return sellingBackService.selectDetailInfo(wayBillId);
	}
	
	@ApiOperation(value = "收货查询数量展示", notes = "")
	@GetMapping("/quantityStatistics")
	public List<Map<String,Object>> quantityStatistics(@RequestParam("sellingBackDetail") List<String> sellingBackId){
		return sellingBackService.quantityStatistics(sellingBackId);
	}
	
	
	@ApiOperation(value = "到货完成", notes = "")
	@PostMapping("/sellingBackSuccess")
	public void sellingBackSuccess(@RequestBody List<SellingBack> sellingBack){
		sellingBackService.sellingBackSuccess(sellingBack);;
	}
	
	
	
	
	@ApiOperation(value = "查询列表信息", notes = "")
	@GetMapping("/sellingBackFindAll")
	@RequestRequire(require="pageNum,pageSize",parameter=String.class)
	public Page<SellingBack> findAll(SellingBack sellingBack,String pageNum,String pageSize){
		Page.builder(pageNum, pageSize);
		Page<SellingBack> findAll = sellingBackService.findAll(sellingBack);
		return findAll;
	}
	
	
	@ApiOperation(value = "销退接收", notes = "")
	@PostMapping("/sellingBackInsert")
	public void sellingBackInsert(SellingBack sellingBack){
		sellingBackService.insertInfo(sellingBack);
	}
	
	
	@ApiOperation(value="导入销退Excel",notes="")
	@PostMapping("/insertExcelInfo")
	public Object insertExcelInfo(@RequestParam("file") MultipartFile file){
			List<ArrayList<String>> readResult = null;//总行记录

			//判断文件是否为空
			if ((file).isEmpty()) {
				return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"上传文件为空");
			}
			//判断文件大小
			long size = file.getSize();
			String name = file.getOriginalFilename();
			if (StringUtils.isBlank(name) || size == 0) {
				return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"文件名为空");
			}
			//获取文件后缀
			String postfix = SimpleExcelUtil.getPostfix(name);
			
			List<String> typeList = new ArrayList<String>();
			typeList.add("客户退货");
			typeList.add("超时退回");
			typeList.add("机场安检不过");
			typeList.add("其它");
			
			List<ArrayList<String>> readXls = SimpleExcelUtil.readXls(file);
			
			for (ArrayList<String> list : readXls) {
				String wayBillId = list.get(0);
				if(receiveGoodDetailsMapper.checkWayBillIdAmount(wayBillId)<=0 
				   && Utils.returnRemoteResultDataString(remoteOutBoundService.findOrderDetailsByOrderNum(wayBillId), "调用出库异常")==null) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号:"+wayBillId+" 没有单据记录,不可销退接收,请检查！");
				}
				
				String type = list.get(1);
				if(!typeList.contains(type)) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号:"+wayBillId+"退货类型填写不正确 请检查！");
				}
				
				String status = list.get(2);
				if(!status.equals("待处理")) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"运单号:"+wayBillId+"退货状态填写不正确 请检查！");
				}
				
				SellingBack sellingBack = new SellingBack();
				sellingBack.setWaybillId(wayBillId);
				String sellingBackType = "";
				switch (type) {
				    case "客户退货":
				    	sellingBackType = "01";
					    break;
				    case "超时退回":
				    	sellingBackType = "02";
				    	break;
				    case "机场安检不过":
				    	sellingBackType = "03";
				    	break;
				    case "其它":
				    	sellingBackType = "04";
				    	break;
				}
				sellingBack.setSellingBackType(sellingBackType);
				sellingBackService.insertInfo(sellingBack);
			}
			return  new GlobalException(ResponseCodeEnum.RETURN_CODE_100200,"销退信息导入成功");
	}
	
	
	@ApiOperation(value = "销退报表导出", notes = "")
	@GetMapping("/excelExport")
	public void excelExport(String ids ,HttpServletResponse response){
		String[] split = ids.split(",");
		List<String> list = Arrays.asList(split);
		List<SellingBack> excelExport = sellingBackService.excelExport(list);
		try {
			EasyPoiUtil.exportExcel(excelExport,"销退报表","销退报表",SellingBack.class,"销退报表.xls",response);
		} catch (Exception e) {
			logger.error("报表导出异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"报表导出异常");
		}
	}
	
	
	
	
	
	
}
