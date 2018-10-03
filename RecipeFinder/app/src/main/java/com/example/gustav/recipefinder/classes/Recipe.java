package com.example.gustav.recipefinder.classes;

import android.graphics.Bitmap;

public class Recipe {
    public String URI;
    public String title;
    public String url;
    public String calories;
    public String time;
    public Bitmap bitmap;

    public Recipe(String URI, String title, String url, String calories, String time) {
        this.URI = URI;
        this.title = title;
        this.url = url;
        this.calories = calories;
        this.time = time;
    }
}
