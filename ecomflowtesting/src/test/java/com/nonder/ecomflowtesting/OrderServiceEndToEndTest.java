package com.nonder.ecomflowtesting;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nonder.ecomflowtesting.model.Order;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = RabbitMQContainerInitializer.class)
@TestPropertySource(properties = {
        "inventory.service.url=http://localhost:8081",
        "spring.rabbitmq.username=guest",
        "spring.rabbitmq.password=guest",})
public class OrderServiceEndToEndTest {

    @Container
    static final GenericContainer rabbitMQContainer = new GenericContainer("rabbitmq:3-management")
            .withExposedPorts(5672);
    private static WireMockServer wireMockServer;

    @Value("${order.queue.name}")
    private String orderQueueName;

    @LocalServerPort
    private int port;

    private static CachingConnectionFactory connectionFactory;

    @BeforeAll
    public static  void setUp() {
        // Connect to RabbitMQ
        String address = rabbitMQContainer.getHost();
        Integer port = rabbitMQContainer.getFirstMappedPort();

        connectionFactory = new CachingConnectionFactory(address, port);

        // Setup wireMockServer
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/inventory/check"))
                .withRequestBody(WireMock.containing("\"id\":1234"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"available\": true}")));

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/inventory/check"))
                .withRequestBody(WireMock.containing("\"id\":8888"))
                .willReturn(WireMock.aResponse()
                        .withStatus(400)  // For instance, HTTP 400 Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Invalid orderId\"}")));
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testOrderCreation_HappyPath() {
        Order testOrder = new Order();
        testOrder.setId(1234);
        testOrder.setItemName("Pencil");
        testOrder.setQuantity(2);

        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .body(testOrder)
                .contentType("application/json")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        Object receivedMessage = rabbitTemplate.receiveAndConvert(orderQueueName);
        Assertions.assertEquals(testOrder, receivedMessage);
    }

    @Test
    public void testOrderCreation_BadRequest() {
        Order testOrder = new Order();
        testOrder.setId(8888);
        testOrder.setItemName("Book");
        testOrder.setQuantity(1);

        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .body(testOrder)
                .contentType("application/json")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
}
