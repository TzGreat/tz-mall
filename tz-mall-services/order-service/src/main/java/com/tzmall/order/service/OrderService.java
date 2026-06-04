package com.tzmall.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tzmall.api.order.dto.CreateOrderDTO;
import com.tzmall.api.order.dto.OrderDTO;
import com.tzmall.api.order.dto.PayOrderDTO;
import com.tzmall.api.order.dto.PayOrderResultDTO;
import com.tzmall.common.core.result.Result;
import com.tzmall.order.entity.Order;

public interface OrderService extends IService<Order> {
    Result<OrderDTO> createOrder(CreateOrderDTO createOrderDTO);

    Result<OrderDTO> getOrderById(Long id);

    Result<OrderDTO> getOrderByOrderNo(String orderNo);

    Result<IPage<OrderDTO>> listUserOrders(Long userId, int page, int size);

    Result<IPage<OrderDTO>> listAllOrders(int page, int size);

    Result<Void> cancelOrder(Long orderId);

    Result<Void> updateOrderStatus(Long orderId, Integer status);

    Result<PayOrderResultDTO> payOrder(PayOrderDTO payOrderDTO);
}
