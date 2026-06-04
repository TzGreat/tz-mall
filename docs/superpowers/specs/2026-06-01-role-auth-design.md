# TZ-Mall 角色权限功能设计文档

## 1. 背景

当前 TZ-Mall 项目所有登录用户拥有相同权限，没有角色区分。需要实现管理员/普通用户两种角色，以保护敏感操作（如创建管理员账号）。

## 2. 需求

- `user` 表添加 `role` 字段：`0` = 管理员，`1` = 普通用户
- 第一位管理员通过数据库手动添加（`UPDATE user SET role = 0 WHERE username = 'xxx'`）
- 注册接口只能创建普通用户（role 默认为 1）
- 新增管理员专用接口，仅管理员可调用，用于创建新用户（可指定角色）
- 权限校验采用服务层注解拦截器方案（`@RequireAdmin`）

## 3. 架构设计

### 3.1 认证链路

```
请求 → Gateway（验证JWT + 解析claims + 传递用户信息到请求头）
     → 服务层（RoleInterceptor 检查 @RequireAdmin 注解 + 验证 X-User-Role）
     → 业务方法
```

### 3.2 角色定义

| 值 | 角色 | 说明 |
|----|------|------|
| 0  | 管理员 | 可创建用户（含管理员）、管理商品等 |
| 1  | 普通用户 | 默认角色，注册时自动分配 |

## 4. 需要新增的文件（4个）

| # | 文件 | 模块 | 用途 |
|---|------|------|------|
| 1 | `RequireAdmin.java` | common-core | 管理员权限注解 |
| 2 | `RoleInterceptor.java` | user-service | 角色权限拦截器 |
| 3 | `AdminCreateUserDTO.java` | api-user | 管理员创建用户的请求 DTO |
| 4 | `AdminUserController.java` | user-service | 管理员专用接口控制器 |

### 4.1 RequireAdmin.java

- 路径：`tz-mall-common/common-core/src/main/java/com/tzmall/common/core/annotation/RequireAdmin.java`
- 作用：标记在方法上，表示该方法需要管理员权限
- 内容：`@Target(ElementType.METHOD)` + `@Retention(RetentionPolicy.RUNTIME)` 注解

### 4.2 RoleInterceptor.java

- 路径：`tz-mall-services/user-service/src/main/java/com/tzmall/user/interceptor/RoleInterceptor.java`
- 作用：拦截带有 `@RequireAdmin` 注解的方法，从请求头 `X-User-Role` 读取角色值，非管理员（role != 0）抛出 403 异常
- 依赖：`RequireAdmin` 注解、`BizException`

### 4.3 AdminCreateUserDTO.java

- 路径：`tz-mall-api/api-user/src/main/java/com/tzmall/api/user/dto/AdminCreateUserDTO.java`
- 作用：管理员创建用户时的请求体
- 字段：`username`（必填）、`password`（必填）、`phone`（选填）、`role`（必填，0或1）

### 4.4 AdminUserController.java

- 路径：`tz-mall-services/user-service/src/main/java/com/tzmall/user/controller/AdminUserController.java`
- 作用：管理员专用接口，路径前缀 `/api/admin/user`
- 接口：`POST /api/admin/user` — 创建用户（可指定角色），带 `@RequireAdmin` 注解

## 5. 需要修改的文件（7个）

| # | 文件 | 修改内容 |
|---|------|----------|
| 1 | `init.sql` | user 表添加 `role` 字段，默认值 1 |
| 2 | `User.java` | 添加 `role` 属性（Integer 类型） |
| 3 | `UserDTO.java` | 添加 `role` 属性（Integer 类型） |
| 4 | `UserServiceImpl.java` | `register()` 设置 role=1；`login()` Token 加入 role；`convertToDTO()` 映射 role；新增 `createUserByAdmin()` 方法 |
| 5 | `IUserService.java` | 新增 `createUserByAdmin(AdminCreateUserDTO)` 方法声明 |
| 6 | `JwtAuthenticationFilter.java` | Token 验证通过后，解析 claims 并将 `X-User-Id`、`X-User-Role` 写入请求头传递给下游 |
| 7 | `WebMvcConfig.java` | 注册 `RoleInterceptor`，拦截 `/api/admin/**` 路径 |

## 6. 详细修改说明

### 6.1 init.sql

在 user 表定义中 `phone` 字段后添加：
```sql
`role` tinyint NOT NULL DEFAULT '1' COMMENT '角色 0-管理员 1-普通用户',
```

### 6.2 User.java

在 `phone` 字段后添加：
```java
@TableField("role")
@Schema(description = "角色 0-管理员 1-普通用户")
private Integer role;
```

### 6.3 UserDTO.java

添加：
```java
@Schema(description = "角色 0-管理员 1-普通用户")
private Integer role;
```

### 6.4 UserServiceImpl.java

- `register()` 方法：添加 `user.setRole(1);`
- `login()` 方法：claims 中添加 `"role", user.getRole()`
- `convertToDTO()` 方法：添加 `dto.setRole(user.getRole());`
- 新增 `createUserByAdmin(AdminCreateUserDTO dto)` 方法：校验用户名唯一性、密码加密、设置角色、保存

### 6.5 IUserService.java

新增方法声明：
```java
Result<UserDTO> createUserByAdmin(AdminCreateUserDTO dto);
```

### 6.6 JwtAuthenticationFilter.java

修改 `filter()` 方法中 Token 验证通过的分支：
```java
Map<String, Object> claims = JwtUtil.parseToken(token);
ServerHttpRequest mutatedRequest = request.mutate()
    .header("X-User-Id", claims.get("userId").toString())
    .header("X-User-Role", claims.get("role").toString())
    .build();
return chain.filter(exchange.mutate().request(mutatedRequest).build());
```

### 6.7 WebMvcConfig.java

实现 `WebMvcConfigurer`，注册 `RoleInterceptor` 拦截 `/api/admin/**` 路径。

## 7. 不涉及修改的部分

- Gateway 的 `AuthProperties` 白名单：管理员接口需要登录，不在白名单中，无需修改
- `TokenInterceptor.java`：保持现状不启用，角色校验由新的 `RoleInterceptor` 负责
- Redis Token 存储：保持现状，不影响角色功能

## 8. 首位管理员初始化

项目部署后，手动执行 SQL：
```sql
-- 先通过注册接口创建用户，然后手动修改角色
UPDATE user SET role = 0 WHERE username = 'your_admin_username';
```
