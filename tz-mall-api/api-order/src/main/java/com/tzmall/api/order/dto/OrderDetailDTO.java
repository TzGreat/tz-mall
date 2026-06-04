package com.tzmall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细响应DTO
 *
 * 用于表示订单项的详细信息,包含商品信息、单价、数量等
 */
@Data
@Schema(description = "订单明细相应DTO")
public class OrderDetailDTO {
    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品名称")
    private String itemName;

    @Schema(description = "商品单价")
    private BigDecimal itemPrice;

    @Schema(description = "购买数量")
    private Integer num;
}
