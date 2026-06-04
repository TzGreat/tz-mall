package com.tzmall.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tzmall.api.order.dto.CreateOrderDTO;
import com.tzmall.api.order.dto.OrderDTO;
import com.tzmall.api.order.dto.PayOrderDTO;
import com.tzmall.api.order.dto.PayOrderResultDTO;
import com.tzmall.common.core.annotation.RequireAdmin;
import com.tzmall.common.core.result.Result;
import com.tzmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理",description = "订单CRUD操作")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单接口
     *
     * HTTP方法: POST
     * 请求路径: /api/order/create
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "创建订单")
    public Result<OrderDTO> createOrder(@Parameter(description = "订单创建信息") @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }

    /**
     * 根据订单ID查询订单详情
     *
     * HTTP方法: GET
     * 请求路径: /api/order/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    public Result<OrderDTO> getOrderById(@Parameter(description = "订单ID") @PathVariable Long id){
        return orderService.getOrderById(id);
    }

    /**
     * 根据订单号查询订单详情
     *
     * HTTP方法: GET
     * 请求路径: /api/order/no/{orderNo}
     */
    @GetMapping("/no/{orderNo}")
    @Operation(summary = "查询订单详情", description = "根据订单号查询订单详情")
    public Result<OrderDTO> getOrderByOrderNo(@Parameter(description = "订单号") @PathVariable String orderNo){
        return orderService.getOrderByOrderNo(orderNo);
    }

    /**
     * 根据用户ID查询用户订单列表
     *
     * HTTP方法: GET
     * 请求路径: /api/order/list
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户订单列表", description = "根据用户ID查询用户订单列表")
    public Result<IPage<OrderDTO>> listUserOrders(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size){
        return orderService.listUserOrders(userId, page, size);
    }

    /**
     * 根据订单ID取消订单
     *
     * HTTP方法: PATCH
     * 请求路径: /api/order/cancel/{orderId}
     */
    @PatchMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单", description = "根据订单ID取消订单")
    public Result<Void> cancelOrder(@Parameter(description = "订单ID") @PathVariable Long orderId){
        return orderService.cancelOrder(orderId);
    }

    /**
     * 订单支付接口
     *
     * HTTP方法: POST
     * 请求路径: /api/order/pay
     */
    @PostMapping("/pay")
    @Operation(summary = "订单支付", description = "根据订单ID进行支付")
    public Result<PayOrderResultDTO> payOrder(@Parameter(description = "支付信息") @Valid @RequestBody PayOrderDTO payOrderDTO){
        return orderService.payOrder(payOrderDTO);
    }

    @GetMapping("/admin/list")
    @RequireAdmin
    @Operation(summary = "管理员查询所有订单", description = "管理员查询所有订单列表")
    public Result<IPage<OrderDTO>> listAllOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size){
        return orderService.listAllOrders(page, size);
    }

    @PatchMapping("/admin/{id}/status")
    @RequireAdmin
    @Operation(summary = "管理员更新订单状态", description = "管理员更新订单状态")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam Integer status){
        return orderService.updateOrderStatus(id, status);
    }
}
