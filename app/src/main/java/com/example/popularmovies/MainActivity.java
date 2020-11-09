package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.Adapters.MovieAdapter;
import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.utils.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuItemView;

import java.net.URL;
import java.util.List;

// TODO (1) layout
// TODO (2) add recyclerview (movieAdapter.class contains movieAdapterViewHolder,MovieAdapter) and clickHandler
// TODO (3) add Internet connection(AsyncTask)
// TODO (4) parse json file from API into
// TODO (5) Intents to move between activities
public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOncClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    public MovieAdapter mMovieAdapter;
    public static String MOVIE_SELECTED = "movie_selected";
    private static final int TASK_LOADER_ID = 1357;
    public static final String SERVICE_FINISHED_ACTION = "service-finished";
    public static final String LOAD_MORE_ACTION = "load-more";
    private BroadcastReceiver broadcastReceiver;
    private ProgressBar mProgressBar;
    public static final String ACTION_TYPE="Type";
    public static final String ACTION_SEARCH="search";
    private int currentNavigationSelected;
    private EditText mSearchEditText;
    private ImageButton mSearchButton;
    private BottomNavigationView mNavigation;
    public static final String ACTION_LOAD_MORE="load_more";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,LoadMoviesIntentService.class);
        intent.putExtra(ACTION_TYPE, NetworkUtils.POPULAR_KEY);
        startService(intent);
        currentNavigationSelected = R.id.navigation_popular;
        mProgressBar = (ProgressBar) findViewById(R.id.main_progressBar);
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
        mProgressBar.setVisibility(View.VISIBLE);
        setRecyclerView(this);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null,MainActivity.this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction()==MainActivity.SERVICE_FINISHED_ACTION){
                    getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null,MainActivity.this);

                }else if(intent.getAction()==LOAD_MORE_ACTION){
                    Log.i(TAG,"got the broadcast");
                    loadMore();
                }
            }
        };
        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null,MainActivity.this);
        setEditTextVisibility();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            if (currentNavigationSelected != item.getItemId()||item.getItemId()==R.id.navigation_search) {
                if(item.getItemId()!=R.id.navigation_search){
                    currentNavigationSelected=item.getItemId();}
                switch (item.getItemId()) {
                    case R.id.navigation_popular:
                        setEditTextVisibility();
                        mMovieAdapter.clearData();
                        Log.i(TAG, Integer.toString(mMovieAdapter.getItemCount()));
                        mProgressBar.setVisibility(View.VISIBLE);
                        intent = new Intent(MainActivity.this, LoadMoviesIntentService.class);
                        intent.putExtra(ACTION_TYPE, NetworkUtils.POPULAR_KEY);
                        startService(intent);
                        return true;
                    case R.id.navigation_top_rated:
                        setEditTextVisibility();
                        mMovieAdapter.clearData();
                        Log.i(TAG, Integer.toString(mMovieAdapter.getItemCount()));
                        mProgressBar.setVisibility(View.VISIBLE);
                        intent = new Intent(MainActivity.this, LoadMoviesIntentService.class);
                        intent.putExtra(ACTION_TYPE, NetworkUtils.TOP_RATED_KEY);
                        startService(intent);
                        return true;
                    case R.id.navigation_now_playing:
                        setEditTextVisibility();
                        mMovieAdapter.clearData();
                        mProgressBar.setVisibility(View.VISIBLE);
                        intent = new Intent(MainActivity.this, LoadMoviesIntentService.class);
                        intent.putExtra(ACTION_TYPE, NetworkUtils.NOW_PLAYING_KEY);
                        startService(intent);
                        return true;
                    case R.id.navigation_up_coming:
                        setEditTextVisibility();
                        mMovieAdapter.clearData();
                        mProgressBar.setVisibility(View.VISIBLE);
                        intent = new Intent(MainActivity.this, LoadMoviesIntentService.class);
                        intent.putExtra(ACTION_TYPE, NetworkUtils.UPCOMING_KEY);
                        startService(intent);
                        return true;
                    case R.id.navigation_search:
                        if(mSearchButton.getVisibility()==View.VISIBLE){
                            mSearchButton.setVisibility(View.GONE);
                            mSearchEditText.setVisibility(View.GONE);

                        }else {
                            mSearchButton.setVisibility(View.VISIBLE);
                            mSearchEditText.setVisibility(View.VISIBLE);
                        }
                        return true;

                }
            }

            return false;
        }
    };

    private void setEditTextVisibility(){
        if(mSearchButton.getVisibility()==View.VISIBLE){
            mSearchButton.setVisibility(View.GONE);
            mSearchEditText.setVisibility(View.GONE);}
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_FINISHED_ACTION);
        filter.addAction(LOAD_MORE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    private void setRecyclerView(Context context){
        mRecyclerView = findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager
                = new GridLayoutManager(context,2);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }


    @Override
    public void onClick(int movieNumber) {
        Intent intent = new Intent(MainActivity.this,MovieDetails.class);
        ContentResolver resolver = getContentResolver();
        String selection = Integer.toString(movieNumber);
        String[] selectionArgs = {MovieContract.MovieEntry._ID};

        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,MovieContract.MovieEntry._ID);
        cursor.move(movieNumber+1);
        intent.putExtra(MOVIE_SELECTED,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN)));
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mData = null;

            @Override
            protected void onStartLoading() {
                if(mData!=null){
                    deliverResult(mData);
                }else{
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null, null, null, MovieContract.MovieEntry._ID);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                mData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public void openFavouritesActivity(View view) {
        Intent intent = new Intent(MainActivity.this,FavouritesActivity.class);
        startActivity(intent);
    }

    public void onSearchClicked(View view) {
        if(mSearchEditText.getText()!=null&&mSearchEditText.getText().toString().length()>0){
            String keyword = mSearchEditText.getText().toString();
            Intent intent = new Intent(MainActivity.this,LoadMoviesIntentService.class);
            intent.putExtra(ACTION_SEARCH,keyword);
            mMovieAdapter.clearData();
            Log.i(TAG, Integer.toString(mMovieAdapter.getItemCount()));
            mProgressBar.setVisibility(View.VISIBLE);
            currentNavigationSelected=R.id.navigation_search;
            startService(intent);

        }
    }

    public void loadMore() {
        Intent intent = new Intent(MainActivity.this, LoadMoviesIntentService.class);
        switch (currentNavigationSelected){
            case R.id.navigation_popular:
                intent.putExtra(ACTION_TYPE, NetworkUtils.POPULAR_KEY);
                break;
            case R.id.navigation_now_playing:
                intent.putExtra(ACTION_TYPE, NetworkUtils.NOW_PLAYING_KEY);
                break;
            case R.id.navigation_top_rated:
                intent.putExtra(ACTION_TYPE, NetworkUtils.TOP_RATED_KEY);
                break;
            case R.id.navigation_up_coming:
                intent.putExtra(ACTION_TYPE, NetworkUtils.UPCOMING_KEY);
                break;
            case R.id.navigation_search:
                intent.putExtra(ACTION_TYPE, MainActivity.ACTION_SEARCH);
                break;
        }
        intent.putExtra(ACTION_LOAD_MORE,mMovieAdapter.getItemCount());
        startService(intent);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        mSearchEditText.clearFocus();
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

}