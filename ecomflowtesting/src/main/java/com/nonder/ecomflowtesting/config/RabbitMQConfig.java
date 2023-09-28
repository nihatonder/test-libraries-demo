package com.nonder.ecomflowtesting.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${order.queue.name}")
    private String orderQueueName;

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueueName, true);
    }
}
