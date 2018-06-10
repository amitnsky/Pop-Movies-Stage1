package com.example.amitnsky.popularmovies.activities;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.amitnsky.popularmovies.AppController;
import com.example.amitnsky.popularmovies.R;
import com.example.amitnsky.popularmovies.databinding.ActivityDetailBinding;
import com.example.amitnsky.popularmovies.model.Movie;
import com.example.amitnsky.popularmovies.utilities.JsonUtils;
import com.example.amitnsky.popularmovies.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private ActivityDetailBinding binding;
    private LinearLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mainContent = binding.getRoot().findViewById(R.id.detail_main_layout);
        mainContent.setVisibility(View.GONE);

        final Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.MOVIE_ID)) {
            makeNetworkCalls(intent.getStringExtra(MainActivity.MOVIE_ID));
        } else {
            showIntentErrorMsg();
        }

        //overview text has a limit of maxlines 3, so if there is more text, tap
        //on overview TextView to show more,
        //this listener handles those things, also it is assumed that overview text
        //will not be longer than 100 lines.
        (findViewById(R.id.movie_overview_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getMaxLines() == 3)
                    tv.setMaxLines(100);
                else tv.setMaxLines(3);
            }
        });

        binding.getRoot().findViewById(R.id.retry_load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNetworkCalls(intent.getStringExtra(MainActivity.MOVIE_ID));
            }
        });
    }

    private void showIntentErrorMsg() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void showMovie(Movie movie) {
        binding.setTemp(movie);
    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView imageView, String url) {
        if (url != null)
            Picasso.with(imageView.getContext())
                    .load(QueryUtils.getPosterUrl(url))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(imageView);

    }

    private void makeNetworkCalls(String id) {
        getMovie(id);
    }

    public void getMovie(String id) {
        String url = QueryUtils.getQueryUrl(id);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, response);
                AppController.getInstance().showLoadingStopped(DetailActivity.this);
                mainContent.setVisibility(View.VISIBLE);
                showMovie(JsonUtils.extractMovie((response)));
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getInstance().showLoadFailed(DetailActivity.this);
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, LOG_TAG);
        AppController.getInstance().showLoadingStarted(DetailActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
