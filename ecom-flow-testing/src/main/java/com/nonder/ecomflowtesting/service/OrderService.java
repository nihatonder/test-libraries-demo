package com.nonder.ecomflowtesting.service;

import com.nonder.ecomflowtesting.client.InventoryServiceClient;
import com.nonder.ecomflowtesting.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class OrderService {

    private final InventoryServiceClient inventoryServiceClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.queue.name}")
    private String orderQueueName;

    @Autowired
    public OrderService(InventoryServiceClient inventoryServiceClient, RabbitTemplate rabbitTemplate) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResponseEntity createOrder(Order order) {
        ResponseEntity response = checkInventory(order);

        if (response.getStatusCode().is2xxSuccessful()) {
            placeOrderInQueue(order);
            return ResponseEntity.created(URI.create(String.valueOf(order.getId()))).build();
        } else {
            return response;
        }
    }

    private ResponseEntity checkInventory(Order order) {
        return inventoryServiceClient.checkInventory(order.getId(), order.getQuantity());
    }

    private void placeOrderInQueue(Order order) {
        rabbitTemplate.convertAndSend(orderQueueName, order);
    }
}
