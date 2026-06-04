package com.tzmall.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员权限注解
 * <p>
 * 标记在 Controller 方法上，表示该方法需要管理员权限才能访问。
 * 由 RoleInterceptor 拦截器负责校验。
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAdmin {
}
