package com.tzmall.api.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品创建/更新请求DTO
 *
 * 设计说明:
 * 1. 不包含id字段(由数据库自动生成)
 * 2. 不包含时间字段(由数据库自动维护)
 * 3. 包含完整的业务字段用于创建和更新操作
 * 4. 添加参数校验规则确保数据有效性
 */

@Data
@Schema(description = "商品创建/更新请求DTO")
public class ItemRequestDTO {

    /**
     * 商品名称
     *
     * 必填字段
     * 最小长度: 2个字符
     * 最大长度: 20个字符
     */
    @Schema(description = "商品名称")
    @Size(min = 2, max = 20, message = "商品名称长度必须在2到20个字符之间")
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品价格
     *
     * 必填字段
     * 最小值: 0.01
     */
    @Schema(description = "商品价格")
    @DecimalMin(value = "0.01", message = "商品价格必须大于等于0")
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    /**
     * 商品库存
     *
     * 必填字段
     * 最小值: 0
     */
    @Schema(description = "商品库存")
    @Min(value = 0, message = "商品库存必须大于等于0")
    private  Integer stock;

    /**
     * 商品描述
     *
     * 选填字段
     * 最大长度: 5000字符(支持较长描述)
     */
    @Size(max = 5000, message = "商品描述不能超过5000个字符")
    @Schema(description = "商品描述", example = "Apple最新旗舰手机，搭载A17 Pro芯片")
    private String description;

    /**
     * 商品图片URL
     *
     * 选填字段
     * 可存储单个或多个图片URL(用逗号分隔)
     */
    @Size(max = 1000, message = "图片URL长度不能超过1000个字符")
    @Schema(description = "商品图片URL", example = "https://example.com/images/iphone15.jpg")
    private String image;

    /**
     * 商品分类
     *
     * 选填字段
     * 用于商品分类展示和筛选
     */
    @Size(max = 50, message = "分类名称不能超过50个字符")
    @Schema(description = "商品分类", example = "电子产品")
    private String category;

    /**
     * 商品状态
     *
     * 必填字段
     * 取值范围: 0(下架)或1(上架)
     */
    @Min(value = 0, message = "商品状态值不能为负数")
    @Max(value = 1, message = "商品状态值只能为0(下架)或1(上架)")
    @Schema(description = "商品状态(0-下架,1-上架)", example = "1")
    private Integer status;
}
