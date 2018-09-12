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
    public Bitmap bitmap;

    public slideImage(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.bitmap = null;
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
}
