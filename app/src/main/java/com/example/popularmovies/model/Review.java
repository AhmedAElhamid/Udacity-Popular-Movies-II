package com.example.popularmovies.model;

public class Review {
    private String author;
    private String content;

    public Review(String author,String content){
        this.author=author;
        this.content=content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}