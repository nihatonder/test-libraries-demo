package com.nonder.ecomflowtesting.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {

    private Long id;
    private String itemName;
    private int quantity;
}
