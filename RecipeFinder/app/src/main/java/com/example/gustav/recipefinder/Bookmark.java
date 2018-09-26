package com.example.gustav.recipefinder;

public class Bookmark {
    private String URI;
    private String title;
    private String image;

    public Bookmark() {}

    public Bookmark(String URI, String title, String image) {
        this.URI = URI;
        this.title = title;
        this.image = image;
    }


    public String getURI() {
        return URI;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}