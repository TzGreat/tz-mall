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
