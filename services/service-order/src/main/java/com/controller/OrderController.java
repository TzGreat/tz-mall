package com.controller;

import com.example.Order.been.Order;
import com.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/create")
    public Order create(@RequestParam("productId") Long productId, @RequestParam("userId") Long userId){
        Order order=orderService.create(productId, userId);
        return order;
    }

    @RequestMapping("/seckill")
    public Order seckill(@RequestParam("productId") Long productId, @RequestParam("userId") Long userId){
        Order order=orderService.create(productId, userId);
        return order;
    }

}
