package com.example.bharat.popularmovies.videos;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.bharat.popularmovies.BuildConfig;
import com.example.bharat.popularmovies.DetailFragment;
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
 * Created by Bharat on 7/16/2016.
 */
//This class extends Asynctask to perform an api call to tmdb for available videos given a movieID. It will
//set the result to the adapter. It also sets the share action provider.
//A click listener is attached to each button to pass the youtube key for the video as well as set up an intent to launch the video.

public class FetchVideosTask extends AsyncTask<String, Void, ArrayList<MovieVideos>> {
    private final Context mContext;
    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();
    View rootView;

    public FetchVideosTask(Context context, View rootView) { mContext = context; this.rootView = rootView;}

    private ArrayList<MovieVideos> VideosDataFromJson(String reviewJsonStr)
            throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_NAME = "name";
        final String TMDB_KEY = "key";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray resultsArray = reviewJson.getJSONArray(TMDB_RESULTS);
        ArrayList<MovieVideos> videos = new ArrayList<>();

        //This will parse the result and put it into an array of names and keys for the videos. It will then return an array of arrays.
        for(int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultsData = resultsArray.getJSONObject(i);
            videos.add(new MovieVideos(resultsData.getString(TMDB_NAME),resultsData.getString(TMDB_KEY)));
        }
        return videos;

    }

    @Override
    protected ArrayList<MovieVideos> doInBackground(String... params) {
        String movieID = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;
        String videos = "videos";

        try {
            final String TMDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(movieID)
                    .appendPath(videos)
                    .appendQueryParameter(APIKEY_PARAM,  BuildConfig.TMDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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
            return VideosDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (ArrayList<MovieVideos> result) {
        if (result != null) {
            if (!result.isEmpty()) {
                //Sets the recycler view to the arraylist with an adapter.
                RecyclerView videoView = (RecyclerView) rootView.findViewById(R.id.videos);
                VideosAdapter adapter = new VideosAdapter(mContext, result);
                videoView.setAdapter(adapter);
                videoView.setNestedScrollingEnabled(false);
                videoView.setLayoutManager(new LinearLayoutManager(mContext));

                //This will get the youtube key for the first video and then pass it into the share intent function in the DetailFragment
                String videoKey = result.get(0).key;
                DetailFragment.mShareActionProvider.setShareIntent(DetailFragment.createShareMovieIntent(videoKey));
            } else {
                //There are no videos so notify the user in the textview.
                rootView.findViewById(R.id.noVideos).setVisibility(View.VISIBLE);
                DetailFragment.mShareActionProvider.setShareIntent(null);
            }
        }
        //Done processing, set the loading indicator to gone
        rootView.findViewById(R.id.loadingVideos).setVisibility(View.GONE);
    }
}

