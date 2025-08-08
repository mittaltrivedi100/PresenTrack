package com.example.myapplication.models;

public class LectureModel {
    private String id;
    private String timetableName;
    private String section;
    private String teacherName;
    private String day;
    private String subject;
    private int startTimeHour;
    private int startTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;

    // Constructor
    public LectureModel(String id, String timetableName, String section, String teacherName,
                        String day, String subject, int startTimeHour, int startTimeMinute,
                        int endTimeHour, int endTimeMinute) {
        this.id = id;
        this.timetableName = timetableName;
        this.section = section;
        this.teacherName = teacherName;
        this.day = day;
        this.subject = subject;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(int startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public int getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(int startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    // Formatted time strings
    public String getStartTime() {
        return String.format("%02d:%02d", startTimeHour, startTimeMinute);
    }

    public String getEndTime() {
        return String.format("%02d:%02d", endTimeHour, endTimeMinute);
    }

    // ToString
    @Override
    public String toString() {
        return "LectureModel{" +
                "id='" + id + '\'' +
                ", timetableName='" + timetableName + '\'' +
                ", section='" + section + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", day='" + day + '\'' +
                ", subject='" + subject + '\'' +
                ", startTimeHour=" + startTimeHour +
                ", startTimeMinute=" + startTimeMinute +
                ", endTimeHour=" + endTimeHour +
                ", endTimeMinute=" + endTimeMinute +
                '}';
    }
}
