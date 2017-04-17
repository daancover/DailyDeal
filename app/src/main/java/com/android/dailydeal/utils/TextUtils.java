package com.android.dailydeal.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

/**
 * Created by Daniel on 09/04/2017.
 */

public class TextUtils {
    public static void createPriceTextView(TextView textView, String oldPrice, String newPrice) {
        String text = oldPrice + " - " + newPrice;
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        StrikethroughSpan span = new StrikethroughSpan();
        spannable.setSpan(span, 0, oldPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}
