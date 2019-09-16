package com.brandslink.cloud.inbound.remote;

import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author xiaozhang
 * @Classname UserService
 * @Description 调用用户服务
 * @Date 2019/7/23 11:02
 */
@FeignClient(name = "brandslink-user-service", fallback = RemoteUserService.UserServiceImpl.class)
public interface RemoteUserService {

    @PostMapping("/customer/getCustomerShipperDetail")
    String getCustomerShipperDetail(@RequestBody List<CustomerShipperDetailRequestDTO> list);

    @GetMapping("/customer/getCustomer")
    String  getCustomer(@RequestParam("id") Integer id);

    @GetMapping("/customer/getCustomerByCustomerCode")
    String getCustomerByCustomerCode(@RequestParam("customerCode") String customerCode);
    
    @PostMapping("/customer/getShipperDetail")
    String getShipperDetail(List<String> shipperCodeList);
    
    @GetMapping("/customer/getShipperByCustomerId")
    String getShipperByCustomerId(@RequestParam("customerId") Integer customerId);
    
    @Service
    class UserServiceImpl implements RemoteUserService{
 
        @Override
        public String getCustomerShipperDetail(List<CustomerShipperDetailRequestDTO> list) {
            return fallback();
        }

        private String fallback(){
            return null;
        }

		@Override
        public String getCustomer(Integer id) {
			return "getCustomer failed!!!";
		}

        @Override
        public String getShipperDetail(List<String> shipperCodeList) {
            return null;
        }

		@Override
        public String getShipperByCustomerId(Integer customerId) {
			return "getShipperByCustomerId failed!!!";
		}

		@Override
        public String getCustomerByCustomerCode(String customerCode) {
			return "getCustomerByCustomerCode failed!!!";
		}
    }
}
