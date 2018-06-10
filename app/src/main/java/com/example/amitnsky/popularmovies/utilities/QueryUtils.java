package com.example.amitnsky.popularmovies.utilities;

import com.example.amitnsky.popularmovies.BuildConfig;

import okhttp3.HttpUrl;

public class QueryUtils {


    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String LOG_TAG = "QueryUtils";
    private static final String BASE_THUMBNAIL_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String BASE_MOV_URL = "http://api.themoviedb.org/3/movie/";
    private static final String KEY_API = "api_key";

    public static String getPosterUrl(String posterPath) {
        return HttpUrl.parse(BASE_THUMBNAIL_URL).newBuilder().addPathSegments(posterPath).toString();
    }

    /**
     * @param sortOrder/Id
     * @return query url for given param.
     * this method is used in both cases for a single movie url or list of movies url
     * as both are similar, only change is for a movie query we have id of movie
     * while for list of movies(posters) we have sort_order after BASE_MOV_URL .
     */
    public static String getQueryUrl(String sortOrder) {
        return HttpUrl.parse(BASE_MOV_URL).newBuilder()
                .addPathSegment(sortOrder)
                .addQueryParameter(KEY_API, API_KEY).toString();
    }
}
