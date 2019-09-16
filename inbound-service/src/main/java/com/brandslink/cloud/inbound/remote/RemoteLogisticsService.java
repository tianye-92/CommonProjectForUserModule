package com.brandslink.cloud.inbound.remote;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;

/**
 * @author xiaozhang
 * @Classname RemoteLogisticsService
 * @Description 调用用户服务
 * @Date 2019/8/5 17:30
 */
@FeignClient(name = "brandslink-logistics-service", fallback = RemoteLogisticsService.LogisticsServiceImpl.class)
public interface RemoteLogisticsService {

	@GetMapping("/basic/selectMethod")
	String selectMethod(@RequestParam("page")String page, @RequestParam("row")String row, @RequestParam("method")Map<String,Object> method);
	
	@PostMapping("/basic/selectPlatform")
	String  selectPlatform(@RequestBody Map<String,Object> platformInfoVO);
	
    
    @Service
    class LogisticsServiceImpl implements RemoteLogisticsService{
 
        public String getCustomerShipperDetail(List<CustomerShipperDetailRequestDTO> list) {
            return fallback();
        }

        private String fallback(){
            return null;
        }

        public String selectMethod(String page, String row, Map<String, Object> method) {
			return "selectMethod Failed!!!";
		}

		public String selectPlatform(Map<String, Object> method) {
			return "selectPlatform Failed!!!";
		}

    }
}
