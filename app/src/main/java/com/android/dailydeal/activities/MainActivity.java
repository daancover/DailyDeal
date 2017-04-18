package com.android.dailydeal.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Place;
import com.android.dailydeal.callbacks.CurrentPlaceListener;
import com.android.dailydeal.fragments.AddDealFragment;
import com.android.dailydeal.fragments.AddDealFragment.OnListFragmentInteractionListener;
import com.android.dailydeal.fragments.ProductListFragment;
import com.android.dailydeal.utils.ActivityUtils;
import com.android.dailydeal.utils.LocationUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener, CurrentPlaceListener, OnListFragmentInteractionListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private static final String TAG = LoginActivity.class.getName();
    private static final String DATABASE_DEALS = "deals";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDealsDatabaseReference;
    private double mLongitude;
    private double mLatitude;

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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                LocationUtils.getNearbyGroceryOrSupermarkets(mLatitude, mLongitude);
                ActivityUtils.replaceFragmentToActivityWithBackStack(getSupportFragmentManager(), new AddDealFragment(), R.id.container_main);
            }
        });

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), new ProductListFragment(), R.id.container_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            LocationUtils.getCurrentPlace(mGoogleApiClient, this);
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDealsDatabaseReference = mFirebaseDatabase.getReference().child(DATABASE_DEALS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.getCurrentPlace(mGoogleApiClient, this);
                }
            }
        }
    }

    public void setupAddDeal() {
        fab.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setupProductList() {
        fab.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.log_out) {
            //TODO confirm dialog
            LoginUtils.signOut(this);
            return true;
        } else if (id == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        mLatitude = response.get(0).getPlace().getLatLng().latitude;
        mLongitude = response.get(0).getPlace().getLatLng().longitude;
    }

    @Override
    public void onListFragmentInteraction(Place item) {

    }
}
