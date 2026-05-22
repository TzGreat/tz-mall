package com.service;

import com.example.Order.been.Order;

public interface OrderService {
    Order create(Long productId, Long userId);
}
