package com.brandslink.cloud.inbound.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveCardBoardCreate;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.mapper.ReceiveArrivalNoticeMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveCardBoardCreateMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveGoodDetailsMapper;
import com.brandslink.cloud.inbound.mapper.ReceiveSignInMapper;
import com.brandslink.cloud.inbound.service.ReceiveArrivalNoticeService;
import com.brandslink.cloud.inbound.service.ReceiveCardBoardCreateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "创建卡板接口")
@RestController
@RequestMapping("/receiveCardBoardCreate")
public class ReceiveCardBoardCreateController {

	@Autowired
	private ReceiveCardBoardCreateService receiveCardBoardCreateService;
	@Autowired
	private ReceiveArrivalNoticeService receiveArrivalNoticeService;
	@Resource
	private ReceiveCardBoardCreateMapper ReceiveCardBoardCreateMapper;
	@Resource
	private ReceiveSignInMapper receiveSignInMapper;
	@Resource
	private ReceiveArrivalNoticeMapper receiveArrivalNoticeMapper;
	@Resource
	private ReceiveGoodDetailsMapper receiveGoodDetailsMapper;
	private final Logger logger = LoggerFactory.getLogger(ReceiveCardBoardCreateController.class);
	
	
	/**
	 * 添加添加卡板创建信息
	 * @param receiveCardBoardCreate
	 * @return
	 */
	@ApiOperation(value = "添加卡板创建信息", notes = "")
	@PostMapping("/insertReceiveCardInfo")
	public Integer insertReceiveCardInfo(@RequestBody List<ReceiveCardBoardCreate> receiveCardBoardCreate) {
		if(receiveCardBoardCreate == null || receiveCardBoardCreate.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"商品信息不能为空");
        }
		try {
			return receiveCardBoardCreateService.insertReceiveCardBoardCreate(receiveCardBoardCreate);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("添加卡板信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"添加卡板信息异常");
		}
	}


	
	@ApiOperation(value = "通过运单号查询商品信息", notes = "")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "wayBillId", value = "运单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sourceId", value = "来源单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "weight", value = "包裹重量", required = false, dataType = "double", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = true, dataType = "string", paramType = "query")
    })
	@GetMapping("/selectInfoById")
	public Object selectInfoByWayId(String wayBillId,String sourceId,Double weight,String warehouseCode) {
		if(StringUtil.isBlank(wayBillId) && StringUtil.isBlank(sourceId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"单号不能为空");
        }
		ReceiveGoodDetails receiveGoodDetails =new ReceiveGoodDetails();
		receiveGoodDetails.setWaybillId(wayBillId);
		receiveGoodDetails.setWeight(weight);
		if(!StringUtil.isBlank(wayBillId)){
			ReceiveArrivalNotice ArrivalNotice = receiveArrivalNoticeMapper.selectArrivalNoticeByWaybill(wayBillId);
			if(ArrivalNotice == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100501,"未查询出此运单号在仓储系统中的信息,请检查");
            }
			if(ReceiveCardBoardCreateMapper.checkWayBillIdAmount(wayBillId)>0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"此运单号已绑定过卡板,请检查后再试");
            }
			if(ArrivalNotice.getWarehouse().equals(warehouseCode) == false ) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"此运单号的来源单号仓库信息与当前作业仓库不符,请检查");
            }
			if("2".equals(ArrivalNotice.getSourceType())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此运单号属于销退入库 不能绑定卡板,请检查!");
            }
			if(receiveSignInMapper.checkWayBillId(wayBillId) <= 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此运单号未签到,不能绑定卡板。请检查后再试！");
            }
			
			if(weight != null){
				receiveArrivalNoticeService.updateDetailSelective(receiveGoodDetails);
				logger.info("{weight}" + weight.toString());
			}
			ReceiveGoodDetails selectInfoById = receiveCardBoardCreateService.selectInfoById(receiveGoodDetails);
			return selectInfoById;
		}else if(!StringUtil.isBlank(sourceId)){
			String SourceID = sourceId.toUpperCase();
			ReceiveArrivalNotice selectArrivalNoticeBySourceId = receiveArrivalNoticeMapper.selectArrivalNoticeBySourceId(SourceID);
			if(selectArrivalNoticeBySourceId == null ){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100501,"没有此来源单号的信息");
			}else{
				return true;
			}
		}
		return null;
	}
	
	
	@ApiOperation(value = "绑定关系", notes = "")
	@GetMapping("/associationId")
	public ReceiveGoodDetails  associationId(String wayBillId,String sourceId,Double weight) {
		if(StringUtil.isBlank(wayBillId) || StringUtil.isBlank(sourceId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"单号不能为空");
        }
		if(receiveGoodDetailsMapper.checkWayBillIdAmount(wayBillId) > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"此运单号已存在,请检查!");
        }
		try {
			ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
			receiveGoodDetails.setSourceId(sourceId);
			receiveGoodDetails.setWaybillId(wayBillId);
			receiveGoodDetails.setWeight(weight);
			receiveGoodDetails.setCreateTime(new Date());
			receiveGoodDetailsMapper.insertSelective(receiveGoodDetails);
			ReceiveGoodDetails receiveGoodDetails2 = new ReceiveGoodDetails();
			receiveGoodDetails2.setWaybillId(wayBillId);
			return receiveCardBoardCreateService.selectInfoById(receiveGoodDetails2);
		} catch (Exception e) {
			logger.error("绑定关系异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"绑定关系异常");
		}
	}
	
	
	
	
	
	/**
	 * 标记异常运单
	 * @param 
	 * @return
	 */
	@ApiOperation(value = "异常标记", notes = "")
	@PutMapping("/exceptionMark")
	@RequestRequire(require="wayBillId",parameter=String.class)
	public Integer exceptionMark(String  wayBillId) {
		try {
			ReceiveGoodDetails receiveGoodDetails = new ReceiveGoodDetails();
			receiveGoodDetails.setWaybillId(wayBillId);
			receiveGoodDetails.setExceptionMark(1);
			return receiveArrivalNoticeService.updateDetailSelective(receiveGoodDetails);
		} catch (Exception e) {
			logger.error("异常标记失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"异常标记失败");
		}
	}
	
	
	/**
	 * 创建卡板号
	 * @return
	 */
	@ApiOperation(value = "创建卡板号", notes = "")
	@GetMapping("/cardBoardIdCreate")
	public String cardBoardIdCreate() {
		try {
			return receiveCardBoardCreateService.setReceiveArrivalNoticeId();
		} catch (Exception e) {
			logger.error("创建卡板号异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"创建卡板号异常");
		}
	}
	
	
	
}