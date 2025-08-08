package com.example.myapplication.models;



public class ViewAttendanceStudentModel {
    private String id;
    private String name;
    private String status;  // e.g., "Present" or "Absent"

    public ViewAttendanceStudentModel(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    // Setters (if needed)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
