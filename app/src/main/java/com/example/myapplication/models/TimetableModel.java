package com.example.myapplication.models;

public class TimetableModel {
    private int id;
    private String name;

    // Constructor with id and name (used for existing records)
    public TimetableModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Overloaded constructor with only name (used for new records)
    public TimetableModel(String name) {
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
