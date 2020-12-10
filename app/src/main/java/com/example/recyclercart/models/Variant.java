package com.example.recyclercart.models;

import org.jetbrains.annotations.NotNull;

public class Variant {

    public String name;
    public int price;

    public Variant(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Variant() {
    }

    @NotNull
    @Override
    public String toString() {
        return name + " - Rs. " + price;
    }
}
