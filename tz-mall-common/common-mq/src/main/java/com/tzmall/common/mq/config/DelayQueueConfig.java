package com.tzmall.common.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列配置
 * <p>
 * 用于订单超时取消场景：
 * 1. 订单创建后发送消息到延迟队列（TTL）
 * 2. 消息过期后进入死信交换机
 * 3. 死信队列消费者处理超时订单
 * </p>
 */
@Configuration
public class DelayQueueConfig {

    // ==================== 订单超时取消队列 ====================

    /**
     * 订单延迟队列（TTL 队列）
     * 消息过期后转发到死信交换机
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("order.delay.queue")
                .ttl(30 * 60 * 1000) // 30 分钟过期
                .deadLetterExchange("order.dlx.exchange")
                .deadLetterRoutingKey("order.cancel")
                .build();
    }

    /**
     * 订单死信交换机
     */
    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange("order.dlx.exchange");
    }

    /**
     * 订单死信队列（真正处理超时订单的队列）
     */
    @Bean
    public Queue orderDlxQueue() {
        return new Queue("order.dlx.queue", true);
    }

    /**
     * 死信队列绑定死信交换机
     */
    @Bean
    public Binding orderDlxBinding() {
        return BindingBuilder.bind(orderDlxQueue())
                .to(orderDlxExchange())
                .with("order.cancel");
    }

    /**
     * 订单交换机（用于发送订单消息）
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order.exchange");
    }

    /**
     * 延迟队列绑定订单交换机
     */
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderExchange())
                .with("order.delay");
    }
}
