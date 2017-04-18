package com.android.dailydeal.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.dailydeal.callbacks.CurrentPlaceListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

/**
 * Created by Daniel on 11/04/2017.
 */

public class LocationUtils {
    private static final String TAG = LocationUtils.class.getName();

    public static void getCurrentPlace(GoogleApiClient googleApiClient, final CurrentPlaceListener placeResponse) {
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

    public static void getNearbyGroceryOrSupermarkets(final double lat, final double lon) {
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

                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(response).getAsJsonObject();

                Log.e(TAG, object.toString());
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
