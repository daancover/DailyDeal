package com.android.dailydeal.basics;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Daniel on 17/04/2017.
 */

public class Place implements Parcelable {
    private String name;
    private String address;
    private double lat;
    private double lon;
    private String placeId;

    public Place() {
    }

    public Place(String name, String address, double lat, double lon, String placeId) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.placeId =  placeId;
    }

    public Place(JSONObject result) {
        try {
            this.name = result.getString("name");
            this.address = result.getString("vicinity");
            this.lat = result.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            this.lon = result.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            this.placeId = result.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    protected Place(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        placeId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(placeId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
