package com.android.dailydeal.basics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 10/04/2017.
 */

public class Product implements Parcelable {
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

    protected Product(Parcel in) {
        product = in.readString();
        oldPrice = in.readDouble();
        newPrice = in.readDouble();
        place = (Place) in.readValue(Place.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
        dest.writeValue(place);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
