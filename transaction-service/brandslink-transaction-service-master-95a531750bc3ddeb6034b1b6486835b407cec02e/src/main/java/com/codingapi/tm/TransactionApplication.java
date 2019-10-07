package com.codingapi.tm;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 *分布式事务服务启动类
 * */
@SpringBootApplication
@EnableDiscoveryClient
@EnableApolloConfig
public class TransactionApplication {


    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
        System.out.println("=================TransactionApplication启动成功====================");
    }

}
