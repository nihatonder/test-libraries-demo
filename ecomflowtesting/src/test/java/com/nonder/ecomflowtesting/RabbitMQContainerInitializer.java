package com.nonder.ecomflowtesting;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class RabbitMQContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.rabbitmq.host=" + OrderServiceEndToEndTest.rabbitMQContainer.getContainerIpAddress(),
                "spring.rabbitmq.port=" + OrderServiceEndToEndTest.rabbitMQContainer.getMappedPort(5672)
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
