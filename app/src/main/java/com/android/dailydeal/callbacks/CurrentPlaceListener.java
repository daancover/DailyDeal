package com.android.dailydeal.callbacks;

import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

/**
 * Created by Daniel on 11/04/2017.
 */

public interface CurrentPlaceListener {
    void onCurrentPlaceResponse(PlaceLikelihoodBuffer response);
}
