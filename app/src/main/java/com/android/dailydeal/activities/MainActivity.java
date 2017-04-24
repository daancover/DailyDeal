package com.android.dailydeal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.fragments.ProductListFragment;
import com.android.dailydeal.utils.ActivityUtils;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private static final String TAG = MainActivity.class.getName();

    private ProductListFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            LoginUtils.goToLogin(this);
            finish();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHasValidLatLng) {
                    Intent intent = new Intent(MainActivity.this, AddDealActivity.class);
                    intent.putExtra(INTENT_VALID_LOCATION, mHasValidLatLng);
                    intent.putExtra(INTENT_LATITUDE, mLatitude);
                    intent.putExtra(INTENT_LONGITUDE, mLongitude);
                    startActivity(intent);
                } else {
                    showLocationErrorDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            getCurrentPlace();
        }
    }

    @Override
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        super.onCurrentPlaceResponse(response);

        if(response.getStatus().getStatusMessage() != "ERROR") {
            if(mFragment != null && mFragment.isAdded()) {
                mFragment.updatePrductList(response.get(0).getPlace().getId());
            } else {
                mFragment = ProductListFragment.newInstance(response.get(0).getPlace().getId());
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.container_main);
            }
        } else {
            showLocationErrorDialog();
        }
    }
}
