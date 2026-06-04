package com.tzmall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private List<String> whitelist = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/item/list",
            "/api/item/{id}"
    );
}