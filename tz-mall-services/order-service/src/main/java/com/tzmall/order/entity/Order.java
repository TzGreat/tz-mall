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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`order`")
@Schema(description = "订单表")
@Accessors(chain = true)
public class Order {
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description = "订单ID")
    private Long id;

    @TableField("order_no")
    @Schema(description = "订单号")
    private Long orderNo;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("total_price")
    @Schema(description = "订单总金额")
    private BigDecimal totalPrice;

    @TableField("status")
    @Schema(description = "订单状态(0:待支付,1:待发货,2:待收货,3:待评价,4:已完成,5:已取消)")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
