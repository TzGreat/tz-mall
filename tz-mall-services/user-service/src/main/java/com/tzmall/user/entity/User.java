package com.tzmall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户表实体类
 * 
 * 实体类说明:
 * 1. 对应数据库中的user表
 * 2. 用于ORM映射,将数据库表映射为Java对象
 * 3. 包含数据库表字段对应的属性
 * 4. 通常一个实体类对应一张表
 * 
 * 注解说明:
 * - @TableName: 指定数据库表名
 * - @TableId: 标识主键字段
 * - @TableField: 标识普通字段(当属性名与表字段名不一致时使用)
 * 
 * ORM框架说明:
 * - MyBatis-Plus会自动将实体类与数据库表进行映射
 * - 属性名默认与字段名对应(支持驼峰命名自动映射)
 *
 * @author author
 * @since 2026-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 
     * @TableId注解说明:
     * - value: 指定数据库中的主键字段名
     * - type: 指定主键生成策略
     *   - IdType.AUTO: 数据库自增
     *   - IdType.INPUT: 用户输入
     *   - IdType.ASSIGN_ID: 雪花算法生成(分布式环境推荐)
     *   - IdType.ASSIGN_UUID: UUID生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     * 
     * 数据库字段映射:
     * - 默认为username(驼峰转下划线)
     * - 如果数据库字段名不同,需要使用@TableField("field_name")指定
     */
    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     * 
     * 存储说明:
     * - 密码在数据库中以MD5加密形式存储
     * - 不能明文存储用户密码(安全性考虑)
     * - 登录时需要将输入的密码用相同方式加密后比对
     */
    @TableField("password")
    @Schema(description = "密码")
    private String password;

    /**
     * 手机号
     * 
     * 字段说明:
     * - 选填字段
     * - 可以添加唯一约束防止重复手机号注册
     */
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 角色
     *
     * 字段说明:
     * - 0-管理员
     * - 1-普通用户
     */
    @TableField("role")
    @Schema(description = "角色,0-管理员,1-普通用户")
       private Integer role;

    /**
     * 创建时间
     * 
     * 使用说明:
     * - 记录用户注册时间
     * - 通常在插入数据时自动设置
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 
     * 使用说明:
     * - 记录用户信息最后修改时间
     * - 每次更新用户信息时自动更新
     * - 有助于数据审计和问题排查
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
