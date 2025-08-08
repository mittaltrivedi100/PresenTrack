package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.TeacherLectureAdapter;
import com.example.myapplication.models.TeacherLectureModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AttendanceListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TeacherLectureAdapter adapter;
    ArrayList<TeacherLectureModel> lectureList = new ArrayList<>();
    DatabaseHelper dbHelper;
    int teacherId;
    String teacherName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        dbHelper = new DatabaseHelper(this);


        teacherId = getIntent().getIntExtra("teacherId", -1);
        if (teacherId == -1) {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String idStr = prefs.getString("teacher_id", "");
            if (!idStr.isEmpty()) {
                teacherId = Integer.parseInt(idStr);
            }
        }


        teacherName = getTeacherName(teacherId);

        recyclerView = findViewById(R.id.recyclerLectures);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ 3. Set up adapter
        adapter = new TeacherLectureAdapter(this, lectureList, lecture -> {
            Intent intent = new Intent(AttendanceListActivity.this, TakeAttendanceActivity.class);
            intent.putExtra("lectureId", lecture.getLectureId());
            intent.putExtra("className", lecture.getSectionName()); // or className if you have it
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // ✅ 4. Load lectures initially
        loadLectures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLectures();               // Refresh lecture list
        adapter.notifyDataSetChanged(); // Update adapter
    }

    private void loadLectures() {
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        lectureList.clear();

        // ✅ Query Lecture table using teacherName and current day
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Lecture WHERE teacherName = ? AND day = ?",
                new String[]{teacherName, currentDay}
        );

        while (cursor.moveToNext()) {
            int lectureId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String timetableName = cursor.getString(cursor.getColumnIndexOrThrow("timetableName"));
            String sectionName = cursor.getString(cursor.getColumnIndexOrThrow("sectionName"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
            int startHour = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeHour"));
            int startMin = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeMinute"));
            int endHour = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeHour"));
            int endMin = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeMinute"));

            String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMin);
            String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMin);

            TeacherLectureModel model = new TeacherLectureModel();
            model.setLectureId(lectureId);
            model.setTimetableName(timetableName);
            model.setSectionName(sectionName);
            model.setSubject(subject);
            model.setDay(day);
            model.setStartTime(startTime);
            model.setEndTime(endTime);

            lectureList.add(model);
        }

        cursor.close();
    }

    private String getTeacherName(int teacherId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String name = "";
        Cursor cursor = db.rawQuery(
                "SELECT firstName, lastName FROM Teacher WHERE id = ?",
                new String[]{String.valueOf(teacherId)}
        );
        if (cursor.moveToFirst()) {
            String first = cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
            String last = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
            name = first + " " + last;
        }
        cursor.close();
        return name;
    }
}
