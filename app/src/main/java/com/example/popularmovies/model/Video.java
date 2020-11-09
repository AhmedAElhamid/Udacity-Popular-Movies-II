package com.example.popularmovies.model;

public class Video {
    private String name;
    private String key;
    public Video(String name,String key){
        this.name=name;
        this.key=key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }
}
