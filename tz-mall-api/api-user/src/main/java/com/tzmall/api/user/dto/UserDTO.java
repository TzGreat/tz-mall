package com.tzmall.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO (Data Transfer Object)
 * 
 * DTO的作用:
 * 1. 数据传输对象: 用于API接口的请求和响应
 * 2. 隐藏敏感信息: 只暴露需要的字段(如不包含密码)
 * 3. 解耦: 前后端数据交互的桥梁
 * 4. 格式化: 可以对数据进行处理和格式化
 * 
 * 与Entity的区别:
 * - Entity(实体): 对应数据库表,包含所有字段
 * - DTO: 只包含需要传输的数据,可以过滤敏感字段
 * 
 * 命名建议:
 * - 请求DTO: XxxRequestDTO 或 XxxDTO
 * - 响应DTO: XxxResponseDTO 或 XxxDTO
 * - 这里简化为UserDTO,用于用户信息响应
 * 
 * 使用场景:
 * - 用户详情查询响应
 * - 用户列表查询响应
 * - 注册、登录成功后返回用户信息
 */
@Data
@Schema(description = "用户信息响应DTO")
public class UserDTO {

    /**
     * 用户ID
     * 
     * 说明: 用户唯一标识,全局唯一
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     * 
     * 说明: 用户登录账号
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 手机号
     * 
     * 说明: 用户联系方式,可为空
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 角色
     *
     * 说明: 用户角色,0-管理员,1-普通用户
     */
    @Schema(description = "角色 0-管理员 1-普通用户")
    private Integer role;

    /**
     * 创建时间
     * 
     * 说明: 用户注册时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 
     * 说明: 用户信息最后更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
