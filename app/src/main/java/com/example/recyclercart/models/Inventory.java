package com.example.recyclercart.models;

import java.util.List;

public class Inventory {
    public List<Product> products;

    public Inventory() {
    }

    public Inventory(List<Product> products) {
        this.products = products;
    }
}
