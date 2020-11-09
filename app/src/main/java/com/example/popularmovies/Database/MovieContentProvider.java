package com.example.popularmovies.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES =  100;
    public static final int MOVIES_WITH_ID = 101;


    private MovieDbHelper mMovieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.MOVIE_TABLE_NAME,strings,s
                        ,strings1,null,null,s1);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArg = new String[]{id};

                retCursor = db.query(MovieContract.MovieEntry.MOVIE_TABLE_NAME,strings
                ,mSelection,mSelectionArg,null,null,s1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME,null,contentValues);
                if(id>0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri :" + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int numberOfItemsDeleted = 0;

        switch (match){
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                numberOfItemsDeleted = db.delete(MovieContract.MovieEntry.MOVIE_TABLE_NAME,mSelection,mSelectionArgs);
                break;
            case MOVIES:
                numberOfItemsDeleted = db.delete(MovieContract.MovieEntry.MOVIE_TABLE_NAME,s,strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return numberOfItemsDeleted;

}

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int numberOfItemsUpdated = 0;

        switch (match){
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                numberOfItemsUpdated = db.update(MovieContract.MovieEntry.MOVIE_TABLE_NAME,contentValues,mSelection,mSelectionArgs);
                break;
            case MOVIES:
                numberOfItemsUpdated = db.delete(MovieContract.MovieEntry.MOVIE_TABLE_NAME,s,strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return numberOfItemsUpdated;
    }



    static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Directory
        uriMatcher.addURI(MovieContract.AUTHORITY_MOVIES,MovieContract.PATH_MOVIES,MOVIES);
        //Single
        uriMatcher.addURI(MovieContract.AUTHORITY_MOVIES,MovieContract.PATH_MOVIES + "/#",MOVIES_WITH_ID);

        return uriMatcher;
    }

}
