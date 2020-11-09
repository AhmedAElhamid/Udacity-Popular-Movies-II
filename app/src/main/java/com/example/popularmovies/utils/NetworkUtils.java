/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String TMDB_MOVIE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String TMDB_SEARCH_URL =
            "https://api.themoviedb.org/3/search";

    private static final String MOVIE_BASE_URL = TMDB_MOVIE_URL;

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */


    final static String QUERY_PARAM = "query";
    final static String API_KEY_PARAM = "api_key";
    final static String PAGE_PARAM="page";
    public static final String POPULAR_KEY = "popular";
    public static final String TOP_RATED_KEY = "top_rated";
    public static final String NOW_PLAYING_KEY = "now_playing";
    public static final String UPCOMING_KEY = "upcoming";
    public static final String REVIEW_KEY = "reviews";
    public static final String VIDEOS_KEY = "videos";
    public static final String MOVIE_KEY = "movie";
    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * //@param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */

//    REVIEW_URL="https://api.themoviedb.org/3/movie/"+MainActivity.id[intGotPosition]+"/reviews?api_key="+getResources().getString(R.string.API_key);
//    VIDEO_URL="http://api.themoviedb.org/3/movie/"+MainActivity.id[intGotPosition]+"/videos?api_key="+getResources().getString(R.string.API_key);

    public static URL buildUrl(String pageNumber,String type,String API_KEY) {
        Uri buildUri;
        switch (type){
            case POPULAR_KEY:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(POPULAR_KEY)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).appendQueryParameter(PAGE_PARAM,pageNumber).build();
                break;
            case TOP_RATED_KEY:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(TOP_RATED_KEY)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).appendQueryParameter(PAGE_PARAM,pageNumber).build();
                break;
            case NOW_PLAYING_KEY:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(NOW_PLAYING_KEY)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).appendQueryParameter(PAGE_PARAM,pageNumber).build();
                break;
            case UPCOMING_KEY:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(UPCOMING_KEY)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).appendQueryParameter(PAGE_PARAM,pageNumber).build();
                break;
            default:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(pageNumber)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).build();
        }
            URL url = null;
            try {
                url = new URL(buildUri.toString());
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            return url;
    }
//    https://api.themoviedb.org/3/movie/popular?api_key=316a9527ef72a88bee56ccbf8acb9d53&page=1
//    https://api.themoviedb.org/3/search/movie?query=Avengers&api_key=316a9527ef72a88bee56ccbf8acb9d53&page=1

    public static URL buildSearchUrl(String keyword,String pageNumber,String API_KEY){
        Uri buildUri;
        buildUri = Uri.parse(TMDB_SEARCH_URL).buildUpon().appendEncodedPath(MOVIE_KEY)
                .appendQueryParameter(QUERY_PARAM,keyword).appendQueryParameter(API_KEY_PARAM,API_KEY).appendQueryParameter(PAGE_PARAM,pageNumber).build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildPosterUrl(String posterPath){

        Uri buildUri = Uri.parse(POSTER_BASE_URL).buildUpon().appendEncodedPath(posterPath)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildReviewUrl(String urlId,String API_KEY) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(urlId)
                        .appendEncodedPath(REVIEW_KEY)
                        .appendQueryParameter(API_KEY_PARAM,API_KEY).build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildVideosUrl(String urlId,String API_KEY) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendEncodedPath(urlId)
                .appendEncodedPath(VIDEOS_KEY)
                .appendQueryParameter(API_KEY_PARAM,API_KEY).build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildYoutubeThumbnailUrl(String youtubeUrl) {
        youtubeUrl = youtubeUrl.replace("http://www.youtube.com/watch?v=","https://img.youtube.com/vi/");
        youtubeUrl = youtubeUrl+"/0.jpg";
        Uri buildUri = Uri.parse(youtubeUrl);
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}