package com.tzmall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "订单支付请求DTO")
public class PayOrderDTO {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true, example = "1")
    private Long orderId;

    @Schema(description = "支付方式(1:微信支付,2:支付宝支付)", example = "1")
    private Integer payMethod = 1;
}
