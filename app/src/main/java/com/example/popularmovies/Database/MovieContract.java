package com.example.popularmovies.Database;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY_MOVIES = "com.example.android.popularmovies.movies";
    public static final Uri MOVIES_BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY_MOVIES);
    public static final String AUTHORITY_FAVOURITES = "com.example.android.popularmovies.favourites";
    public static final Uri FAVOURITES_BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY_FAVOURITES);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVOURITE = "favourite";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                MOVIES_BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final Uri FAVOURITE_CONTENT_URI =
                FAVOURITES_BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();


        public static final String MOVIE_TABLE_NAME = "movie";
        public static final String FAVOURITE_TABLE_NAME = "favourite";
        public static final String _ID = "id";
        public static final String TITLE_COLUMN = "title";
        public static final String POSTER_PATH_COLUMN = "poster_path";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String RATING_COLUMN = "rating";
        public static final String RELEASE_DATE_COLUMN = "release_date";
        public static final String POPULARITY_COLUMN = "popularity";
        public static final String URL_ID = "url_id";
    }
}
