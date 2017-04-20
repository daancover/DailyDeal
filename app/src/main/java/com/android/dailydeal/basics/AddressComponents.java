package com.android.dailydeal.basics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Daniel on 20/04/2017.
 */

public class AddressComponents {
    private String longName;
    private String shortName;
    private ArrayList<String> types;

    public AddressComponents() {

    }

    public AddressComponents(String longName, String shortName, ArrayList<String> types) {
        this.longName = longName;
        this.shortName = shortName;
        this.types = types;
    }

    public AddressComponents(JSONObject object) {
        try {
            this.longName = object.getString("long_name");
            this.shortName = object.getString("short_name");

            JSONArray jsonArray = object.getJSONArray("types");

            ArrayList<String> tmp = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                String type = jsonArray.getString(i);
                tmp.add(type);
            }

            this.types = tmp;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public boolean isCountry() {
        return (types.contains("country"));
    }

    public boolean isState() {
        return (types.contains("administrative_area_level_1"));
    }

    public boolean isCity() {
        return (types.contains("administrative_area_level_2"));
    }
}
