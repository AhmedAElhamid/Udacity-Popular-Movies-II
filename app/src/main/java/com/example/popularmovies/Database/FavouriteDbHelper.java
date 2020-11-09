package com.example.popularmovies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favourite.db";

    private static final int DATABASE_VERSION = 2;
    public FavouriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieContract.MovieEntry.FAVOURITE_TABLE_NAME + " ("+

                        MovieContract.MovieEntry._ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieContract.MovieEntry.TITLE_COLUMN  + " TEXT NOT NULL, "              +

                        MovieContract.MovieEntry.OVERVIEW_COLUMN + " TEXT, "            +

                        MovieContract.MovieEntry.POSTER_PATH_COLUMN + " TEXT, "         +

                        MovieContract.MovieEntry.POPULARITY_COLUMN + " TEXT, "          +

                        MovieContract.MovieEntry.RATING_COLUMN + " REAL, "              +

                        MovieContract.MovieEntry.RELEASE_DATE_COLUMN + " INTEGER, "     +

                        MovieContract.MovieEntry.URL_ID + " TEXT, "                      +


                        " UNIQUE (" + MovieContract.MovieEntry.TITLE_COLUMN + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.FAVOURITE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
