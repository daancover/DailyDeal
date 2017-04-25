package com.android.dailydeal.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.dailydeal.R;
import com.android.dailydeal.callbacks.OnCurrentPlaceListener;
import com.android.dailydeal.utils.DialogUtils;
import com.android.dailydeal.utils.LocationUtils;
import com.android.dailydeal.utils.LoginUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Daniel on 24/04/2017.
 */

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnCurrentPlaceListener {
    protected static final int PERMISSION_REQUEST_CODE = 1;
    protected static final String INTENT_VALID_LOCATION = "intent_valid_location";
    protected static final String INTENT_LATITUDE = "intent_latitude";
    protected static final String INTENT_LONGITUDE = "intent_longitude";

    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    protected boolean mHasValidLatLng;
    protected double mLatitude;
    protected double mLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCurrentPlaceResponse(PlaceLikelihoodBuffer response) {
        try {
            mLatitude = response.get(0).getPlace().getLatLng().latitude;
            mLongitude = response.get(0).getPlace().getLatLng().longitude;
            mHasValidLatLng = true;
        } catch (Exception e) {
            mHasValidLatLng = false;
            e.printStackTrace();
        }
    }

    public void getCurrentPlace() {
        showProgressDialog();
        LocationUtils.getCurrentPlace(mGoogleApiClient, this);
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

    public void showLocationErrorDialog() {
        hideProgressDialog();

        DialogUtils.showDialog(this, getString(R.string.title_attention), getString(R.string.label_retrieve_current_location), true, getString(R.string.action_retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCurrentPlace();
            }
        });
    }

    public void showNetworkErrorDialog() {
        hideProgressDialog();

        DialogUtils.showDialog(this, getString(R.string.title_attention), getString(R.string.label_no_network), true, getString(R.string.action_retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCurrentPlace();
            }
        });
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
                    LoginUtils.signOut(BaseActivity.this);
                }
            }, null);

            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
