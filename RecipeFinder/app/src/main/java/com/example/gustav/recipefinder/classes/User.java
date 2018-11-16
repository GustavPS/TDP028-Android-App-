package com.example.gustav.recipefinder.classes;

public class User {
    private String name;
    private String ID;

    public User() {}

    public User(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }

    public String getName() { return this.name; }
    public String getID() { return this.ID; }

    public void setID(String ID) { this.ID = ID; }
}
