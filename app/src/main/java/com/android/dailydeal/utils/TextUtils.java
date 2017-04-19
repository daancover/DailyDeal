package com.android.dailydeal.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Daniel on 09/04/2017.
 */

public class TextUtils {
    public static void createPriceTextView(TextView textView, String oldPrice, String newPrice) {
        String currencyOld = currencyFormat(Double.parseDouble(oldPrice));
        String currencyNew = currencyFormat(Double.parseDouble(newPrice));
        String text = currencyOld + " - " + currencyNew;
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        StrikethroughSpan span = new StrikethroughSpan();
        spannable.setSpan(span, 0, currencyOld.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    public static String currencyFormat(Double num) {
        NumberFormat numFormat = NumberFormat.getCurrencyInstance();
        return numFormat.format(num);
    }

    public static TextWatcher insertCurrencyMask(final EditText ediTxt) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = "";

                if (!s.toString().equals(current)) {
                    String currencySymbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
                    String replaceable = String.format("[%s,.]", currencySymbol);
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    ediTxt.removeTextChangedListener(this);
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
                    ediTxt.setText(formatted);
                    ediTxt.setSelection(formatted.length());
                    ediTxt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static double getDoubleValueFromStringCurrency(String currency) {
        String currencySymbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        String replaceable = String.format("[%s,.]", currencySymbol);
        String cleanString = currency.replaceAll(replaceable, "");
        double parsed = Double.parseDouble(cleanString);
        return parsed / 100;
    }
}
