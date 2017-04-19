package com.android.dailydeal.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;

/**
 * Created by Daniel on 19/04/2017.
 */

public class DialogUtils {

    private static AlertDialog alertDialog;

    public static final void showDialog(Activity activity, final String title, final String msg, final String positiveButton) {
        showDialog(activity, -1, title, msg, true, positiveButton, null, null, null);
    }

    public static final void showDialog(Activity activity, final int icon, final String title, final String msg, final String positiveButton) {
        showDialog(activity, icon, title, msg, true, positiveButton, null, null, null);
    }

    public static final void showDialog(Activity activity, final String title, final String msg, final boolean setCancelable, final String positiveButton, final DialogInterface.OnClickListener positiveClick) {
        showDialog(activity, -1, title, msg, setCancelable, positiveButton, null, positiveClick, null);
    }

    public static final void showDialog(Activity activity, final String title, final String msg, final boolean setCancelable, final String positiveButton, final String negativeButton, final DialogInterface.OnClickListener positiveClick, final DialogInterface.OnClickListener negativeClick) {
        showDialog(activity, -1, title, msg, setCancelable, positiveButton, negativeButton, positiveClick, negativeClick);
    }

    public static final void showDialog(Activity activity, final int icon, final String title, final String msg, final boolean setCancelable, final String positiveButton, final DialogInterface.OnClickListener positiveClick) {
        showDialog(activity, icon, title, msg, setCancelable, positiveButton, null, positiveClick, null);
    }

    public static final void showDialog(Activity activity, final int icon, final String title, final String msg, final boolean setCancelable, final String positiveButton, final String negativeButton, final DialogInterface.OnClickListener positiveClick, final DialogInterface.OnClickListener negativeClick) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) {
            builder.setTitle(title);
        } else {
            builder.setTitle("");
        }

        if (msg != null) {
            builder.setMessage(Html.fromHtml(msg));
        }

        if (positiveButton != null) {
            builder.setPositiveButton(positiveButton, positiveClick);
        }

        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton, negativeClick);
        }

        alertDialog = builder.create();

        if (icon > 0) {
            alertDialog.setIcon(activity.getResources().getDrawable(icon));
        }

        if (setCancelable) {
            alertDialog.setCancelable(true);
        } else {
            alertDialog.setCancelable(false);
        }

        if (activity != null && !activity.isFinishing()) {
            alertDialog.show();
        }
    }
}
