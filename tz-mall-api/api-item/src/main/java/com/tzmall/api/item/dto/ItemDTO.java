package com.tzmall.api.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 商品响应DTO
 */
@Data
@Schema(description = "商品查询参数")
public class ItemDTO {
    //id name price stock image description category

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品库存")
    private Integer stock;

    @Schema(description = "商品图片URL")
    private String image;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "商品状态(0-下架,1-上架)", example = "1")
    private Integer status;

    @Schema(description = "商品创建时间")
    private LocalDateTime createTime;

    @Schema(description = "商品更新时间")
    private LocalDateTime updateTime;

}