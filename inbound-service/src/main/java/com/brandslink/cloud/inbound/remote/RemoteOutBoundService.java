package com.brandslink.cloud.inbound.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;


/**
 * 远程调用服务
 * */
@FeignClient(name = "brandslink-outbound-service", fallback = RemoteOutBoundService.RemoteOutBoundServiceImpl.class)
public interface RemoteOutBoundService {


	@GetMapping("order/findOrder/{packageNum}")
	String findOrderDetailsByOrderNum(@PathVariable("packageNum") String packageNum);
	
	
	@GetMapping("order/findOrderDetails/{waybillNum}")
	String findOrderDetails(@PathVariable("waybillNum") String waybillNum);
	
	@GetMapping("order/findOrderDetailsByZ/{waybillNum}")
	String findOrderDetailsByZ(@PathVariable("waybillNum") String waybillNum);
	
	
    /**
     * 断路降级
     * */
    @Service
    class RemoteOutBoundServiceImpl implements RemoteOutBoundService {
        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "出库服务异常"));
        }

		
		@Override
        public String findOrderDetailsByOrderNum(String packageNum) {
			return "根据包裹号查询订单详情异常";
		}

		@Override
        public String findOrderDetails(String waybillNum) {
			return "根据运单号查询订单详情异常";
		}

		@Override
        public String findOrderDetailsByZ(String waybillNum) {
			return "专属接口根据运单号查询订单详情异常";
		}

	}
}



