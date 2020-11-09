package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovies.Adapters.ReviewsArrayAdapter;
import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;
import com.example.popularmovies.utils.DatabaseUtils;
import com.example.popularmovies.utils.DateUtils;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.Singelton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TASK_LOADER_ID = 311;
    private String mSelection = MovieContract.MovieEntry.TITLE_COLUMN +"=?";
    private String[] mSelectionArgs;
    private Cursor mCursor;
    private static final String TAG = MovieDetails.class.getSimpleName();
    public static final String MOVIE_ID_EXTRA = "movie_id_extra";
    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mAverageVote;
    private TextView mSummary;
    private ImageView mPoster;
    private ImageView mTrailer;
    private List<Review> mReview;
    private ReviewsArrayAdapter adapter;
    private boolean isFavourite;
    private AsyncTask<Void, Void, Cursor> asyncTask;
    private TextView mReviewsLabel;
    private List<Video> videos;
    private String API_KEY;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        API_KEY=getResources().getString(R.string.api_key);
        //this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUi();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.MOVIE_SELECTED)) {
            mSelectionArgs = new String[]{intent.getStringExtra(MainActivity.MOVIE_SELECTED)};
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null,MovieDetails.this);
            asyncTask = new AsyncTask<Void, Void, Cursor>() {
                @Override
                protected Cursor doInBackground(Void... voids) {
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,
                            null, mSelection, mSelectionArgs, MovieContract.MovieEntry._ID);
                    setfab(cursor);
                    return cursor;

                }
            };

            asyncTask.execute();

        }

    }

    private void setUi(){
        mReview=new ArrayList<>();
        mMovieTitle = (TextView) findViewById(R.id.movieTitleValue);
        mReleaseDate = (TextView) findViewById(R.id.movieReleaseDateValue);
        mAverageVote = (TextView) findViewById(R.id.movieVoteValue);
        mSummary = (TextView) findViewById(R.id.movieSummaryValue);
        mPoster = (ImageView) findViewById(R.id.poster_image);
        mTrailer = (ImageView) findViewById(R.id.image_id);
    }

    private void setListView(List<Review> reviews){
        mReviewsLabel = (TextView) findViewById(R.id.review_label);
        if(reviews.size()==0){
            mReviewsLabel.setVisibility(View.GONE);
        }
        adapter = new ReviewsArrayAdapter(this,reviews);
        ListView listView = (ListView) findViewById(R.id.reviews_listview);
        listView.setAdapter(adapter);
    }

    public void picassoLoader(Context context, ImageView imageView, String url){
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.movie)
                .error(R.drawable.movie)
                .into(imageView);
    }

    private void setUiFromCursor(Cursor cursor){
        if(cursor!=null){
            cursor.moveToPosition(0);
            mMovieTitle.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN)));
            mReleaseDate.setText(DateUtils.getDateFormat(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE_COLUMN))));
            mAverageVote.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.RATING_COLUMN)));
            mSummary.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW_COLUMN)));
            picassoLoader(getApplicationContext(),mPoster,NetworkUtils.buildPosterUrl(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH_COLUMN))).toString());
            getTrailerThumbnail(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.URL_ID)),getApplicationContext());
            setReviews(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.URL_ID)),getApplicationContext());
        }
    }

    private void setReviews(String id,Context context){
        URL url =NetworkUtils.buildReviewUrl(id,API_KEY);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Review> reviews = JsonUtils.parseReviewsJson(response);
                if(reviews!=null) {
                    setListView(reviews);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Singelton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    private void getTrailerThumbnail(String id,Context context){
        URL url =NetworkUtils.buildVideosUrl(id,API_KEY);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                videos = JsonUtils.parseVideosJson(response);
                if(videos!=null) {
                    picassoLoader(getApplicationContext(),mTrailer,NetworkUtils.buildYoutubeThumbnailUrl(videos.get(0).getKey()).toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Singelton.getInstance(context).addToRequestQueue(jsonObjectRequest);

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
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null, mSelection, mSelectionArgs, MovieContract.MovieEntry._ID);
                        if(cursor.getCount()==0){
                            cursor = getContentResolver().query(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,
                                    null, mSelection, mSelectionArgs, MovieContract.MovieEntry._ID);
                        }
                        return cursor;

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
        setUiFromCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void setfab(Cursor cursor){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        if(cursor.getCount()==0){
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24px));
            isFavourite=false;
        }else {
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24px));
            isFavourite=true;
        }
    }


    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onShareClicked(View view) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mMovieTitle.getText())
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        } else {
            Log.d(TAG,  "No receiving apps installed!");
        }

    }

    public void onPlayClicked(View view) {
        String movieUrlId = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.URL_ID));
        Intent intent = new Intent(MovieDetails.this,VideosActivity.class);
        intent.putExtra(MOVIE_ID_EXTRA,movieUrlId);
        startActivity(intent);
    }

    public void onFavouriteClicked(View view) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        ContentResolver contentResolver = getContentResolver();
        if(isFavourite){
            int numOfItemDeleted=contentResolver.delete(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,mSelection,mSelectionArgs);
            Log.i(TAG,"deleted " +Integer.toString(numOfItemDeleted));
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24px));
            isFavourite=false;
        }else {
            ContentValues contentValues = DatabaseUtils.getContentValuesFromCursor(mCursor);
            contentResolver.insert(MovieContract.MovieEntry.FAVOURITE_CONTENT_URI,contentValues);
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24px));
            isFavourite=true;
        }
    }


    public void onMovieTrailerClicked(View view) {
        if(videos!=null&&videos.size()>0){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videos.get(0).getKey())));
        }
    }
}