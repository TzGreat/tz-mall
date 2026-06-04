package com.tzmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.tzmall.order", "com.tzmall.common.core", "com.tzmall.common.redis", "com.tzmall.common.mq"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.tzmall.api.item.feign", "com.tzmall.api.user.feign"})
@MapperScan("com.tzmall.order.mapper")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
