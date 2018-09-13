package com.example.gustav.recipefinder;

import com.google.gson.JsonParser;

import org.json.JSONObject;

public class User {
    public String name;
    public String email;

    public User() {

    }

    public User(String first_name, String email) {
        this.name = first_name;
        this.email = email;
    }
}
