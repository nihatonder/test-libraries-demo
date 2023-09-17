package com.nonder.ecomflowtesting.controller;

import com.nonder.ecomflowtesting.model.Order;
import com.nonder.ecomflowtesting.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
}
