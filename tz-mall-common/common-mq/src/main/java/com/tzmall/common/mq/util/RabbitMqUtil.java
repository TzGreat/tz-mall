package com.tzmall.common.mq.util;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 工具类
 * <p>
 * 封装常用的 RabbitMQ 操作，简化业务代码调用。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class RabbitMqUtil {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定交换机
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息内容
     */
    public void send(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    /**
     * 发送消息到默认交换机
     *
     * @param routingKey 路由键（队列名称）
     * @param message    消息内容
     */
    public void send(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(routingKey, message);
    }

    /**
     * 发送延迟消息（使用延迟队列 TTL 方式）
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息内容
     */
    public void sendDelayMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    /**
     * 发送带过期时间的消息
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息内容
     * @param expiration 过期时间（毫秒）
     */
    public void sendWithExpiration(String exchange, String routingKey, Object message, String expiration) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
            msg.getMessageProperties().setExpiration(expiration);
            return msg;
        });
    }

    /**
     * 接收消息
     *
     * @param queueName 队列名称
     * @return 消息对象
     */
    public Object receive(String queueName) {
        return rabbitTemplate.receiveAndConvert(queueName);
    }

    /**
     * 接收消息并转换为指定类型
     *
     * @param queueName 队列名称
     * @param clazz     目标类型
     * @return 消息对象
     */
    public <T> T receive(String queueName, Class<T> clazz) {
        Object message = rabbitTemplate.receiveAndConvert(queueName);
        if (message == null) {
            return null;
        }
        return clazz.cast(message);
    }

    // ==================== 订单延迟队列专用方法 ====================

    /**
     * 发送订单延迟消息（30分钟后过期）
     *
     * @param orderNo 订单号
     */
    public void sendOrderDelayMessage(String orderNo) {
        rabbitTemplate.convertAndSend("order.exchange", "order.delay", orderNo);
    }

    /**
     * 发送订单取消消息
     *
     * @param orderNo 订单号
     */
    public void sendOrderCancelMessage(String orderNo) {
        rabbitTemplate.convertAndSend("order.dlx.exchange", "order.cancel", orderNo);
    }
}
