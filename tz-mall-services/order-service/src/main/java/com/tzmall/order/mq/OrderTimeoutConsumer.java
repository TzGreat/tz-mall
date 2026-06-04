package com.tzmall.order.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzmall.order.entity.Order;
import com.tzmall.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutConsumer {

    private final OrderMapper orderMapper;

    @RabbitListener(queues = "order.delay.queue")
    public void handleOrderTimeout(String orderNo) {
        log.info("收到订单超时消息，订单号={}", orderNo);

        try {
            Long orderNoLong = Long.parseLong(orderNo);
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderNo, orderNoLong);
            List<Order> orders = orderMapper.selectList(wrapper);

            if (orders.isEmpty()) {
                log.warn("未找到订单，订单号={}", orderNo);
                return;
            }

            Order order = orders.get(0);
            if (order.getStatus() == 0) {
                order.setStatus(5);
                order.setUpdateTime(java.time.LocalDateTime.now());
                orderMapper.updateById(order);
                log.info("订单超时已自动取消，订单号={}", orderNo);
            } else {
                log.info("订单状态不是待支付，不处理，订单号={}，状态={}", orderNo, order.getStatus());
            }
        } catch (Exception e) {
            log.error("处理订单超时异常，订单号={}", orderNo, e);
        }
    }
}
