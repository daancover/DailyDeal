package com.android.dailydeal.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.android.dailydeal.activities.LoginActivity;
import com.android.dailydeal.widgets.AddDealWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Daniel on 09/04/2017.
 */

public class LoginUtils {
    public static final int RC_SIGN_IN = 1;

    public static void signIn(AppCompatActivity activity, GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void signOut(FragmentActivity activity) {
        AuthUI.getInstance().signOut(activity);
        goToLogin(activity);
    }

    public static void goToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
