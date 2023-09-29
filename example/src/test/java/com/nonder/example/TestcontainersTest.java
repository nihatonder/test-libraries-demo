package com.nonder.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@Testcontainers
public class TestcontainersTest {

    @Container
    private GenericContainer rabbitMQContainer = new GenericContainer("rabbitmq:3-management")
            .withExposedPorts(5672);  // Port inside the container

    private CachingConnectionFactory connectionFactory;

    @BeforeEach
    public void init() {
        String address = rabbitMQContainer.getHost();
        Integer port = rabbitMQContainer.getFirstMappedPort();

        connectionFactory = new CachingConnectionFactory(address, port);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

        // Declare exchange and queue
        TopicExchange testExchange = new TopicExchange("test-exchange");
        Queue testQueue = new Queue("test-queue");
        Binding binding = BindingBuilder.bind(testQueue).to(testExchange).with("test-routing-key");

        rabbitAdmin.declareExchange(testExchange);
        rabbitAdmin.declareQueue(testQueue);
        rabbitAdmin.declareBinding(binding);
    }

    @Test
    void testSendMessageToRabbitMQ() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Send a message
        rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", "Test Message");

        // Receive and verify the message
        Object receivedMessage = rabbitTemplate.receiveAndConvert("test-queue");
        Assertions.assertEquals("Test Message", receivedMessage);
    }
}