package com.example.popularmovies.model;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.util.ArrayList;

//original title
//movie poster image thumbnail
//A plot synopsis (called overview in the api)
//user rating (called vote_average in the api)
//release date
public class Movie {
    private String title;
    private String posterPath;
    private String overview;
    private String rating;
    private String releaseDate;
    private String Popularity;
    private String id;

    public Movie(){}

    public Movie(String title,String posterPath, String overview, String rating, String releaseDate, String Popularity,String id){
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate=releaseDate;
        this.Popularity = Popularity;
        this.id = id;
    }

    public String getTitle(){return title;}

    public void setTitle(String title){this.title=title;};

    public String getOverview() { return overview;}

    public void setOverview(String overview) { this.overview = overview;}

    public String getPopularity() { return Popularity;}

    public void setPopularity(String popularity) { Popularity = popularity;}

    public String getPosterPath() { return posterPath;}

    public void setPosterPath(String posterPath) { this.posterPath = posterPath;}

    public String getRating() { return rating;}

    public void setRating(String rating) { this.rating = rating;}

    public String getReleaseDate() { return releaseDate;}

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
