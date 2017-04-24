package com.android.dailydeal.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Place;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnNearbyGroceryAndSupermarketListener;
import com.android.dailydeal.fragments.AddDealFragment;
import com.android.dailydeal.utils.ActivityUtils;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.LocationUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddDealActivity extends BaseActivity implements OnNearbyGroceryAndSupermarketListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = AddDealActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            LoginUtils.goToLogin(this);
            finish();
        }

        setContentView(R.layout.activity_add_deal);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        super.onCurrentPlaceResponse(response);

        getNearbyGroceryOrSupermarkets();
    }

    @Override
    public void onNearbyGroceryAndSupermarketListenerResponse(ArrayList<Place> response) {
        hideProgressDialog();
        AddDealFragment fragment = AddDealFragment.newInstance(response);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.container_add_new_deal);
    }

    private void getNearbyGroceryOrSupermarkets() {
        if (ActivityCompat.checkSelfPermission(AddDealActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddDealActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mHasValidLatLng) {
            showProgressDialog();
            LocationUtils.getNearbyGroceryOrSupermarkets(AddDealActivity.this, mLatitude, mLongitude);
        } else {
            showLocationErrorDialog();
        }
    }

    public void postDeal(String[] treeAddress, Product product) {
        DatabaseUtils.postDeal(treeAddress, product);
    }
}
