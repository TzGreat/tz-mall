package com.tzmall.common.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * <p>
 * 配置消息序列化器为 JSON 格式，默认为 JDK 序列化。
 * </p>
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 使用 JSON 序列化消息
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
