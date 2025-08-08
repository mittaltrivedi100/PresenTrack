package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapters.LectureAdapter;
import com.example.myapplication.models.AttendanceLectureModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarkAttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    com.example.myapplication.AttendanceLectureAdapter adapter;
    String teacherName, todayDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        recyclerView = findViewById(R.id.recyclerLectures);
        db = new DatabaseHelper(this);

        // Example: Get logged-in teacher name from intent/session
        teacherName = getIntent().getStringExtra("teacherName"); // Must be passed from login
        if (teacherName == null) teacherName = "Default Teacher";

        // Get current day of week
        todayDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        List<AttendanceLectureModel> lectureList = db.getLecturesForTeacherAndDay(teacherName, todayDay);
        com.example.myapplication.AttendanceLectureAdapter adapter = new com.example.myapplication.AttendanceLectureAdapter(lectureList, this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
