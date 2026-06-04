package com.tzmall.item.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("item")
@Schema(description = "商品表")
@Accessors(chain = true)
public class Item {
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description = "商品ID")
    private Long id;

    @TableField("name")
    @Schema(description = "商品名称")
    private String name;

    @TableField("price")
    @Schema(description = "商品价格")
    private BigDecimal price;

    @TableField("stock")
    @Schema(description = "商品库存")
    private Integer stock;

    @TableField("description")
    @Schema(description = "商品描述")
    private String description;

    @TableField("image")
    @Schema(description = "商品图片")
    private String image;

    @TableField("category")
    @Schema(description = "商品分类")
    private String category;

    @TableField("status")
    @Schema(description = "商品状态")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
