package com.example.WatchW.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.WatchW.inventory.ProductContract.ProductEntry;

import java.text.DecimalFormat;



class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView name = view.findViewById(R.id.name_textview);
        TextView info = view.findViewById(R.id.info_textview);
        TextView date = view.findViewById(R.id.date_textview);

        final TextView quantity = view.findViewById(R.id.quantity_textview);
        TextView price = view.findViewById(R.id.price_textview);


        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int modelColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int shelfColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SHELF);
        int datestampColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DATESTAMP);


        String currentName = cursor.getString(nameColumnIndex);
        String currentModel = cursor.getString(modelColumnIndex);
        double currentPrice = cursor.getDouble(priceColumnIndex);
        final int currentQuantity = cursor.getInt(quantityColumnIndex);
        String currentShelf = cursor.getString(shelfColumnIndex);
        String currentDatestamp = cursor.getString(datestampColumnIndex);


        name.setText(currentName);
        info.setText(currentModel + " | " + currentShelf);
        date.setText(currentDatestamp);

        quantity.setText(String.valueOf(currentQuantity));


        DecimalFormat df = new DecimalFormat("0.00");
        price.setText("x " + String.valueOf(df.format(currentPrice)));

        Button quickSellButton = view.findViewById(R.id.button);
        quickSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Uri u = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, idColumnIndex);


                if (currentQuantity >= 1) {


                    quantity.setText(String.valueOf(currentQuantity - 1));


                    int updatedQuantity = currentQuantity - 1;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, updatedQuantity);
                    int i = context.getContentResolver().update(u, contentValues, null, null);

                } else {
                    Toast.makeText(context, context.getString(R.string.no_more_items),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}