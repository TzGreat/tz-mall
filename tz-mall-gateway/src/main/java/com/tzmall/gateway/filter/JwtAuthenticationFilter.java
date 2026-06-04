package com.tzmall.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzmall.common.core.exception.BizException;
import com.tzmall.common.core.result.Result;
import com.tzmall.common.core.utils.JwtUtil;
import com.tzmall.gateway.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("请求路径: {}", path);

        if (isWhitelisted(path)) {
            log.debug("白名单路径，直接放行: {}", path);
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("未携带Token: {}", path);
            return unauthorized(exchange.getResponse(), "未携带Token");
        }

        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            log.debug("Token验证通过: {}, 解析出的 claims 内容: {}", path, claims);

            // 将用户信息传递到下游服务
            String userIdValue = String.valueOf(claims.get("userId"));
            String roleValue = String.valueOf(claims.get("role"));
            log.debug("准备写入下游请求头: X-User-Id={}, X-User-Role={}", userIdValue, roleValue);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userIdValue)
                    .header("X-User-Role", roleValue)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (BizException e) {
            log.warn("Token验证失败: {} - {}", path, e.getMessage());
            return unauthorized(exchange.getResponse(), e.getMessage());
        } catch (Exception e) {
            log.error("Token验证异常: {}", path, e);
            return unauthorized(exchange.getResponse(), "Token无效");
        }
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = authProperties.getWhitelist();
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        return whitelist.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<Void> result = Result.fail(401, message);

        try {
            String json = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化响应失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}