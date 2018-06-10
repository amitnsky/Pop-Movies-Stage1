package com.example.amitnsky.popularmovies.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.amitnsky.popularmovies.AppController;
import com.example.amitnsky.popularmovies.R;
import com.example.amitnsky.popularmovies.adapter.GridAdapter;
import com.example.amitnsky.popularmovies.utilities.JsonUtils;
import com.example.amitnsky.popularmovies.utilities.QueryUtils;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    public static final int SORT_BY_POPULAR = 0;
    public static final int SORT_BY_RATING = 1;
    public static final int SORT_BY_DEFAULT = SORT_BY_POPULAR;
    public static final String MOVIE_ID = "movies_id";
    int current_checked_item;

    private static final String LIST_STATE = "listState";
    private Parcelable mGridState = null;
    private GridView mGridView;
    private GridAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = findViewById(R.id.movie_gv);
        mAdapter = new GridAdapter(this, R.layout.thumbnail);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(MOVIE_ID, mAdapter.getItem(position).getmId());
                startActivity(intent);
            }
        });

        //handle retry button clicks
        findViewById(R.id.retry_load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartLoading();
            }
        });
        performThumbnailsQuery();
        //getLoaderManager().initLoader(THUMBNAILS_LOADER_ID, null, this).forceLoad();
    }

    /**
     * method triggers upon selection of sort menu
     * shows sort options and save changes in a private sharedPreference
     */
    private void showSortDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sort_pref_main_name), MODE_PRIVATE);
        String sorted_by = sharedPreferences.getString(getString(R.string.key_sort_by_posters), getString(R.string.default_sort_by_value));
        final int checked;
        if (getString(R.string.sort_by_popular_value).equals(sorted_by)) {
            checked = SORT_BY_POPULAR;
        } else {
            checked = SORT_BY_RATING;
        }
        builder.setTitle(R.string.sort)
                .setSingleChoiceItems(R.array.sort_by_labels, checked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which_choice) {
                        current_checked_item = which_choice;
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checked != current_checked_item)
                            savePrefAndReload(dialog);
                        else
                            Toast.makeText(MainActivity.this, R.string.canceled_msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, R.string.canceled_msg, Toast.LENGTH_SHORT).show();
                    }
                });

        builder.create().show();
    }

    /**
     * helper method to showSortDialog
     * also it saves changes only if a change was made, that is if new pref is diff from earlier pref.
     *
     * @param dialog the sort dialog shown
     */
    private void savePrefAndReload(DialogInterface dialog) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sort_pref_main_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String sort_order = getCurrentCheckedItem();
        editor.putString(getString(R.string.key_sort_by_posters), sort_order);
        editor.apply();
        dialog.dismiss();
        Toast.makeText(MainActivity.this, R.string.saved_msg, Toast.LENGTH_SHORT).show();
        mAdapter.clear();
        reStartLoading();
    }

    private String getCurrentCheckedItem() {
        String sort_order;
        switch (current_checked_item) {
            case SORT_BY_POPULAR:
                sort_order = getString(R.string.sort_by_popular_value);
                break;
            case SORT_BY_RATING:
                sort_order = getString(R.string.sort_by_rating_value);
                break;
            default:
                sort_order = getString(R.string.default_sort_by_value);
        }
        return sort_order;
    }

    //helper method for properly handling loading with other veiws,
    private void reStartLoading() {
        AppController.getInstance().showLoadingStarted(this);
        performThumbnailsQuery();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGridState = savedInstanceState.getParcelable(LIST_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mGridState = mGridView.onSaveInstanceState();
        outState.putParcelable(LIST_STATE, mGridState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_main_menu:
                showSortDialog();
                return true;
        }
        return false;
    }

    /**
     * use volley to perform networking
     * this method make network call for json of list of movies
     * and then parse using Utility method of JsonUtils and finally set to adapter.
     */
    private void performThumbnailsQuery() {
        Context context = this;
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.sort_pref_main_name),
                Context.MODE_PRIVATE);

        String sort_order = preferences.getString(context.getString(R.string.key_sort_by_posters),
                context.getString(R.string.default_sort_by_value));

        String url = QueryUtils.getQueryUrl(sort_order);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, response);
                AppController.getInstance().showLoadingStopped(MainActivity.this);
                mAdapter.setData(JsonUtils.extractThumbnails((response)));
                if (mGridState != null)
                    mGridView.onRestoreInstanceState(mGridState);
                mGridState = null;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getInstance().showLoadFailed(MainActivity.this);
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, LOG_TAG);
        AppController.getInstance().showLoadingStarted(MainActivity.this);
    }

}
