package com.example.amitnsky.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.amitnsky.popularmovies.R;
import com.example.amitnsky.popularmovies.model.ThumbnailsResponse;
import com.example.amitnsky.popularmovies.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends ArrayAdapter<ThumbnailsResponse.Thumbnail> {
    private List<ThumbnailsResponse.Thumbnail> mList;

    public GridAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rootView = convertView;
        if (rootView == null)
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail, parent, false);

        ImageView poster_iv = rootView.findViewById(R.id.thumbnail_IV);

        Log.d("Thumbnails Url : ", QueryUtils.getPosterUrl(mList.get(position).getmThumbnailUrl()));
        Picasso.with(parent.getContext())
                .load(QueryUtils.getPosterUrl(mList.get(position).getmThumbnailUrl()))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(poster_iv);
        return rootView;
    }

    @Override
    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public void clear() {
        if (mList != null)
            mList.clear();
        notifyDataSetInvalidated();
    }

    public void setData(List<ThumbnailsResponse.Thumbnail> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public ThumbnailsResponse.Thumbnail getItem(int position) {
        return mList.get(position);
    }
}
