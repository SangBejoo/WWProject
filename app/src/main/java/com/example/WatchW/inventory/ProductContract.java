package com.example.WatchW.inventory;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// The contract class allows you to use the same constants across all the other classes in the same package.
// This lets you change a column name in one place and have it propagate throughout your code.

final class ProductContract {


    public static final String CONTENT_AUTHORITY = "com.example.WatchW.inventory";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_PRODUCTS = "products_table";


    private ProductContract() {
    }

    public static class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_MODEL = "product_model";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SHELF = "shelf_number";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier_code";
        public static final String COLUMN_PRODUCT_PHONE = "supplier_phone_number";
        public static final String COLUMN_PRODUCT_DATESTAMP = "datestamp";


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

    }
}


