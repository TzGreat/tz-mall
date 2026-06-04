package com.tzmall.common.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * <p>
 * 使用 Knife4j (OpenAPI 3.x) 提供在线 API 文档。
 * 访问地址：http://{host}:{port}/doc.html
 * </p>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TZ-Mall API 文档")
                        .description("TZ-Mall 微服务商城项目接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("TZ-Mall")
                                .url("https://github.com/tz-mall"))//示例URL
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
