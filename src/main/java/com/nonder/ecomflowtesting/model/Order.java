package com.nonder.ecomflowtesting.model;

import lombok.Data;

@Data
public class Order {

    private Long id;
    private String itemName;
    private int quantity;
}
