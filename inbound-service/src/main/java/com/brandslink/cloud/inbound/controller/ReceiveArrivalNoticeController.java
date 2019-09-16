package com.brandslink.cloud.inbound.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.annotation.OpenAPI;
import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.dto.ReceiveArrivalNoticeInfo;
import com.brandslink.cloud.inbound.dto.ReceiveGoodDetailsDto;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.remote.RemoteUserService;
import com.brandslink.cloud.inbound.service.QualityControlService;
import com.brandslink.cloud.inbound.service.ReceiveArrivalNoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "到货通知接口")
@RestController
@RequestMapping("/receiveArrivalNotice")
public class ReceiveArrivalNoticeController {

	@Resource
	private ReceiveArrivalNoticeService receiveArrivalNoticeService;
	@Resource
	private QualityControlService qualityControlService;
	
	private final Logger logger = LoggerFactory.getLogger(ReceiveArrivalNoticeController.class);
	
	
	
	/**-----------------------------------------到货通知接口--------------------------------------------------------*/
	
	/**
	 * 添加到货商品信息
	 * @param receiveArrivalNotice
	 */
	@ApiOperation(value = "添加到货商品信息", notes = "")
	@PostMapping("/insertNoticeInfo")
	@OpenAPI
	public Object insertNoticeInfo(ReceiveArrivalNotice receiveArrivalNotice){
		receiveArrivalNoticeService.insertReceiveArrivalNotice(receiveArrivalNotice);
		return "云仓入库订单写入成功";
	}
	
	
	
	/**
	 * 查询入库订单信息
	 * @param receiveArrivalNotice
	 */
	@ApiOperation(value = "查询入库订单信息", notes = "")
	@GetMapping("/selectOrderInfo")
	@OpenAPI
	public Object selectOrderInfo(@RequestParam("sourceId")String sourceId){
		if(StringUtils.isBlank(sourceId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200403,"来源单号不能为空");
		}
		ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
		receiveGoodDetails.setSourceId(sourceId);
		receiveGoodDetails.setType("open");
		ReceiveGoodDetailsDto selectGoodDetailsSelective = receiveArrivalNoticeService.selectGoodDetailsSelective(receiveGoodDetails);
		return selectGoodDetailsSelective;
	}
	
	
	
	
	/**
	 * 到货通知单信息查询
	 * @param receiveArrivalNotice
	 * @param page
	 * @param row
	 * @return
	 */
	@ApiOperation(value = "查询到货通知单信息", notes = "")
	@GetMapping("/findAll")	
	@RequestRequire(require="page,row",parameter=String.class)
	public Object findAll(ReceiveArrivalNoticeInfo receiveArrivalNotice,String page,String row){
//		Page.builder(page, row);
		if(StringUtil.isBlank(receiveArrivalNotice.getWayBillId())){
			if(!StringUtil.isBlank(receiveArrivalNotice.getSourceId())){
				receiveArrivalNotice.setSourceId(receiveArrivalNotice.getSourceId().toUpperCase());
			}
			return receiveArrivalNoticeService.findAll(receiveArrivalNotice,page,row);
		}else{
			String wayBillId = receiveArrivalNotice.getWayBillId().toUpperCase();
			return receiveArrivalNoticeService.selectArrivalNoticeByWaybillDim(wayBillId,page,row);
		}
	}
	
	
	/**
	 * 到货完成
	 * @param receiveArrivalNotice
	 * @return
	 */
	@ApiOperation(value = "到货完成", notes = "")
	@PostMapping("/ReceiveArrivalSuccess")
	public void ReceiveArrivalSuccess(@RequestBody List<ReceiveArrivalNotice> receiveArrivalNotice) {
		if(receiveArrivalNotice == null || receiveArrivalNotice.size() == 0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"参数列表不能为空");
		}
		receiveArrivalNoticeService.updateSelective(receiveArrivalNotice);
	}
	
	
	
	/*-----------------------------------------到货商品详情接口--------------------------------------------------------*/
	
	/**
	 * 关联查询收货商品详情信息
	 * @param receiveGoodDetails
	 * @return
	 */
	@ApiOperation(value = "查询收货商品详情信息", notes = "")
	@GetMapping("/selectGoodDetails")
	public ReceiveGoodDetailsDto selectGoodDetails(String sourceId){
		if(StringUtil.isBlank(sourceId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"来源单号不能为空");
		}
			ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
			receiveGoodDetails.setSourceId(sourceId);
			return receiveArrivalNoticeService.selectGoodDetailsSelective(receiveGoodDetails);
	}
	
}
