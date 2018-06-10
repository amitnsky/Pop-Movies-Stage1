package com.example.amitnsky.popularmovies.model;

import java.util.List;

public class ThumbnailsResponse {

    public List<Thumbnail> results;

    public static class Thumbnail {
        String poster_path;
        String id;

        public String getmThumbnailUrl() {
            return poster_path;
        }

        public String getmId() {
            return id;
        }

        public Thumbnail(String mThumbnailUrl, String mId) {
            this.poster_path = mThumbnailUrl;
            this.id = mId;
        }
    }
}
