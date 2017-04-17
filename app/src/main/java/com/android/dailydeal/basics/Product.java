package com.android.dailydeal.basics;

/**
 * Created by Daniel on 10/04/2017.
 */

public class Product {
    private String name;
    private String address;
    private double oldPrice;
    private double newPrice;

    public Product() {
    }

    public Product(String name, String address, double oldPrice, double newPrice) {
        this.name = name;
        this.address = address;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}
