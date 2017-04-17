package com.android.dailydeal.utils;

import android.util.Log;

import com.android.dailydeal.callbacks.CurrentPlaceListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Daniel on 11/04/2017.
 */

public class LocationUtils {
    private static final String TAG = LocationUtils.class.getName();

    public static void getCurrentPlace(GoogleApiClient googleApiClient, final CurrentPlaceListener placeResponse) {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g", placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                }

                placeResponse.onCurrentPlaceResponse(likelyPlaces);
                likelyPlaces.release();
            }
        });
    }
}
