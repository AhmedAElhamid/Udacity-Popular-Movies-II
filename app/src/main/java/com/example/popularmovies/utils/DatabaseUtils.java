package com.example.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.model.Movie;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DatabaseUtils {

    public static ContentValues getContentValuesFromCursor(Cursor cursor){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.TITLE_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.OVERVIEW_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.POSTER_PATH_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.POPULARITY_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POPULARITY_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.RATING_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.RATING_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.RELEASE_DATE_COLUMN,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE_COLUMN)));
        contentValues.put(MovieContract.MovieEntry.URL_ID,cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.URL_ID)));
        return contentValues;

    }

}
