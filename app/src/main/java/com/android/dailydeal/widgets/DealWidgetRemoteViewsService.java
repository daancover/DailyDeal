package com.android.dailydeal.widgets;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.dailydeal.R;
import com.android.dailydeal.data.DealContract;
import com.android.dailydeal.utils.TextUtils;

/**
 * Created by Daniel on 25/04/2017.
 */

public class DealWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(DealContract.DealEntry.CONTENT_URI, null, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                data.moveToPosition(position);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item_deal);
                views.setTextViewText(R.id.tv_product, data.getString(1));
                views.setTextViewText(R.id.tv_place, data.getString(2));
                views.setTextViewText(R.id.tv_address, data.getString(3));
                views.setTextViewText(R.id.tv_price, TextUtils.currencyFormat(data.getDouble(5)));
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item_deal);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getInt(0);
                }

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
