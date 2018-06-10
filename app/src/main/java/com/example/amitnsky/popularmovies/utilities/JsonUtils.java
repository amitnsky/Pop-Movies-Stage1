package com.example.amitnsky.popularmovies.utilities;

import android.text.TextUtils;

import com.example.amitnsky.popularmovies.model.Movie;
import com.example.amitnsky.popularmovies.model.ThumbnailsResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<ThumbnailsResponse.Thumbnail> extractThumbnails(String thumbnailJson) {
        if (thumbnailJson==null||TextUtils.isEmpty(thumbnailJson))
            return null;
        Gson gson = new Gson();
        Type response_obj = new TypeToken<ThumbnailsResponse>() {
        }.getType();
        ThumbnailsResponse results = gson.fromJson(thumbnailJson, response_obj);
        return  results.results;
    }

    public static Movie extractMovie(String json) {
        Gson gson = new Gson();
        Type type_movie = new TypeToken<Movie>() {
        }.getType();
        return  gson.fromJson(json, type_movie);
    }

    public static String getFormattedGenres(List<Movie.Genres> genres){
        List<String> res = new ArrayList<>();
       for (Movie.Genres genre: genres)
           res.add(genre.name);
       return TextUtils.join(" | ",res);
    }
}
