package com.tzmall.common.core.interceptor;

import com.tzmall.common.core.annotation.RequireAdmin;
import com.tzmall.common.core.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 角色权限拦截器
 * <p>
 * 拦截带有 @RequireAdmin 注解的 Controller 方法，
 * 从请求头 X-User-Role 中读取角色值，非管理员（role != 0）拒绝访问。
 * </p>
 */
@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.hasMethodAnnotation(RequireAdmin.class)) {
                String roleHeader = request.getHeader("X-User-Role");
                String userIdHeader = request.getHeader("X-User-Id");
                log.debug("[RoleInterceptor] 请求路径: {}, 从请求头读取到的: userIdHeader={}, roleHeader={}",
                        request.getRequestURI(), userIdHeader, roleHeader);
                if (roleHeader == null || !"0".equals(roleHeader)) {
                    log.warn("[RoleInterceptor] 权限拒绝，需要 role=0，但实际是: {}", roleHeader);
                    throw new BizException(403, "需要管理员权限");
                }
            }
        }
        return true;
    }
}
