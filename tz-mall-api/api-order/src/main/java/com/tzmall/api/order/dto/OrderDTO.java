package com.tzmall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 *
 * 设计说明:
 * 1. 包含订单主表所有字段
 * 2. 包含订单明细列表
 * 3. 用于订单查询接口返回
 */
@Data
@Schema(description = "订单响应DTO")
public class OrderDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单总价")
    private BigDecimal totalPrice;

    @Schema(description = "订单状态(0-待支付 1-已支付 2-已发货 3-已完成 4-已取消)", example = "0")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "订单明细列表")
    private List<OrderDetailDTO> orderDetails;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
