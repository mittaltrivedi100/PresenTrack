package com.example.myapplication.models;
public class AttendanceRecord {
    private String studentName;
    private String subject;
    private String date;
    private boolean isPresent;

    public AttendanceRecord(String studentName, String subject, String date, boolean isPresent) {
        this.studentName = studentName;
        this.subject = subject;
        this.date = date;
        this.isPresent = isPresent;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public boolean isPresent() {
        return isPresent;
    }
}
