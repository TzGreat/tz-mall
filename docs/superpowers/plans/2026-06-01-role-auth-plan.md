# 角色权限功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 TZ-Mall 项目实现管理员/普通用户角色区分，支持管理员创建新用户（含管理员），采用服务层注解拦截器方案。

**Architecture:** Gateway 层解析 JWT Token 中的角色信息，通过请求头传递给下游服务；服务层通过 `@RequireAdmin` 注解 + `RoleInterceptor` 拦截器校验权限。

**Tech Stack:** Java 17, Spring Boot, Spring Cloud Gateway, MyBatis-Plus, JWT (Auth0), Redis

---

### Task 1: 数据库添加 role 字段

**Files:**
- Modify: `docker/mysql/init/init.sql:11-20`

- [ ] **Step 1: 在 user 表定义中添加 role 字段**

在 `phone` 字段后、`create_time` 字段前添加 `role` 字段：

```sql
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '角色 0-管理员 1-普通用户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

- [ ] **Step 2: Commit**

```bash
git add docker/mysql/init/init.sql
git commit -m "feat: add role field to user table (0=admin, 1=normal user)"
```

---

### Task 2: User 实体添加 role 属性

**Files:**
- Modify: `tz-mall-services/user-service/src/main/java/com/tzmall/user/entity/User.java`

- [ ] **Step 1: 在 User 实体类中添加 role 字段**

在 `phone` 字段后、`createTime` 字段前添加：

```java
@TableField("role")
@Schema(description = "角色 0-管理员 1-普通用户")
private Integer role;
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/entity/User.java
git commit -m "feat: add role field to User entity"
```

---

### Task 3: UserDTO 添加 role 属性

**Files:**
- Modify: `tz-mall-api/api-user/src/main/java/com/tzmall/api/user/dto/UserDTO.java`

- [ ] **Step 1: 在 UserDTO 中添加 role 字段**

在 `phone` 字段后、`createTime` 字段前添加：

```java
@Schema(description = "角色 0-管理员 1-普通用户")
private Integer role;
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-api/api-user/src/main/java/com/tzmall/api/user/dto/UserDTO.java
git commit -m "feat: add role field to UserDTO"
```

---

### Task 4: 新增 RequireAdmin 注解

**Files:**
- Create: `tz-mall-common/common-core/src/main/java/com/tzmall/common/core/annotation/RequireAdmin.java`

- [ ] **Step 1: 创建 RequireAdmin 注解**

```java
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
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-common/common-core/src/main/java/com/tzmall/common/core/annotation/RequireAdmin.java
git commit -m "feat: add RequireAdmin annotation"
```

---

### Task 5: 新增 AdminCreateUserDTO

**Files:**
- Create: `tz-mall-api/api-user/src/main/java/com/tzmall/api/user/dto/AdminCreateUserDTO.java`

- [ ] **Step 1: 创建 AdminCreateUserDTO**

```java
package com.tzmall.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员创建用户请求DTO
 */
@Data
@Schema(description = "管理员创建用户请求DTO")
public class AdminCreateUserDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Schema(description = "用户名", example = "newuser")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotNull(message = "角色不能为空")
    @Schema(description = "角色 0-管理员 1-普通用户", example = "1")
    private Integer role;
}
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-api/api-user/src/main/java/com/tzmall/api/user/dto/AdminCreateUserDTO.java
git commit -m "feat: add AdminCreateUserDTO for admin user creation"
```

---

### Task 6: 修改 UserServiceImpl — register/login/convertToDTO

**Files:**
- Modify: `tz-mall-services/user-service/src/main/java/com/tzmall/user/service/impl/UserServiceImpl.java`

- [ ] **Step 1: 修改 register() 方法，设置默认角色为普通用户**

在 `user.setPhone(registerDTO.getPhone());` 之后添加：

```java
user.setRole(1); // 默认普通用户
```

- [ ] **Step 2: 修改 login() 方法，Token 中包含 role**

将第 143 行：
```java
Map<String, Object> claims = Map.of("userId", user.getId(), "username", user.getUsername());
```
改为：
```java
Map<String, Object> claims = Map.of("userId", user.getId(), "username", user.getUsername(), "role", user.getRole());
```

- [ ] **Step 3: 修改 convertToDTO() 方法，映射 role**

在 `dto.setPhone(user.getPhone());` 之后添加：

```java
dto.setRole(user.getRole());
```

- [ ] **Step 4: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/service/impl/UserServiceImpl.java
git commit -m "feat: add role support to register, login and convertToDTO"
```

---

### Task 7: 新增管理员创建用户方法

**Files:**
- Modify: `tz-mall-services/user-service/src/main/java/com/tzmall/user/service/IUserService.java`
- Modify: `tz-mall-services/user-service/src/main/java/com/tzmall/user/service/impl/UserServiceImpl.java`

- [ ] **Step 1: 在 IUserService 接口中添加方法声明**

在接口末尾（`listUsers` 方法后）添加：

```java
/**
 * 管理员创建用户
 *
 * @param dto 管理员创建用户的请求信息（包含用户名、密码、手机号、角色）
 * @return 统一响应格式，包含创建的用户信息
 */
Result<UserDTO> createUserByAdmin(AdminCreateUserDTO dto);
```

需要在文件顶部添加 import：
```java
import com.tzmall.api.user.dto.AdminCreateUserDTO;
```

- [ ] **Step 2: 在 UserServiceImpl 中实现 createUserByAdmin 方法**

在 `convertToDTO` 方法之前添加：

```java
/**
 * 管理员创建用户
 *
 * 创建流程:
 * 1. 校验用户名唯一性
 * 2. 校验角色值合法性（只能是 0 或 1）
 * 3. 密码 MD5 加密
 * 4. 设置角色、时间戳
 * 5. 保存到数据库
 */
public Result<UserDTO> createUserByAdmin(AdminCreateUserDTO dto) {
    // 校验角色值
    if (dto.getRole() != 0 && dto.getRole() != 1) {
        return Result.fail(400, "角色值不合法，只能是 0（管理员）或 1（普通用户）");
    }

    // 校验用户名唯一性
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(User::getUsername, dto.getUsername());
    User existingUser = this.getOne(queryWrapper);
    if (existingUser != null) {
        return Result.fail(400, "用户名已存在");
    }

    // 创建用户
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
    user.setPhone(dto.getPhone());
    user.setRole(dto.getRole());
    user.setCreateTime(LocalDateTime.now());
    user.setUpdateTime(LocalDateTime.now());

    this.save(user);

    return Result.success("创建用户成功", convertToDTO(user));
}
```

需要在文件顶部添加 import：
```java
import com.tzmall.api.user.dto.AdminCreateUserDTO;
```

- [ ] **Step 3: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/service/IUserService.java
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/service/impl/UserServiceImpl.java
git commit -m "feat: add createUserByAdmin method for admin user creation"
```

---

### Task 8: 新增 RoleInterceptor 拦截器

**Files:**
- Create: `tz-mall-services/user-service/src/main/java/com/tzmall/user/interceptor/RoleInterceptor.java`

- [ ] **Step 1: 创建 RoleInterceptor**

```java
package com.tzmall.user.interceptor;

import com.tzmall.common.core.annotation.RequireAdmin;
import com.tzmall.common.core.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.hasMethodAnnotation(RequireAdmin.class)) {
                String roleHeader = request.getHeader("X-User-Role");
                if (roleHeader == null || !"0".equals(roleHeader)) {
                    throw new BizException(403, "需要管理员权限");
                }
            }
        }
        return true;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/interceptor/RoleInterceptor.java
git commit -m "feat: add RoleInterceptor for admin permission check"
```

---

### Task 9: 修改 WebMvcConfig 注册拦截器

**Files:**
- Modify: `tz-mall-services/user-service/src/main/java/com/tzmall/user/config/WebMvcConfig.java`

- [ ] **Step 1: 修改 WebMvcConfig，注册 RoleInterceptor**

将整个文件替换为：

```java
package com.tzmall.user.config;

import com.tzmall.user.interceptor.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/config/WebMvcConfig.java
git commit -m "feat: register RoleInterceptor for /api/admin/** paths"
```

---

### Task 10: 新增 AdminUserController

**Files:**
- Create: `tz-mall-services/user-service/src/main/java/com/tzmall/user/controller/AdminUserController.java`

- [ ] **Step 1: 创建 AdminUserController**

```java
package com.tzmall.user.controller;

import com.tzmall.api.user.dto.AdminCreateUserDTO;
import com.tzmall.api.user.dto.UserDTO;
import com.tzmall.common.core.annotation.RequireAdmin;
import com.tzmall.common.core.result.Result;
import com.tzmall.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员用户管理控制器
 * <p>
 * 所有接口均需要管理员权限（通过 @RequireAdmin 注解 + RoleInterceptor 拦截器校验）。
 * </p>
 */
@RestController
@RequestMapping("/api/admin/user")
@Tag(name = "管理员用户管理", description = "管理员专用用户管理接口")
public class AdminUserController {

    private final IUserService userService;

    public AdminUserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @RequireAdmin
    @Operation(summary = "创建用户", description = "管理员创建用户，可指定角色（0-管理员 1-普通用户）")
    public Result<UserDTO> createUser(@Valid @RequestBody AdminCreateUserDTO dto) {
        return userService.createUserByAdmin(dto);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-services/user-service/src/main/java/com/tzmall/user/controller/AdminUserController.java
git commit -m "feat: add AdminUserController with RequireAdmin protection"
```

---

### Task 11: 修改 Gateway JwtAuthenticationFilter 传递用户信息

**Files:**
- Modify: `tz-mall-gateway/src/main/java/com/tzmall/gateway/filter/JwtAuthenticationFilter.java`

- [ ] **Step 1: 修改 filter() 方法，Token 验证通过后传递用户信息到请求头**

将第 58-61 行：
```java
try {
    JwtUtil.parseToken(token);
    log.debug("Token验证通过: {}", path);
    return chain.filter(exchange);
```
改为：
```java
try {
    Map<String, Object> claims = JwtUtil.parseToken(token);
    log.debug("Token验证通过: {}", path);

    // 将用户信息传递到下游服务
    ServerHttpRequest mutatedRequest = request.mutate()
            .header("X-User-Id", String.valueOf(claims.get("userId")))
            .header("X-User-Role", String.valueOf(claims.get("role")))
            .build();
    return chain.filter(exchange.mutate().request(mutatedRequest).build());
```

需要在文件顶部添加 import：
```java
import java.util.Map;
```

- [ ] **Step 2: Commit**

```bash
git add tz-mall-gateway/src/main/java/com/tzmall/gateway/filter/JwtAuthenticationFilter.java
git commit -m "feat: pass userId and role via headers to downstream services"
```

---

### Task 12: 验证编译

- [ ] **Step 1: 编译整个项目确认无错误**

```bash
mvn clean compile -pl tz-mall-common/common-core,tz-mall-api/api-user,tz-mall-services/user-service,tz-mall-gateway -am
```

Expected: BUILD SUCCESS

- [ ] **Step 2: Final Commit (如有修复)**

```bash
git add -A
git commit -m "fix: compilation fixes if any"
```
