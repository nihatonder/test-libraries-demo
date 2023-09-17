package com.nonder.ecomflowtesting.service;

import com.nonder.ecomflowtesting.client.InventoryServiceClient;
import com.nonder.ecomflowtesting.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public Order createOrder(Order order) {
        boolean inventoryAvailable = checkInventory(order);

        if (inventoryAvailable) {
            placeOrderInQueue(order);
            return order;
        } else {
            throw new RuntimeException("Order cannot be placed due to insufficient inventory.");
        }
    }

    private boolean checkInventory(Order order) {
        return inventoryServiceClient.checkInventory(order);
    }

    private void placeOrderInQueue(Order order) {
        rabbitTemplate.convertAndSend(orderQueueName, order);
    }
}
