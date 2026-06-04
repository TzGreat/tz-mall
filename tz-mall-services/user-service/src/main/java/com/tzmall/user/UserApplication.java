package com.tzmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 * 
 * 核心注解说明:
 * - @SpringBootApplication: Spring Boot应用主注解,包含以下三个功能:
 *   1. @Configuration: 标识该类为配置类
 *   2. @EnableAutoConfiguration: 启用Spring Boot自动配置
 *   3. @ComponentScan: 自动扫描组件,默认扫描主类所在包及其子包
 * 
 * - @EnableDiscoveryClient: 启用服务发现客户端,让该服务能够注册到Nacos等注册中心
 *   这样其他服务就可以通过服务名来调用此服务,而不需要硬编码URL
 * 
 * - @MapperScan: MyBatis-Plus的注解,指定要扫描的Mapper接口位置
 *   这里配置为扫描com.tzmall.user.mapper包下的所有Mapper接口
 */
@SpringBootApplication(scanBasePackages = {"com.tzmall.user", "com.tzmall.common.core", "com.tzmall.common.redis"})
@EnableDiscoveryClient
@MapperScan("com.tzmall.user.mapper")
public class UserApplication {

    /**
     * 应用入口方法
     * 
     * SpringApplication.run()方法会完成以下工作:
     * 1. 创建Spring应用上下文(ApplicationContext)
     * 2. 自动装配@Configuration注解的类
     * 3. 启动内嵌的Tomcat服务器
     * 4. 将当前服务注册到服务注册中心
     * 
     * @param args 命令行参数,通常为空
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
