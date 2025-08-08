package com.example.myapplication.models;

public class ClassModel {
    private String id;
    private String name;
    private String section;

    public ClassModel(String id, String name, String section) {
        this.id = id;
        this.name = name;
        this.section = section;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSection() {
        return section;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
