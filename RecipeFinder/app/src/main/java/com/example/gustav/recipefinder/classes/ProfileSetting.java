package com.example.gustav.recipefinder.classes;

import android.graphics.Bitmap;

public class ProfileSetting {
    private String text;
    private Bitmap image;

    public ProfileSetting(String text, Bitmap image) {
        this.text = text;
        this.image = image;
    }

    public String getText() { return text; }
    public Bitmap getBitmap() { return image; }
}
