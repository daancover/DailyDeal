package com.android.dailydeal.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Daniel on 25/04/2017.
 */

public class DealContract {
    public static final String AUTHORITY = "com.android.dailydeal";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_DEALS = "deals";

    public static final class DealEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEALS).build();
        public static final String TABLE_NAME = "deals";

        public static final String COLUMN_PRODUCT = "product";
        public static final String COLUMN_PLACE = "place";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_OLD_PRICE = "old_price";
        public static final String COLUMN_NEW_PRICE = "new_price";
    }
}
