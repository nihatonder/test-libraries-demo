package com.nonder.example;


import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;


@SpringBootTest
@org.testcontainers.junit.jupiter.Testcontainers
public class TestcontainersTest {

    @Container
    private GenericContainer rabbitMQContainer = new GenericContainer("rabbitmq:3-management")
            .withExposedPorts(5672);  // Port inside the container

    @PostConstruct
    public void initContainer() {
        rabbitMQContainer.start();
    }

    @Test
    void testSendMessageToRabbitMQ() {
        String address = rabbitMQContainer.getHost();
        Integer port = rabbitMQContainer.getFirstMappedPort();  // Random port outside the container

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(address, port);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", "Test Message");
    }
}