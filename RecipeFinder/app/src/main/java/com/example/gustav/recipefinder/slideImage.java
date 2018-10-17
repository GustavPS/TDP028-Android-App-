package com.example.gustav.recipefinder;


import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by gustav on 9/12/18.
 */

public class slideImage {
    private String title;
    private String description;
    private String image;
    private String URI;
    private String calories;
    private String time;
    public Bitmap bitmap;

    public slideImage(String title, String description, String image, String URI, String calories, String time) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.bitmap = null;
        this.URI = URI;
        this.calories = calories;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getURI() { return URI; }

    public String getCalories() { return calories; }

    public String getTime() { return time; }
}
