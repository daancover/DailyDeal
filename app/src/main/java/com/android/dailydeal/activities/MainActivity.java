package com.android.dailydeal.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnCurrentPlaceListener;
import com.android.dailydeal.callbacks.OnGetDealsListener;
import com.android.dailydeal.callbacks.OnNearbyGroceryAndSupermarketListener;
import com.android.dailydeal.callbacks.OnPlaceDetailsListener;
import com.android.dailydeal.fragments.AddDealFragment;
import com.android.dailydeal.fragments.ProductListFragment;
import com.android.dailydeal.utils.ActivityUtils;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.DialogUtils;
import com.android.dailydeal.utils.LocationUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener, OnCurrentPlaceListener, OnNearbyGroceryAndSupermarketListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private static final String TAG = LoginActivity.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private boolean mHasValidLatLng;
    private double mLatitude;
    private double mLongitude;
    private ProgressDialog mProgressDialog;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (mHasValidLatLng) {
                    showProgressDialog();
                    LocationUtils.getNearbyGroceryOrSupermarkets(MainActivity.this, mLatitude, mLongitude);
                } else {
                    showLocationErrorDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                getCurrentPlace();
            }
        }
    }

    private void getCurrentPlace() {
        showProgressDialog();
        LocationUtils.getCurrentPlace(mGoogleApiClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentPlace();
                }
            }
        }
    }

    @Override
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        try {
            mLatitude = response.get(0).getPlace().getLatLng().latitude;
            mLongitude = response.get(0).getPlace().getLatLng().longitude;
            mHasValidLatLng = true;

            ProductListFragment fragment = ProductListFragment.newInstance(response.get(0).getPlace().getId());
            ActivityUtils.replaceFragmentToActivity(getSupportFragmentManager(), fragment, R.id.container_main);
        } catch (Exception e) {
            hideProgressDialog();
            showLocationErrorDialog();
            mHasValidLatLng = false;
            e.printStackTrace();
        }
    }

    @Override
    public void onNearbyGroceryAndSupermarketListenerResponse(ArrayList<Place> response) {
        hideProgressDialog();
        AddDealFragment fragment = AddDealFragment.newInstance(response);
        ActivityUtils.replaceFragmentToActivityWithBackStack(getSupportFragmentManager(), fragment, R.id.container_main);
    }

    private void showLocationErrorDialog() {
        DialogUtils.showDialog(this, getString(R.string.title_attention), getString(R.string.label_retrieve_current_location), true, getString(R.string.action_retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCurrentPlace();
            }
        });
    }

    public void setupAddDeal() {
        fab.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setupProductList() {
        fab.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);

            mProgressDialog.setMessage(getString(R.string.label_please_wait));
            mProgressDialog.setCancelable(false);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
            DialogUtils.showDialog(this, getString(R.string.title_attention), getString(R.string.label_log_off), true, getString(R.string.action_yes), getString(R.string.action_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginUtils.signOut(MainActivity.this);
                    finish();
                }
            }, null);

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

    public void postDeal(String[] treeAddress, Product product) {
        DatabaseUtils.postDeal(treeAddress, product);
    }
}
