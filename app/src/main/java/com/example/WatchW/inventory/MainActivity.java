package com.example.WatchW.inventory;

import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.WatchW.inventory.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private static final double MARGIN_DIVISOR = 18.6363636;
    private ProductCursorAdapter mAdapter;
    private TextView mSearchTextView;
    private ListView warehouseItems;
    private ImageView animImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        animImage = findViewById(R.id.animation_icon);
        animImage.setVisibility(View.GONE);




        mSearchTextView = findViewById(R.id.search_instructions);
        mSearchTextView.setVisibility(View.GONE);


        warehouseItems = findViewById(R.id.warehouse_listview);


        final BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.main_nav);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editor_nav:
                        Intent open_editor = new Intent(MainActivity.this, EditorActivity.class);
                        startActivity(open_editor);
                        break;
                    case R.id.search_nav:

                        getSupportActionBar().show();
                        mSearchTextView.setVisibility(View.VISIBLE);
                        mSearchTextView.setText(R.string.search_instructions);
                        setTitle(getString(R.string.search));

                        View emptyView = findViewById(R.id.invisible_view);
                        warehouseItems.setEmptyView(emptyView);
                        break;
                    case R.id.main_nav:

                        getSupportActionBar().hide();
                        mSearchTextView.setVisibility(View.GONE);
                        setupAdapter();
                        break;


                }
                return true;
            }
        });


        setupAdapter();


        View emptyView = findViewById(R.id.empty);
        warehouseItems.setEmptyView(emptyView);

        warehouseItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });


        handleIntent(getIntent());
    }

    private void setupAdapter() {

        mAdapter = new ProductCursorAdapter(this, null);

        warehouseItems.setAdapter(mAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
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
                ProductEntry.COLUMN_PRODUCT_DATESTAMP};

        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        if (mAdapter.isEmpty()) {
            animateEmptyView();
        } else {
            animImage.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            Intent i = new Intent(this, EditorActivity.class);
            i.setData(intent.getData());
            startActivity(i);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String searchQuery = "%" + intent.getStringExtra(SearchManager.QUERY) + "%";
            showResults(searchQuery);
        }
    }


    private void showResults(String searchQuery) {
        Cursor searchCursor = managedQuery(
                ProductEntry.CONTENT_URI,
                new String[]{ProductEntry._ID,
                        ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductEntry.COLUMN_PRODUCT_MODEL,
                        ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY,
                        ProductEntry.COLUMN_PRODUCT_SHELF,
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                        ProductEntry.COLUMN_PRODUCT_DATESTAMP},


                ProductEntry.COLUMN_PRODUCT_NAME + " LIKE ?" +
                        " OR " + ProductEntry.COLUMN_PRODUCT_MODEL + " LIKE ?",
                new String[]{searchQuery, searchQuery},

                null);

        if (searchCursor == null) {

            mSearchTextView.setText(getString(R.string.no_results, searchQuery));

        } else {

            int count = searchCursor.getCount();
            String countString = getResources().getQuantityString(R.plurals.search_results,
                    // Use .substring to remove "%"
                    count, count, searchQuery.substring(1, searchQuery.length() - 1));
            mSearchTextView.setText(countString);


            mAdapter = new ProductCursorAdapter(this, searchCursor);


            warehouseItems.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }


    private void animateEmptyView() {


        ImageView i = findViewById(R.id.empty_view_image);
        ObjectAnimator animation = ObjectAnimator.ofFloat(i, "translationX", 300f, -200f, 0f);
        animation.setDuration(3500);
        animation.start();


        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpRatio = displayMetrics.density;

        float dpWidth = displayMetrics.widthPixels / dpRatio;

        float dpImprovedMarginLeft = (float) (dpWidth / MARGIN_DIVISOR);

        int pixelValue = (int) (dpImprovedMarginLeft * dpRatio);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) animImage.getLayoutParams();
        int bottomMargin = params.bottomMargin;
        params.setMargins(pixelValue, 0, 0, bottomMargin);
        animImage.setLayoutParams(params);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animImage.setVisibility(View.VISIBLE);

                animImage.setBackgroundResource(R.drawable.anim_menu);
                ((AnimationDrawable) animImage.getBackground()).start();
            }
        }, 5000);

    }
}
