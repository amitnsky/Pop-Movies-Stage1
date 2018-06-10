package com.example.amitnsky.popularmovies;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void showLoadingStarted(Activity activity) {
        RelativeLayout layout = activity.findViewById(R.id.load_failed);
        layout.setVisibility(View.GONE);
        ProgressBar progressBar = activity.findViewById(R.id.loading_pb);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void showLoadingStopped(Activity activity) {
        RelativeLayout layout = activity.findViewById(R.id.load_failed);
        layout.setVisibility(View.GONE);
        ProgressBar progressBar = activity.findViewById(R.id.loading_pb);
        progressBar.setVisibility(View.GONE);
    }

    public void showLoadFailed(Activity activity){
        RelativeLayout layout = activity.findViewById(R.id.load_failed);
        layout.setVisibility(View.VISIBLE);
        ProgressBar progressBar = activity.findViewById(R.id.loading_pb);
        progressBar.setVisibility(View.GONE);
    }

}
