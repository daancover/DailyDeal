package com.android.dailydeal.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.dailydeal.basics.Place;
import com.android.dailydeal.callbacks.OnCurrentPlaceListener;
import com.android.dailydeal.callbacks.OnNearbyGroceryAndSupermarketListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Daniel on 11/04/2017.
 */

public class LocationUtils {
    private static final String TAG = LocationUtils.class.getName();

    public static void getCurrentPlace(GoogleApiClient googleApiClient, final OnCurrentPlaceListener placeResponse) {
        if (ActivityCompat.checkSelfPermission((Context) placeResponse, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.e(TAG, String.format("Place '%s' has likelihood: %g", placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                }

                placeResponse.onCurrentPlaceResponse(likelyPlaces);
                likelyPlaces.release();
            }
        });
    }

    public static void getNearbyGroceryOrSupermarkets(final OnNearbyGroceryAndSupermarketListener listener, final double lat, final double lon) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";

                try {
                    response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(lat, lon));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    ArrayList<Place> places = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject placeResult = jsonArray.getJSONObject(i);
                        places.add(new Place(placeResult));
                    }

                    listener.onNearbyGroceryAndSupermarketListenerResponse(places);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // TODO
//    private Geocoder mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
//
//    private String getCityNameByCoordinates(double lat, double lon) throws IOException {
//
//        List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 1);
//        if (addresses != null && addresses.size() > 0) {
//            return addresses.get(0).getLocality();
//        }
//        return null;
//    }
}
