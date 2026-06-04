package com.tzmall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 *
 * 用于创建订单时,包含订单项列表
 */
@Data
@Schema(description = "创建订单DTO")
public class CreateOrderDTO  {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "订单项明细列表")
    @NotNull(message = "订单项明细列表不能为空")
    @Valid
    private List<OrderItemDTO> items;
}
