package com.example.gustav.recipefinder;

public class Bookmark {
    private String uri;
    private String title;
    private String image;

    public Bookmark() {}

    public Bookmark(String URI, String title, String image) {
        this.uri = URI;
        this.title = title;
        this.image = image;
    }


    public String getURI() {
        return uri;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}