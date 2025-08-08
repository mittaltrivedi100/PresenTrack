package com.example.myapplication.models;

public class ViewAttendanceLectureModel {
    private String id;
    private String name;

    public ViewAttendanceLectureModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Optional: for spinner display (to show name in spinner)
    @Override
    public String toString() {
        return name;
    }
}
