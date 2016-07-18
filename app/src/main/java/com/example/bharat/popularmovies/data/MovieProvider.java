package com.example.bharat.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.net.Uri;

/**
 * Created by Bharat on 4/29/2016.
 */
public class MovieProvider extends ContentProvider {

    static final int POPULAR_MOVIES = 100;
    static final int POPULAR_MOVIES_WITH_ID = 101;
    static final int TOPRATED_MOVIES = 200;
    static final int TOPRATED_MOVIES_WITH_ID = 201;
    static final int FAV_MOVIES = 300;
    static final int FAV_MOVIES_WITH_ID = 301;

    static final UriMatcher uriMatcher;
    static final String authority = MovieContract.CONTENT_AUTHORITY;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR_MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_TOPRATED, TOPRATED_MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_FAV, FAV_MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_FAV + "/#", FAV_MOVIES_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR + "/#", POPULAR_MOVIES_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_TOPRATED + "/#", TOPRATED_MOVIES_WITH_ID);
    }

    //popularmovies.movie_id = ?
    private static final String sPopularMoviesIDSelection =
            MovieContract.PopularMovies.TABLE_NAME+
                    "." + MovieContract.PopularMovies.COLUMN_MOVIE_ID + " = ? ";

    private static final String sTopratedMoviesIDSelection =
            MovieContract.TopMovies.TABLE_NAME+
                    "." + MovieContract.TopMovies.COLUMN_MOVIE_ID + " = ? ";

    private static final String sFavMoviesIDSelection =
            MovieContract.FavMovies.TABLE_NAME+
                    "." + MovieContract.FavMovies.COLUMN_MOVIE_ID + " = ? ";

    private MovieDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = uriMatcher.match(uri);

        switch (match) {
            case POPULAR_MOVIES:
                return MovieContract.PopularMovies.CONTENT_TYPE;
            case POPULAR_MOVIES_WITH_ID:
                return MovieContract.PopularMovies.CONTENT_TYPE;
            case TOPRATED_MOVIES:
                return MovieContract.TopMovies.CONTENT_TYPE;
            case TOPRATED_MOVIES_WITH_ID:
                return MovieContract.TopMovies.CONTENT_TYPE;
            case FAV_MOVIES:
                return MovieContract.FavMovies.CONTENT_TYPE;
            case FAV_MOVIES_WITH_ID:
                return MovieContract.FavMovies.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            // "popular"
            case POPULAR_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "toprated"
            case TOPRATED_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAV_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR_MOVIES_WITH_ID: {
                int movieID = MovieContract.getMovieIDFromURI(uri);
                selectionArgs = new String[]{Integer.toString(movieID)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularMovies.TABLE_NAME,
                        projection,
                        sPopularMoviesIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOPRATED_MOVIES_WITH_ID: {
                int movieID = MovieContract.getMovieIDFromURI(uri);
                selectionArgs = new String[]{Integer.toString(movieID)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopMovies.TABLE_NAME,
                        projection,
                        sTopratedMoviesIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAV_MOVIES_WITH_ID: {
                int movieID = MovieContract.getMovieIDFromURI(uri);
                selectionArgs = new String[]{Integer.toString(movieID)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavMovies.TABLE_NAME,
                        projection,
                        sFavMoviesIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POPULAR_MOVIES: {
                long _id = db.insert(MovieContract.PopularMovies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.PopularMovies.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOPRATED_MOVIES: {
                long _id = db.insert(MovieContract.TopMovies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TopMovies.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAV_MOVIES: {
                long _id = db.insert(MovieContract.FavMovies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavMovies.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case POPULAR_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.PopularMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case TOPRATED_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.TopMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case FAV_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.FavMovies.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POPULAR_MOVIES:
                rowsUpdated = db.update(MovieContract.PopularMovies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TOPRATED_MOVIES:
                rowsUpdated = db.update(MovieContract.TopMovies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAV_MOVIES:
                rowsUpdated = db.update(MovieContract.FavMovies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case POPULAR_MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PopularMovies.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TOPRATED_MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TopMovies.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    //In order to insert it into the Favorites database we must copy it from the respective table.
    public void insertMovieIntoFavorites(int movieID, String sortOrder, Context context) {
         SQLiteDatabase db = MovieDBHelper.getInstance(context).getWritableDatabase();
        try {
            if (sortOrder.equals("popular")) {
                db.beginTransaction();
                db.execSQL("CREATE TEMPORARY TABLE tmp AS SELECT * FROM " + MovieContract.PopularMovies.TABLE_NAME + " WHERE " + MovieContract.PopularMovies.COLUMN_MOVIE_ID + "=" + movieID + ";");
                db.execSQL("UPDATE tmp SET "+ MovieContract.PopularMovies.COLUMN_ID + "= NULL;");
                db.execSQL("INSERT INTO "+ MovieContract.FavMovies.TABLE_NAME + " SELECT * FROM tmp;");
                db.execSQL("DROP TABLE tmp;");
                db.setTransactionSuccessful();
            }
            if (sortOrder.equals("top_rated")) {
                db.beginTransaction();
                db.execSQL("CREATE TEMPORARY TABLE tmp AS SELECT * FROM " + MovieContract.TopMovies.TABLE_NAME + " WHERE " + MovieContract.TopMovies.COLUMN_MOVIE_ID + "=" + movieID + ";");
                db.execSQL("UPDATE tmp SET "+ MovieContract.TopMovies.COLUMN_ID + "= NULL;");
                db.execSQL("INSERT INTO "+ MovieContract.FavMovies.TABLE_NAME + " SELECT * FROM tmp;");
                db.execSQL("DROP TABLE tmp;");
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }
    }

    public static boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, int movieID, Context context) {
        SQLiteDatabase sqldb = MovieDBHelper.getInstance(context).getReadableDatabase();
        sqldb.beginTransaction();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + movieID;
        Cursor cursor = sqldb.rawQuery(Query, null);
        sqldb.setTransactionSuccessful();
        sqldb.endTransaction();
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
