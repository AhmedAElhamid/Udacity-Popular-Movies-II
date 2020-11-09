package com.example.popularmovies.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.popularmovies.MainActivity;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<Movie> parseMovieJson(JSONObject movieJson) {
        List<Movie> movies=new ArrayList<>();
        String title;
        String posterPath;
        String overview;
        String rating;
        String releaseDate;
        String popularity;
        String id;
        if (movieJson != null) {
            try {


                JSONArray array = movieJson.getJSONArray("results");
                for(int i=0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    title = object.getString("title");
                    posterPath = object.getString("poster_path");
                    overview = object.getString("overview");
                    rating = object.getString("vote_average");
                    releaseDate = object.getString("release_date");
                    popularity = object.getString("popularity");
                    id = object.getString("id");
                    movies.add(new Movie(title,posterPath,overview,rating,releaseDate,popularity,id));
                }
                return movies;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }else {return null;}

    }

        public static List<Review> parseReviewsJson(JSONObject reviewJson) {
        List<Review> reviews=new ArrayList<>();
        String author;
        String content;
        if (reviewJson != null) {
            try {
                JSONArray array = reviewJson.getJSONArray("results");
                for(int i=0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    author = object.getString("author");
                    content = object.getString("content");
                    reviews.add(new Review(author,content));
                }

                return reviews;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }else {return null;}


    }

    public static List<Video> parseVideosJson(JSONObject videoJson) {
        List<Video> videos=new ArrayList<>();
        String key;
        String name;
        if (videoJson != null) {
            try {
                JSONArray array = videoJson.getJSONArray("results");
                for(int i=0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    name = object.getString("name");
                    key = "http://www.youtube.com/watch?v="+object.getString("key");
                    Log.i(JsonUtils.class.getSimpleName(),object.getString("key"));
                    videos.add(new Video(name,key));
                }

                return videos;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }else {return null;}

    }
}
