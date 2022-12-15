package com.example.WatchW.inventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.WatchW.inventory.ProductContract.ProductEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<android.database.Cursor> {


    private static final int EXISTING_PRODUCT_LOADER = 0;


    private Uri mCurrentUri;

    private EditText mNameEditText;
    private EditText mModelEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mShelfEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;

    private double price;
    private int quantity;
    private int supplierCode;

    private int step;


    private boolean mProductHasChanged = false;


    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.trash);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        Intent intent = getIntent();
        mCurrentUri = intent.getData();


        if (mCurrentUri == null) {

            setTitle(getString(R.string.add_a_new_item));


            invalidateOptionsMenu();

        } else {

            setTitle(getString(R.string.edit_an_item));


            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        mNameEditText = findViewById(R.id.edit_product_name);
        mModelEditText = findViewById(R.id.edit_product_model);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mShelfEditText = findViewById(R.id.edit_product_shelf);
        mSupplierEditText = findViewById(R.id.edit_product_supplier);
        mPhoneEditText = findViewById(R.id.edit_product_phone);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(EditorActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (mNameEditText.getText().toString().isEmpty()) {
                    mNameEditText.setError(getString(R.string.field_required));
                } else if (mModelEditText.getText().toString().isEmpty()) {
                    mModelEditText.setError(getString(R.string.field_required));
                } else {
                    saveProduct();
                    finish();
                }
            }
        });



        mNameEditText.setOnTouchListener(mTouchListener);
        mModelEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mShelfEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        step = Integer.parseInt(sharedPrefs.getString(getString(R.string.settings_step_key), "1"));



        Button minusButton = findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityString = mQuantityEditText.getText().toString().trim();
                if (!quantityString.isEmpty()) {
                    quantity = Integer.parseInt(quantityString);
                } else quantity = 0;
                if (quantity > 0 && quantity >= step) {
                    mQuantityEditText.setText(String.valueOf(quantity - step));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.not_enough_items),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button plusButton = findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityString = mQuantityEditText.getText().toString().trim();
                if (!quantityString.isEmpty()) {
                    quantity = Integer.parseInt(quantityString);
                } else quantity = 0;
                if (quantity >= 0) {
                    mQuantityEditText.setText(String.valueOf(quantity + step));
                }
            }
        });

        ImageView callSupplier = findViewById(R.id.call_supplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneString = mPhoneEditText.getText().toString().trim();
                if (!phoneString.isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneString));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

    }


    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        if (!priceString.isEmpty()) {
            price = Double.parseDouble(priceString);
        }
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (!quantityString.isEmpty()) {
            quantity = Integer.parseInt(quantityString);
        }
        String shelfString = mShelfEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        if (!supplierString.isEmpty()) {
            supplierCode = Integer.parseInt(supplierString);
        }
        String phoneString = mPhoneEditText.getText().toString().trim();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String datestampString = sdf.format(new Date());


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_MODEL, modelString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SHELF, shelfString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierCode);
        values.put(ProductEntry.COLUMN_PRODUCT_PHONE, phoneString);
        values.put(ProductEntry.COLUMN_PRODUCT_DATESTAMP, datestampString);


        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);


            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
             Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);


            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {


        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }


        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };


        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trash:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

           showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SHELF,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PHONE};

        return new CursorLoader(
                this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int modelColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int shelfColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SHELF);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PHONE);


            String currentName = cursor.getString(nameColumnIndex);
            String currentModel = cursor.getString(modelColumnIndex);
            double currentPrice = cursor.getDouble(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentShelf = cursor.getString(shelfColumnIndex);
            int currentSupplier = cursor.getInt(supplierColumnIndex);
            String currentPhone = cursor.getString(phoneColumnIndex);


            mNameEditText.setText(currentName);
            mModelEditText.setText(currentModel);
            mPriceEditText.setText(String.valueOf(currentPrice));
            mQuantityEditText.setText(String.valueOf(currentQuantity));
            mShelfEditText.setText(currentShelf);
            mSupplierEditText.setText(String.valueOf(currentSupplier));
            mPhoneEditText.setText(currentPhone);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mModelEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mShelfEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {

        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);


            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


        finish();
    }
}
