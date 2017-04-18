package com.android.dailydeal.callbacks;

import com.android.dailydeal.basics.Place;

import java.util.List;

/**
 * Created by Daniel on 11/04/2017.
 */

public interface NearbyGroceryAndSupermarketListener {
    void onNearbyGroceryAndSupermarketListenerResponse(List<Place> response);
}
