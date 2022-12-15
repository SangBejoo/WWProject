package com.example.WatchW.inventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.WatchW.inventory.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {


    private static final String LOG_TAG = ProductProvider.class.getSimpleName();


    private static final int PRODUCTS = 100;


    private static final int PRODUCT_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {


        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, 100);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", 101);
    }


    private ProductDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();


        Cursor cursor;

        switch (sUriMatcher.match(uri)) {


            case PRODUCTS:

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;


            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertProduct(Uri uri, ContentValues values) {


        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("PRODUCT_NAME requires a value");
        }
        String model = values.getAsString(ProductEntry.COLUMN_PRODUCT_MODEL);
        if (model == null) {
            throw new IllegalArgumentException("PRODUCT_MODEL requires a value");
        }
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("PRODUCT_PRICE requires a valid value");
        }
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("PRODUCT_QUANTITY requires a valid value");
        }
        String shelf = values.getAsString(ProductEntry.COLUMN_PRODUCT_SHELF);
        if (shelf == null) {
            throw new IllegalArgumentException("PRODUCT_SHELF requires a value");
        }
        Integer supplier = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier != null && supplier < 0) {
            throw new IllegalArgumentException("PRODUCT_SUPPLIER requires a valid value");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }


        getContext().getContentResolver().notifyChange(uri, null);


        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("PRODUCT_NAME requires a value");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_MODEL)) {
            String model = values.getAsString(ProductEntry.COLUMN_PRODUCT_MODEL);
            if (model == null) {
                throw new IllegalArgumentException("PRODUCT_MODEL requires a value");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("PRODUCT_PRICE requires a valid value");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("PRODUCT_QUANTITY requires a valid value");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SHELF)) {
            String shelf = values.getAsString(ProductEntry.COLUMN_PRODUCT_SHELF);
            if (shelf == null) {
                throw new IllegalArgumentException("PRODUCT_SHELF requires a value");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            Integer supplier = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplier != null && supplier < 0) {
                throw new IllegalArgumentException("PRODUCT_SUPPLIER requires a valid value");
            }
        }



        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:

                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}