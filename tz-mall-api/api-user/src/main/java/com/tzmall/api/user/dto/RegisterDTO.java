package com.tzmall.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求DTO
 * 
 * 注册信息说明:
 * 1. 包含用户注册时需要填写的信息
 * 2. 添加了参数校验规则,确保数据有效性
 * 3. 不包含ID等自动生成的字段
 * 
 * 校验注解说明:
 * - @NotBlank: 不能为空字符串
 * - @Size: 长度限制
 * - @Pattern: 正则表达式校验
 * 
 * 校验框架说明:
 * - 使用Jakarta Validation API (jakarta.validation.constraints)
 * - 配合@Valid注解自动触发校验
 * - 校验失败会抛出MethodArgumentNotValidException
 */
@Data
@Schema(description = "用户注册请求DTO")
public class RegisterDTO {

    /**
     * 用户名
     * 
     * 校验规则:
     * 1. 不能为空
     * 2. 长度必须在3-50个字符之间
     * 
     * example: Swagger文档示例值
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码
     * 
     * 校验规则:
     * 1. 不能为空
     * 2. 长度必须在6-100个字符之间
     * 
     * 安全性建议:
     * - 实际项目中应该要求更复杂的密码
     * - 建议包含: 大小写字母、数字、特殊字符
     * - 建议最小长度8位
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 手机号
     * 
     * 校验规则:
     * - 使用正则表达式: ^1[3-9]\\d{9}$
     * - 1: 第一位必须是1
     * - [3-9]: 第二位必须是3-9之间的数字
     * - \\d{9}: 后面9位是任意数字
     * - 总共11位手机号
     * 
     * 说明:
     * - 手机号是选填字段
     * - 如果填写则必须符合格式要求
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
