package com.example.myapplication.models;
public class AttendanceDisplayModel {
    private String studentId;
    private String studentName;
    private Integer status; // 1 = present, 0 = absent, null = no record

    public AttendanceDisplayModel(String studentId, String studentName, Integer status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getStatus() {
        return status;
    }
}