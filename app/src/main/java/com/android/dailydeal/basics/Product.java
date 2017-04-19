package com.android.dailydeal.basics;

/**
 * Created by Daniel on 10/04/2017.
 */

public class Product {
    private String product;
    private double oldPrice;
    private double newPrice;
    private Place place;

    public Product() {
    }

    public Product(String product, double oldPrice, double newPrice, Place place) {
        this.product = product;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.place = place;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
