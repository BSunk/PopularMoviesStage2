package com.example.bharat.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bharat on 4/29/2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";
    private static MovieDBHelper mInstance = null;

    public static MovieDBHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new MovieDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + MovieContract.PopularMovies.TABLE_NAME +  " (" +
                MovieContract.PopularMovies.COLUMN_ID + " INTEGER PRIMARY KEY," +
                MovieContract.PopularMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.PopularMovies.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieContract.PopularMovies.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieContract.PopularMovies.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieContract.PopularMovies.COLUMN_RATING + " INTEGER NOT NULL," +
                MovieContract.PopularMovies.COLUMN_RELEASEDATE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_TOPMOVIES_TABLE = "CREATE TABLE " + MovieContract.TopMovies.TABLE_NAME +  " (" +
                MovieContract.TopMovies.COLUMN_ID + " INTEGER PRIMARY KEY," +
                MovieContract.TopMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.TopMovies.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieContract.TopMovies.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieContract.TopMovies.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieContract.TopMovies.COLUMN_RATING + " INTEGER NOT NULL," +
                MovieContract.TopMovies.COLUMN_RELEASEDATE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavMovies.TABLE_NAME +  " (" +
                MovieContract.FavMovies.COLUMN_ID + " INTEGER PRIMARY KEY," +
                MovieContract.FavMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.FavMovies.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieContract.FavMovies.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieContract.FavMovies.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieContract.FavMovies.COLUMN_RATING + " INTEGER NOT NULL," +
                MovieContract.FavMovies.COLUMN_RELEASEDATE + " INTEGER NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOPMOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.PopularMovies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TopMovies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavMovies.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }




}
