package com.tzmall.order.entity;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("order_detail")
@Accessors(chain = true)
public class OrderDetail {
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description = "订单详情ID")
    private Long id;

    @TableField("order_id")
    @Schema(description = "订单ID")
    private Long orderId;

    @TableField("item_id")
    @Schema(description = "商品ID")
    private Long itemId;

    @TableField("item_name")
    @Schema(description = "商品名称")
    private String itemName;

    @TableField("item_price")
    @Schema(description = "商品价格")
    private BigDecimal itemPrice;

    @TableField("num")
    @Schema(description = "商品数量")
    private Integer num;
}
