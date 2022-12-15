package com.example.WatchW.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.WatchW.inventory.ProductContract.ProductEntry;



class ProductDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "warehouse.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_MODEL + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.00, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SHELF + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " INTEGER NOT NULL DEFAULT 000, "
                + ProductEntry.COLUMN_PRODUCT_PHONE + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_DATESTAMP + " TEXT NOT NULL);";


        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
