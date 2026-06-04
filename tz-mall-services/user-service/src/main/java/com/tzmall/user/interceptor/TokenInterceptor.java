package com.tzmall.user.interceptor;

import com.tzmall.common.core.exception.BizException;
import com.tzmall.common.redis.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从 Header 中获取 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        token = token.substring(7);

        // 2. 验证 token 是否在 Redis 中存在
        Object userId = redisUtil.get("token:" + token);
        if (userId == null) {
            throw new BizException(401, "Token 已过期，请重新登录");
        }

        // 3. 将 userId 存入 request 供后续使用
        request.setAttribute("userId", userId);
        return true;
    }
}
