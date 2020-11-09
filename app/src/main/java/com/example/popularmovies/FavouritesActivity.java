package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.example.popularmovies.Adapters.FavouriteAdapter;
import com.example.popularmovies.Adapters.MovieAdapter;
import com.example.popularmovies.Database.MovieContract;

public class FavouritesActivity extends AppCompatActivity implements FavouriteAdapter.FavouriteAdapterOnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView mRecyclerView;
    private FavouriteAdapter mFavouriteAdapter;
    private static final int FAVOURITE_TASK_LOADER_ID = 511;
    private Cursor mCursor;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private String mSelection = MovieContract.MovieEntry.TITLE_COLUMN +"=?";
    private String[] mSelectionArg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setRecyclerView(this);
        getSupportLoaderManager().initLoader(FAVOURITE_TASK_LOADER_ID,null,FavouritesActivity.this);
        mProgressBar = (ProgressBar) findViewById(R.id.favourites_progressbar);
        mToolbar = (Toolbar) findViewById(R.id.favourite_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = viewHolder.getAdapterPosition();
                mProgressBar.setVisibility(View.VISIBLE);
                deleteItemFromDatabase(id);

            }
        });itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void deleteItemFromDatabase(int id){
        mCursor.moveToPosition(id);
        mSelectionArg = new String[]{mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN))};
        getContentResolver().delete(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,mSelection,mSelectionArg);
        getSupportLoaderManager().restartLoader(FAVOURITE_TASK_LOADER_ID,null,FavouritesActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAVOURITE_TASK_LOADER_ID,null,FavouritesActivity.this);
    }


    private void setRecyclerView(Context context){
        mRecyclerView = findViewById(R.id.recyclerview_favourites);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFavouriteAdapter = new FavouriteAdapter(this);
        mRecyclerView.setAdapter(mFavouriteAdapter);
    }

    @Override
    public void onClick(int movieNumber) {
        Intent intent = new Intent(this,MovieDetails.class);
        ContentResolver resolver = getContentResolver();
        String selection = Integer.toString(movieNumber);
        String[] selectionArgs = {MovieContract.MovieEntry._ID};
        Cursor cursor = resolver.query(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,null,null,null,MovieContract.MovieEntry._ID);
        cursor.move(movieNumber+1);
        intent.putExtra(MainActivity.MOVIE_SELECTED,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN)));
        startActivity(intent);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(final int id, @Nullable Bundle args) {
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
                    return getContentResolver().query(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,
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
        mCursor=data;
        mFavouriteAdapter.setData(mCursor);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}