package com.example.bharat.popularmovies.reviews;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.bharat.popularmovies.BuildConfig;
import com.example.bharat.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bharat on 7/15/2016.
 */

//Asynctask to get the reviews for the selected movie from the TMDB database.
public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<MovieReviews>> {
    private final Context mContext;
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private View rootView;

    public FetchReviewsTask(Context context, View rootView) {
        mContext = context; this.rootView = rootView;
    }

    private ArrayList<MovieReviews> ReviewDataFromJson(String reviewJsonStr)
            throws JSONException {

        ArrayList<MovieReviews> movieReviews = new ArrayList<>();

        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray resultsArray = reviewJson.getJSONArray(TMDB_RESULTS);
        String resultStrs = "";

        //This will just put all the reviews into one string and return it.
        for(int i = 0; i < resultsArray.length(); i++) {

            JSONObject resultsData = resultsArray.getJSONObject(i);
            movieReviews.add(new MovieReviews(resultsData.getString(TMDB_AUTHOR),resultsData.getString(TMDB_CONTENT)));
          //  resultStrs = resultStrs + resultsData.getString(TMDB_AUTHOR) + " - " + resultsData.getString(TMDB_CONTENT)+"\n\n";
        }
        return movieReviews;

    }

    @Override
    protected ArrayList<MovieReviews> doInBackground(String... params) {
        String movieID = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;
        String reviews = "reviews";
        try {
            final String TMDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(movieID)
                    .appendPath(reviews)
                    .appendQueryParameter(APIKEY_PARAM,  BuildConfig.TMDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            reviewJsonStr = buffer.toString();
        }
        catch (IOException e) {
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
            return ReviewDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (ArrayList<MovieReviews> result) {
        if(result !=null) {
            if (!result.isEmpty()) {
                RecyclerView reviewView = (RecyclerView) rootView.findViewById(R.id.reviews);
                ReviewsAdapter adapter = new ReviewsAdapter(mContext, result);
                reviewView.setAdapter(adapter);
                reviewView.setNestedScrollingEnabled(false);
                reviewView.setLayoutManager(new LinearLayoutManager(mContext));
                rootView.findViewById(R.id.loadingReviews).setVisibility(View.GONE);
            } else {
                //No reviews, so set the textview visible to tell the user.
                rootView.findViewById(R.id.noReviews).setVisibility(View.VISIBLE);
            }
        }
        //done loading, set loading to gone
        rootView.findViewById(R.id.loadingReviews).setVisibility(View.GONE);



    }

}