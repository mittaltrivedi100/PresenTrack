package com.example.myapplication.models;



public class AttendanceStudentModel {
    private String id;
    private String firstName;
    private String lastName;
    private String section;
    private boolean isPresent;


    public AttendanceStudentModel() {}

    // Optional: Full constructor if needed
    public AttendanceStudentModel(String id, String firstName, String lastName, String section, boolean isPresent) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.section = section;
        this.isPresent = isPresent;
    }

    // ✅ Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFullName() {
        return firstName + " " + lastName;
    }


    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
}


