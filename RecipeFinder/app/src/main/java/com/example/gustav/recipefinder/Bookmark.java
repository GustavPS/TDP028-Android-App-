package com.example.gustav.recipefinder;

public class Bookmark {
    private String uri;
    private String title;
    private String image;
    private String time;
    private String calories;

    public Bookmark() {}

    public Bookmark(String URI, String title, String image, String time, String calories) {
        this.uri = URI;
        this.title = title;
        this.image = image;
        this.time = time;
        this.calories = calories;
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

    public String getTime() { return time; }

    public String getCalories() { return calories; }
}