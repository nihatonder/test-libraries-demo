package com.nonder.ecomflowtesting.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {

    private int id;
    private String itemName;
    private int quantity;
}
