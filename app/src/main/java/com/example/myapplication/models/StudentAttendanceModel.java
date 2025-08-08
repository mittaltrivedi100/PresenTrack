package com.example.myapplication.models;

public class StudentAttendanceModel {
//    private int id;
    private String id,firstName, lastName, email, contactNumber, section;
    private String password, fatherName, dob;

    public StudentAttendanceModel(String id, String firstName, String lastName,
                                  String email, String contactNumber, String section,
                                  String password, String fatherName, String dob) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.section = section;
        this.password = password;
        this.fatherName = fatherName;
        this.dob = dob;
    }

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getSection() { return section; }
    public String getPassword() { return password; }
    public String getFatherName() { return fatherName; }
    public String getDob() { return dob; }
}
