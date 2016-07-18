package com.example.bharat.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bharat.popularmovies.data.MovieContract;

/**
 * Created by Bharat on 4/29/2016.
 */
public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.griditemmovie, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iv = (ImageView) view.findViewById(R.id.grid_item_movies);

        String sortby = Utility.getSortOrder(context);
        if (sortby.equals("popular")) {
            int idx_poster = cursor.getColumnIndex(MovieContract.PopularMovies.COLUMN_POSTER);
            String poster = cursor.getString(idx_poster);
            Glide.with(context).load(poster).into(iv);
        }
        else if (sortby.equals("top_rated")) {
            int idx_poster = cursor.getColumnIndex(MovieContract.TopMovies.COLUMN_POSTER);
            String poster = cursor.getString(idx_poster);
            Glide.with(context).load(poster).into(iv);
        }
        else if (sortby.equals("favorites")) {
            int idx_poster = cursor.getColumnIndex(MovieContract.FavMovies.COLUMN_POSTER);
            String poster = cursor.getString(idx_poster);
            Glide.with(context).load(poster).into(iv);
        }

    }

}


