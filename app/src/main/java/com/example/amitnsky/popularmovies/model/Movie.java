package com.example.amitnsky.popularmovies.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.amitnsky.popularmovies.BR;

import java.util.ArrayList;
import java.util.List;

public class Movie extends BaseObservable implements Parcelable {
    public int id;
    public String title;
    public String overview;
    public String poster_path;
    public String release_date;
    public int runtime;
    public double vote_average;
    public List<Movie.Genres> genres;
    public String tagline;
    public String original_title;

    public static class Genres {
        public String name;
    }

    @Bindable
    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String path){
        this.poster_path = path;
        notifyPropertyChanged(BR.poster_path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.poster_path);
        dest.writeString(this.release_date);
        dest.writeInt(this.runtime);
        dest.writeDouble(this.vote_average);
        dest.writeList(this.genres);
        dest.writeString(this.tagline);
        dest.writeString(this.original_title);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.overview = in.readString();
        this.poster_path = in.readString();
        this.release_date = in.readString();
        this.runtime = in.readInt();
        this.vote_average = in.readDouble();
        this.genres = new ArrayList<Genres>();
        in.readList(this.genres, Genres.class.getClassLoader());
        this.tagline = in.readString();
        this.original_title = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
