package com.android.dailydeal.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by Daniel on 10/04/2017.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId, Bundle bundle) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void replaceFragmentToActivityWithBackStack(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(fragment.getClass().toString());
        transaction.commit();
    }

    public static void replaceFragmentToActivityWithBackStack(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId, Bundle bundle) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(fragment.getClass().toString());
        transaction.commit();
    }
}
