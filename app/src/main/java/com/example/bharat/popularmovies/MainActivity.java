package com.example.bharat.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bharat.popularmovies.service.FetchMoviesService;

public class MainActivity extends AppCompatActivity implements PopularMovieFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static boolean mTwoPane;
    private String mSortby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // first launch
            Intent intent = new Intent(this, FetchMoviesService.class);
            startService(intent);
        }

        mSortby = Utility.getSortOrder(this);

        setContentView(R.layout.activity_main);
        //checks to see if on a tablet. If true then it will add the detail fragment into the layout.
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortby = Utility.getSortOrder(this);
        if (sortby != null && !sortby.equals(mSortby)) {
            PopularMovieFragment ff = (PopularMovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if ( null != ff ) {
                ff.onSortChanged();
            }
            mSortby = sortby;
        }
    }

    @Override
    public void onItemSelected(MovieInfo movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra(DetailsActivity.MOVIE_KEY, movie);
            startActivity(intent);
        }
    }
    //This function will set the sortOrder selected and call the reload function in the PopularMoviesFragment.
    public void loadMovies(MenuItem item){
        if (item.getOrder()==10) {
            Utility.saveSortOrder(getApplicationContext(), getString(R.string.sort_order_popular));
            Intent intent = new Intent(this, FetchMoviesService.class);
            startService(intent);
        }

        else if (item.getOrder()==20) {
            Utility.saveSortOrder(getApplicationContext(), getString(R.string.sort_order_toprated));
            Intent intent = new Intent(this, FetchMoviesService.class);
            startService(intent);
        }

        else if (item.getOrder()==30) {
            Utility.saveSortOrder(getApplicationContext(), getString(R.string.sort_order_favorites));
        }

        PopularMovieFragment ff = (PopularMovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
        if ( null != ff ) {
            ff.onSortChanged();
        }
        if(mTwoPane) {
            onItemSelected(new MovieInfo()); //In order to set the detailView invisible on tablets send a blank MovieInfo object.
        }
    }

}
