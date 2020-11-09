package com.example.popularmovies;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.DatabaseUtils;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.Singelton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadMoviesIntentService extends IntentService {
    private static final int NUMBER_OF_PAGES = 5;
    private String type = NetworkUtils.POPULAR_KEY;
    private String API_KEY;
    //private static final int MOVIE_START = 100;
    public LoadMoviesIntentService(){super("LoadMoviesIntentService");}

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        API_KEY=getResources().getString(R.string.api_key);
        ContentResolver resolver = getContentResolver();
        if(intent.hasExtra(MainActivity.ACTION_LOAD_MORE)) {
            int pagesLoaded = (intent.getIntExtra(MainActivity.ACTION_LOAD_MORE, 0) / 20)+1;
            Log.i(LoadMoviesIntentService.class.getSimpleName(),"number of pages loaded = "+Integer.toString(pagesLoaded));
            if (intent.hasExtra(MainActivity.ACTION_TYPE)) {
                type = intent.getStringExtra(MainActivity.ACTION_TYPE);
                for (int i = pagesLoaded; i < NUMBER_OF_PAGES + pagesLoaded-3; i++) {
                    getMovieInfo(i, this, type,NUMBER_OF_PAGES+pagesLoaded-3);
                }
            } else if (intent.hasExtra(MainActivity.ACTION_SEARCH)) {
                String keyword = intent.getStringExtra(MainActivity.ACTION_SEARCH);
                getSearchResult(keyword, this, pagesLoaded+1);
            }
        }else {
            resolver.delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
            if (intent.hasExtra(MainActivity.ACTION_TYPE)) {
                type = intent.getStringExtra(MainActivity.ACTION_TYPE);
                for (int i = 1; i < NUMBER_OF_PAGES+1; i++) {
                    getMovieInfo(i, this, type,NUMBER_OF_PAGES+1);
                }
            } else if (intent.hasExtra(MainActivity.ACTION_SEARCH)) {
                String keyword = intent.getStringExtra(MainActivity.ACTION_SEARCH);
                getSearchResult(keyword, this, 1);
            }
        }

    }

    private void getSearchResult(final String keyword,final Context context,final int pageNumber){
        URL url = NetworkUtils.buildSearchUrl(keyword,Integer.toString(pageNumber),API_KEY);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Movie> movies = JsonUtils.parseMovieJson(response);
                if(movies!=null&&movies.size()!=0) {
                    insertMovieInDatabase(movies);
                 }
                Intent intent = new Intent(MainActivity.SERVICE_FINISHED_ACTION);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Singelton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    private void getMovieInfo(final int pageNumber, final Context context, String type,final int maxNumberPages){

        URL url = NetworkUtils.buildUrl(String.valueOf(pageNumber),type,API_KEY);
        Log.i(LoadMoviesIntentService.class.getSimpleName(),"url = "+url.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Movie> movies = JsonUtils.parseMovieJson(response);
                if(movies!=null) {

                  insertMovieInDatabase(movies);
                  if(pageNumber==maxNumberPages-1){
                      Intent intent = new Intent(MainActivity.SERVICE_FINISHED_ACTION);
                      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);}
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


    private void insertMovieInDatabase(List<Movie> movies){
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        for(int i=0;i<movies.size();i++){
        operations.add(ContentProviderOperation
                .newInsert(MovieContract.MovieEntry.CONTENT_URI)
                .withValue(MovieContract.MovieEntry.TITLE_COLUMN, movies.get(i).getTitle())
                .withValue(MovieContract.MovieEntry.POSTER_PATH_COLUMN, movies.get(i).getPosterPath())
                        .withValue(MovieContract.MovieEntry.OVERVIEW_COLUMN, movies.get(i).getOverview())
                .withValue(MovieContract.MovieEntry.POPULARITY_COLUMN, movies.get(i).getPopularity())
                .withValue(MovieContract.MovieEntry.RATING_COLUMN, movies.get(i).getRating())
                .withValue((MovieContract.MovieEntry.RELEASE_DATE_COLUMN), movies.get(i).getReleaseDate())
                .withValue((MovieContract.MovieEntry.URL_ID), movies.get(i).getId()).build());
        }

        try {
            getContentResolver().applyBatch(MovieContract.AUTHORITY_MOVIES,operations);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
