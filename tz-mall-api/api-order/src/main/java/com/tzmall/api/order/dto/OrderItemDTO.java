package com.tzmall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单详情请求DTO
 *
 * 用于创建订单时,包含商品ID和数量
 */
@Data
@Schema(description = "订单详情请求DTO")
public class OrderItemDTO {
    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    @Schema(description = "商品数量")
    @Min(value = 1, message = "商品数量不能小于1")
    @NotNull(message = "商品数量不能为空")
    private Integer num;
}
