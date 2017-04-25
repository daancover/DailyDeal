package com.android.dailydeal.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Place;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnNearbyGroceryAndSupermarketListener;
import com.android.dailydeal.fragments.AddDealFragment;
import com.android.dailydeal.utils.ActivityUtils;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.android.dailydeal.utils.NetworkUtils;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddDealActivity extends BaseActivity implements OnNearbyGroceryAndSupermarketListener, LoaderManager.LoaderCallbacks<String> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = AddDealActivity.class.getName();
    private static final int NEARBY_LOADER = 4;
    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            LoginUtils.goToLogin(this);
        }

        setContentView(R.layout.activity_add_deal);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition exitTrans = new Slide();
            getWindow().setExitTransition(exitTrans);

            Transition reenterTrans = new Slide();
            getWindow().setReenterTransition(reenterTrans);
        }

        overridePendingTransition(R.anim.animation_enter, R.anim.animation_add_deal_leave);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHasValidLatLng = false;

        if (getIntent() != null) {
            if (getIntent().hasExtra(INTENT_VALID_LOCATION)) {
                mHasValidLatLng = getIntent().getBooleanExtra(INTENT_VALID_LOCATION, false);
                mLatitude = getIntent().getDoubleExtra(INTENT_LATITUDE, 0);
                mLongitude = getIntent().getDoubleExtra(INTENT_LONGITUDE, 0);
            }
        }

        if (mHasValidLatLng) {
            getNearbyGroceryOrSupermarkets();
        } else {
            getCurrentPlace();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        super.onCurrentPlaceResponse(response);

        if (response.getStatus().getStatusMessage() != "ERROR" && response.getStatus().getStatusMessage() != "NETWORK_ERROR") {
            getNearbyGroceryOrSupermarkets();
        } else if (response.getStatus().getStatusMessage() == "NETWORK_ERROR") {
            showNetworkErrorDialog();
        } else {
            showLocationErrorDialog();
        }
    }

    @Override
    public void onNearbyGroceryAndSupermarketListenerResponse(ArrayList<Place> response) {
        hideProgressDialog();
        AddDealFragment fragment = AddDealFragment.newInstance(response);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.container_add_new_deal);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (args == null) {
                    return;
                }

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                double lat = args.getDouble(ARG_LATITUDE);
                double lon = args.getDouble(ARG_LONGITUDE);

                String response = "";

                try {
                    response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(lat, lon));
                } catch (IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNetworkErrorDialog();
                        }
                    });
                }

                return response;
            }

            @Override
            public void onCanceled(String data) {
                super.onCanceled(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray jsonArray = object.getJSONArray("results");

            ArrayList<Place> places = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject placeResult = jsonArray.getJSONObject(i);
                places.add(new Place(placeResult));
            }

            onNearbyGroceryAndSupermarketListenerResponse(places);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void getNearbyGroceryOrSupermarkets() {
        if (ActivityCompat.checkSelfPermission(AddDealActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddDealActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mHasValidLatLng) {
            showProgressDialog();
            Bundle bundle = new Bundle();
            bundle.putDouble(ARG_LATITUDE, mLatitude);
            bundle.putDouble(ARG_LONGITUDE, mLongitude);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> loader = loaderManager.getLoader(NEARBY_LOADER);

            if (loader == null) {
                loaderManager.initLoader(NEARBY_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(NEARBY_LOADER, bundle, this);
            }
        } else {
            showLocationErrorDialog();
        }
    }

    public void postDeal(String[] treeAddress, Product product) {
        DatabaseUtils.postDeal(treeAddress, product);
    }
}
