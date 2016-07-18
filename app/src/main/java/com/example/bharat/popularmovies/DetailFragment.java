package com.example.bharat.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bharat.popularmovies.data.MovieContract;
import com.example.bharat.popularmovies.data.MovieProvider;
import com.example.bharat.popularmovies.reviews.FetchReviewsTask;
import com.example.bharat.popularmovies.videos.FetchVideosTask;

/**
 * Created by Bharat on 5/2/2016.
 */
public class DetailFragment extends Fragment {

    static final String DETAIL_URI = "URI";
    private MovieInfo movieInfo;
    public static ShareActionProvider mShareActionProvider;
    View rootView;

    //Column ids to the database
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_POSTER = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RATING = 5;
    public static final int COL_RELEASEDATE = 6;

    //References to the various views.
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;
    private ImageButton mFavButton;
    private ImageButton mFavButtonRemove;
    private ScrollView mScrollView;
    LinearLayout detailView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieInfo = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mPoster = (ImageView) rootView.findViewById(R.id.moviePoster);
        mTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        mOverview = (TextView) rootView.findViewById(R.id.movieOverview);
        mRating = (TextView) rootView.findViewById(R.id.movieVoteAverage);
        mReleaseDate = (TextView) rootView.findViewById(R.id.movieReleaseDate);
        mFavButton = (ImageButton) rootView.findViewById(R.id.favbuttonadd);
        mFavButtonRemove = (ImageButton) rootView.findViewById(R.id.favbuttonremove);
        detailView = (LinearLayout) rootView.findViewById(R.id.detailView);
        setDetails();
        mScrollView = (ScrollView) rootView.findViewById(R.id.details_scroll);

        if (savedInstanceState!=null) {
            final int[] position = savedInstanceState.getIntArray("SCROLL_POS");
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }
        return rootView;
    }


    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("SCROLL_POS",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    public static Intent createShareMovieIntent(String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String youtubeLink = "https://www.youtube.com/watch?v=" + key;
        shareIntent.putExtra(Intent.EXTRA_TEXT, youtubeLink);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setDetails() {
        if (movieInfo != null) {
            if (movieInfo.movieID == 0) {
               detailView.setVisibility(View.INVISIBLE); //sets the detail view invisible when a change in sort order occurs.
            } else {
                detailView.setVisibility(View.VISIBLE);
                mTitle.setText(movieInfo.title);
                Glide.with(getContext()).load(movieInfo.poster).placeholder(R.drawable.movieplaceholder).into(mPoster);
                mOverview.setText(movieInfo.plot);

                int voteAve = Math.round(Float.parseFloat(movieInfo.rating));
                String voteAverage = String.format(getResources().getString(R.string.vote_average), Integer.toString(voteAve));
                mRating.setText(voteAverage);

                String releaseDate = String.format(getResources().getString(R.string.release_Date), movieInfo.releaseDate.substring(0, 4));
                mReleaseDate.setText(releaseDate);
                //Checks if movie is in the favorites database or not and sets the button visibility.
                if (MovieProvider.CheckIsDataAlreadyInDBorNot(MovieContract.FavMovies.TABLE_NAME, MovieContract.FavMovies.COLUMN_MOVIE_ID, movieInfo.movieID, getContext())) {
                    mFavButton.setVisibility(View.GONE);
                    mFavButtonRemove.setVisibility(View.VISIBLE);
                } else {
                    mFavButtonRemove.setVisibility(View.GONE);
                    mFavButton.setVisibility(View.VISIBLE);
                }

                //Onclick listener to add the movie into the favorites database.
                mFavButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //If not in database then insert it and notify user it has been added.
                        if (!MovieProvider.CheckIsDataAlreadyInDBorNot(MovieContract.FavMovies.TABLE_NAME, MovieContract.FavMovies.COLUMN_MOVIE_ID, movieInfo.movieID, getContext())) {
                            String sortOrder = Utility.getSortOrder(getActivity());
                            MovieProvider db = new MovieProvider();
                            db.insertMovieIntoFavorites(movieInfo.movieID, sortOrder, getContext());
                            Toast.makeText(getContext(), getString(R.string.favoritesAdd), Toast.LENGTH_SHORT).show();
                            mFavButton.setVisibility(View.GONE);
                            mFavButtonRemove.setVisibility(View.VISIBLE);
                        }
                    }
                });
                //Onclick listener to remove the movie from the favorites database.
                mFavButtonRemove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (MovieProvider.CheckIsDataAlreadyInDBorNot(MovieContract.FavMovies.TABLE_NAME, MovieContract.FavMovies.COLUMN_MOVIE_ID, movieInfo.movieID, getContext())) {
                            getContext().getContentResolver().delete(MovieContract.FavMovies.CONTENT_URI, MovieContract.FavMovies.COLUMN_MOVIE_ID + "=?", new String[]{Integer.toString(movieInfo.movieID)});
                            Toast.makeText(getContext(), getString(R.string.favoritesRemove), Toast.LENGTH_SHORT).show();
                            mFavButtonRemove.setVisibility(View.GONE);
                            mFavButton.setVisibility(View.VISIBLE);
                            if (MainActivity.mTwoPane & Utility.getSortOrder(getContext()).equals(getString(R.string.sort_order_favorites))) {
                                detailView.setVisibility(View.INVISIBLE); //sets the detailview invisible on tablets when removing a movie from favs.
                            }
                        }
                    }
                });

                //Calls the review and videos async tasks to populate their respective fields.
                FetchVideosTask videos = new FetchVideosTask(getContext(), rootView);
                videos.execute(Integer.toString(movieInfo.movieID));
                FetchReviewsTask reviews = new FetchReviewsTask(getContext(), rootView);
                reviews.execute(Integer.toString(movieInfo.movieID));
            }
        }
    }

}