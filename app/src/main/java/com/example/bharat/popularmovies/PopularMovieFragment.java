package com.example.bharat.popularmovies;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.bharat.popularmovies.data.MovieContract;

/**
 * A simple {@link Fragment} subclass.
 */

public class PopularMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MoviesAdapter mMovieAdapter;
    private GridView mGridview;
    private String mSortby;
    public final int MOVIES_LOADER = 0;

    public interface Callback {
        //Callback for when an item has been selected.
        public void onItemSelected(MovieInfo movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortby = Utility.getSortOrder(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movie, container, false);
        mMovieAdapter = new MoviesAdapter(getActivity(), null, 0);
        mGridview = (GridView) rootView.findViewById(R.id.gridviewmovies);
        mGridview.setAdapter(mMovieAdapter);
        mGridview.setEmptyView(rootView.findViewById(R.id.emptyGridView));

        //This onclick function will set a MovieInfo object and pass it into the onclick function in the MainActivity
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int movieID = cursor.getInt(DetailFragment.COL_MOVIE_ID);
                    String title = cursor.getString(DetailFragment.COL_TITLE);
                    String overview = cursor.getString(DetailFragment.COL_OVERVIEW);
                    String rating = cursor.getString(DetailFragment.COL_RATING);
                    String poster = cursor.getString(DetailFragment.COL_POSTER);
                    String releaseDate = cursor.getString(DetailFragment.COL_RELEASEDATE);
                    MovieInfo movie = new MovieInfo(movieID, title, poster, releaseDate, overview, rating);
                    ((Callback) getActivity())
                            .onItemSelected(movie);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(MOVIES_LOADER) == null) {
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        } else {
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }


    //When called this will change the layout of the movies to the latest sort method.
    void onSortChanged( ) {
        mSortby = Utility.getSortOrder(getActivity());
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        mGridview.setSelection(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (mSortby.equals(getString(R.string.sort_order_popular))) {
            return new CursorLoader(getActivity(),
                    MovieContract.PopularMovies.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        else if (mSortby.equals(getString(R.string.sort_order_toprated))) {
            return new CursorLoader(getActivity(),
                    MovieContract.TopMovies.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        else if (mSortby.equals(getString(R.string.sort_order_favorites))) {
            return new CursorLoader(getActivity(),
                    MovieContract.FavMovies.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        else { return null; }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.changeCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {mMovieAdapter.changeCursor(null);
    }
}