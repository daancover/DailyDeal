package com.android.dailydeal.widgets;

import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Product;

import java.util.ArrayList;

/**
 * Created by Daniel on 25/04/2017.
 */

public class DealWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private ArrayList<Product> data;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.clear();
                }

                // TODO GET DATA?
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !(position < data.size())) {
                    return null;
                }

                Product product = data.get(position);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.item_deal);
                views.setTextViewText(R.id.tv_product, product.getProduct());
                views.setTextViewText(R.id.tv_place, product.getPlace().getName());
                views.setTextViewText(R.id.tv_address, product.getPlace().getAddress());
                views.setTextViewText(R.id.tv_price, product.getNewPrice() + "");
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.item_deal);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
