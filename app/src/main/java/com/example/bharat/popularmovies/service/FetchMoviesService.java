package com.example.bharat.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.bharat.popularmovies.BuildConfig;
import com.example.bharat.popularmovies.R;
import com.example.bharat.popularmovies.Utility;
import com.example.bharat.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Bharat on 7/15/2016.
 */
//This class extends an IntentService in order to update both the Popular and Top Rated movie lists and store the
//information in the database.

public class FetchMoviesService extends IntentService {

    private static final String LOG_TAG = "Fetch Movies Service";

    public FetchMoviesService() {
        super(FetchMoviesService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.v(LOG_TAG, "Running Service");
        //This will get the data depending on the sort method.
        getData(Utility.getSortOrder(getApplicationContext()));

    }

    private void MovieDataFromJson(String movieJsonStr, String sortby)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_VOTEAVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_TITLE = "title";
        final String TMDB_RELEASEDATE = "release_date";
        final String TMDB_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsArray.length());

        for(int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultsData = resultsArray.getJSONObject(i);
            String BaseURL = "http://image.tmdb.org/t/p/";
            String imageSize = "w342";
            ContentValues moviesValues = new ContentValues();

            if (sortby.equals("popular")) {
                moviesValues.put(MovieContract.PopularMovies.COLUMN_MOVIE_ID, resultsData.getString(TMDB_ID));
                moviesValues.put(MovieContract.PopularMovies.COLUMN_TITLE, resultsData.getString(TMDB_TITLE));
                moviesValues.put(MovieContract.PopularMovies.COLUMN_POSTER, BaseURL+imageSize+resultsData.getString(TMDB_POSTER));
                moviesValues.put(MovieContract.PopularMovies.COLUMN_OVERVIEW, resultsData.getString(TMDB_OVERVIEW));
                moviesValues.put(MovieContract.PopularMovies.COLUMN_RELEASEDATE, resultsData.getString(TMDB_RELEASEDATE));
                moviesValues.put(MovieContract.PopularMovies.COLUMN_RATING, resultsData.getString(TMDB_VOTEAVERAGE));
            }
            else if (sortby.equals("top_rated")) {
                moviesValues.put(MovieContract.TopMovies.COLUMN_MOVIE_ID, resultsData.getString(TMDB_ID));
                moviesValues.put(MovieContract.TopMovies.COLUMN_TITLE, resultsData.getString(TMDB_TITLE));
                moviesValues.put(MovieContract.TopMovies.COLUMN_POSTER, BaseURL+imageSize+resultsData.getString(TMDB_POSTER));
                moviesValues.put(MovieContract.TopMovies.COLUMN_OVERVIEW, resultsData.getString(TMDB_OVERVIEW));
                moviesValues.put(MovieContract.TopMovies.COLUMN_RELEASEDATE, resultsData.getString(TMDB_RELEASEDATE));
                moviesValues.put(MovieContract.TopMovies.COLUMN_RATING, resultsData.getString(TMDB_VOTEAVERAGE));
            }
            cVVector.add(moviesValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            //adds the movies into the database depending on the sort method.
            if (sortby.equals("popular")) {
                getApplicationContext().getContentResolver().delete(MovieContract.PopularMovies.CONTENT_URI, null, null);
                getApplicationContext().getContentResolver().bulkInsert(MovieContract.PopularMovies.CONTENT_URI, cvArray);
            }
            else if (sortby.equals("top_rated")) {
                getApplicationContext().getContentResolver().delete(MovieContract.TopMovies.CONTENT_URI, null, null);
                getApplicationContext().getContentResolver().bulkInsert(MovieContract.TopMovies.CONTENT_URI, cvArray);
            }
        }
    }

    protected Void getData(String... params) {

        String sortby = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {

            final String TMDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(sortby)
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.TMDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            MovieDataFromJson(moviesJsonStr, sortby);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

}