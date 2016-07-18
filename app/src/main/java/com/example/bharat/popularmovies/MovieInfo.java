package com.example.bharat.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bharat on 7/12/2016.
 */

//An object that contains various movie data. It also implements the parcelable class for easy and efficient transfer of data to the details activity class.
public class MovieInfo implements Parcelable {

    int movieID;
    String title;
    String poster;
    String releaseDate;
    String plot;
    String rating;

    public MovieInfo(int movieID, String mtitle, String mposter, String mreleaseDate, String mplot, String mrating ) {
        this.movieID = movieID;
        this.title = mtitle;
        this.poster = mposter;
        this.releaseDate = mreleaseDate;
        this.plot = mplot;
        this.rating = mrating;
    }

    public MovieInfo() {
        this.movieID = 0;
        this.title = "";
        this.poster = "";
        this.releaseDate = "";
        this.plot = "";
        this.rating = "";
    }

    private MovieInfo(Parcel in) {
        movieID = in.readInt();
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        plot = in.readString();
        rating = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieID);
        parcel.writeString(title);
        parcel.writeString(poster);
        parcel.writeString(releaseDate);
        parcel.writeString(plot);
        parcel.writeString(rating);
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel parcel) {
            return new MovieInfo(parcel);
        }

        @Override
        public MovieInfo[] newArray(int i) {
            return new MovieInfo[i];
        }

    };

}
