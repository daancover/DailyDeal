package com.android.dailydeal.callbacks;

import com.android.dailydeal.basics.Product;

import java.util.ArrayList;

/**
 * Created by Daniel on 20/04/2017.
 */

public interface OnGetDealsListener {
    void onGetDealsListenerResponse(Product product);

    void onGetDealsListenerResponse(ArrayList<Product> products);
}
