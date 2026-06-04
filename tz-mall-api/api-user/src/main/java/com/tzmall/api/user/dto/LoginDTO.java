package com.tzmall.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求DTO
 * 
 * 请求DTO的作用:
 * 1. 接收前端提交的数据
 * 2. 进行参数校验
 * 3. 与用户实体分离,只包含登录所需的字段
 * 
 * 设计考虑:
 * - 只包含登录必需的字段(用户名和密码)
 * - 不包含其他无关字段,保持接口简洁
 * 
 * 验证注解说明:
 * - @NotBlank: 不能为空字符串
 * - @Size: 长度限制
 */
@Data
@Schema(description = "用户登录请求DTO")
public class LoginDTO {

    /**
     * 用户名
     * 
     * @NotBlank: 验证字段不能为空且不能全是空白字符
     * message: 验证失败时的错误信息
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码
     * 
     * @NotBlank: 验证字段不能为空
     * 注意: 实际项目中密码应该更复杂,如包含长度限制、字符类型要求等
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String password;
}
