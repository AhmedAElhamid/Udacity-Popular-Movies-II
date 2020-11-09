package com.example.popularmovies.utils;

public class DateUtils {

    public static String getDateFormat(String date){
        return date.split("-")[0]+"/"+date.split("-")[1];
    }
}