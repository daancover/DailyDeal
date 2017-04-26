package com.android.dailydeal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.dailydeal.data.DealContract.DealEntry;

/**
 * Created by Daniel on 25/04/2017.
 */

public class DealDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dealsDb.db";

    private static final int VERSION = 1;

    DealDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + DealEntry.TABLE_NAME + " (" +
                DealEntry._ID + " INTEGER PRIMARY KEY, " +
                DealEntry.COLUMN_PRODUCT + " TEXT NOT NULL, " +
                DealEntry.COLUMN_PLACE + " TEXT NOT NULL, " +
                DealEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                DealEntry.COLUMN_OLD_PRICE + " REAL NOT NULL, " +
                DealEntry.COLUMN_NEW_PRICE + " REAL NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DealEntry.TABLE_NAME);
        onCreate(db);
    }
}
