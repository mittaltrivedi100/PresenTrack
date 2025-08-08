package com.example.myapplication.models;

public class TeacherModel {

    private String id;
    private String designation;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String dob;
    private String qualification;
    private String experience;
    private String phone;
    private String email;
    private String address;
    private String joiningDate;
    private String password; // ✅ Added

    // Updated Constructor with Password
    public TeacherModel(String id, String designation, String firstName, String lastName, String fatherName,
                        String dob, String qualification, String experience, String phone, String email,
                        String address, String joiningDate, String password) {
        this.id = id;
        this.designation = designation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.dob = dob;
        this.qualification = qualification;
        this.experience = experience;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.joiningDate = joiningDate;
        this.password = password;
    }


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
