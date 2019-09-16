package com.brandslink.cloud.inbound.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.jsoup.helper.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.service.InboundClientOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.brandslink.cloud.common.annotation.OpenAPI;
import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsDto;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.entity.SellingBack;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.InboundClientOrderService;
import com.brandslink.cloud.inbound.service.ReceiveArrivalNoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.IntStream;

/**
 * @author zc
 * @version 1.0
 * @description: 入库客户端基本接口
 * @date 2019/8/27 10:33
 */
@Api(tags = "入库单客户端基本接口")
@RestController
@RequestMapping("/client")
public class InboundClientOrderController extends CommonController {

    @Resource
    private InboundClientOrderService inboundClientOrderService;
    @Resource
	private ReceiveArrivalNoticeService receiveArrivalNoticeService;


    /**
	 * 添加入库订单
	 * @param notice
	 */
	@ApiOperation(value = "添加入库订单", notes = "")
	@PostMapping("/insertNoticeOrder")
	public Object insertNoticeOrder(@RequestBody ReceiveArrivalNotice notice){
		try {
			inboundClientOrderService.insertReceiveArrivalNotice(notice);
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408,"箱号与sku重复，请检查！");
		}
		return "订单添加成功";
	}
	
	
	/**
	 * 修改入库订单
	 * @param notice
	 */
	@ApiOperation(value = "修改入库订单", notes = "")
	@PostMapping("/updateNoticeOrder")
	public Object updateNoticeOrder(@RequestBody ReceiveArrivalNotice notice){
		inboundClientOrderService.updateReceiveArrivalNotice(notice);
		return "订单修改成功";
	}
    
	
	
    
    @ApiOperation(value = "获取入库流水号",notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "type", value = "入库:RK  销退:XT", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping("/getReceiveArrivalNoticeId")
    public String getReceiveArrivalNoticeId(String type){
    	return  type + inboundClientOrderService.setSourceId();
    }


    /**
	 * 查询收货商品详情信息
	 * @return
	 */
	@ApiOperation(value = "查询收货商品详情信息", notes = "")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "sourceId", value = "入库单号", required = true, dataType = "string", paramType = "query")
    })
	@GetMapping("/selectGoodDetails")
	public ReceiveGoodDetailsDto selectGoodDetails(String sourceId){
		if(StringUtil.isBlank(sourceId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"入库单号不能为空");
		}
		ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
		receiveGoodDetails.setSourceId(sourceId);
		return receiveArrivalNoticeService.selectGoodDetailsSelective(receiveGoodDetails);
	}


    /**
     * 删除订单
     * @param list
     */
	@ApiOperation(value = "删除订单 list中为入库单信息的id值", notes = "")
	@PostMapping("/deleteOrders")
	public void deleteOrders(@RequestBody List<Integer> list) {
		inboundClientOrderService.deleteOrders(list);
	}
	
	/**
	 * 确认订单
	 * @param list
	 */
	@ApiOperation(value = "确认订单 list中为入库单信息的入库单号", notes = "")
	@PostMapping("/affirmOrder")
	public void affirmOrder(@RequestBody List<String> list) {
		inboundClientOrderService.affirmOrder(list);
	}

	

	@ApiOperation(value = "客户端列表查询", notes = "")
	@GetMapping("/findAllClient")
	@RequestRequire(require="pageNum,pageSize",parameter=String.class)
	public Page<ReceiveArrivalNotice> findAllClient(ReceiveArrivalNoticeInfo receiveArrivalNotice,String pageNum,String pageSize){
		Page.builder(pageNum, pageSize);
		return inboundClientOrderService.findAll(receiveArrivalNotice);
	}

	
	@ApiOperation(value = "导入",notes = "导入入库订单，flag，入库导入：1;销退导入:2")
	@PostMapping("/import")
	public String orderImport(@RequestParam("file") MultipartFile file, @RequestParam("flag") String flag) {
		return inboundClientOrderService.orderImport(file, flag);
	}
	
	
    @ApiOperation(value = "导出",notes = "导出入库订单")
    @GetMapping("/export")
    public String orderExport( HttpServletResponse response, String sourceId) {
        return inboundClientOrderService.orderExport(sourceId, response);
    }

    
    @ApiOperation(value = "下载导入模板",notes = "下载导入模板，flag，入库导入：1;销退导入:2")
    @GetMapping("/download")
    public void downloadTemplate(@RequestParam("flag") String flag, HttpServletRequest request, HttpServletResponse response){
		inboundClientOrderService.downloadTemplate(flag, request, response);
    }

}
