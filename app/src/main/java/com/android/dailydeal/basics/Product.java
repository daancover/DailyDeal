package com.android.dailydeal.basics;

/**
 * Created by Daniel on 10/04/2017.
 */

public class Product {
    private String name;
    private String address;
    private double lat;
    private double lon;
    private double oldPrice;
    private double newPrice;

    public Product() {
    }

    public Product(String name, String address, double lat, double lon, double oldPrice, double newPrice) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
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
