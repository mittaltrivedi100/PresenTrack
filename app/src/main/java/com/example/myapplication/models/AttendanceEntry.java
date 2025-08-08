package com.example.myapplication.models;



public class AttendanceEntry {
    private String date;
    private String subject;
    private boolean isPresent;

    public AttendanceEntry() {
    }

    public AttendanceEntry(String date, String subject, boolean isPresent) {
        this.date = date;
        this.subject = subject;
        this.isPresent = isPresent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
