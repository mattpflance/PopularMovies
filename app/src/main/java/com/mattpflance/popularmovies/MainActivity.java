package com.mattpflance.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String DEFAULT_SORT = "popularity.desc";

    private static boolean mTwoPane;
    private String mSortingStr;
    private ImageAdapter mMoviesAdapter;
    private AsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortingStr = getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.sort_key), DEFAULT_SORT);

        mMoviesAdapter = MoviesFragment.getMoviesAdapter();

        mAsyncTask = loadMovies();

        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                DetailFragment fragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_popular) {
            mSortingStr = "popularity.desc";
            editor.putString(getString(R.string.sort_key), mSortingStr);
            editor.apply();
            loadMovies();
            return true;
        } else if (id == R.id.action_sort_by_high_rating) {
            // Include movies with at least 50 ratings for filtering
            mSortingStr = "vote_average.desc";
            editor.putString(getString(R.string.sort_key), mSortingStr);
            editor.apply();
            loadMovies();
            return true;
        } else if (id == R.id.action_favourites) {
            // Include movies with at least 50 ratings for filtering
            mSortingStr = "favourites";
            editor.putString(getString(R.string.sort_key), mSortingStr);
            editor.apply();
            loadMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AsyncTask loadMovies() { return new FetchMoviesTask(this, mSortingStr, mMoviesAdapter).execute(); }

    public static boolean getTwoPane() { return mTwoPane; }
    public static String getDetailfragmentTag() { return DETAILFRAGMENT_TAG; }
}