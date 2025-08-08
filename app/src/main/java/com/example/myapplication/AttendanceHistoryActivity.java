// AttendanceHistoryActivity.java
package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.AttendanceHistoryAdapter;
import com.example.myapplication.models.AttendanceRecord;
import com.example.myapplication.models.TimetableModel;
import com.example.myapplication.models.LectureModel;

import java.text.SimpleDateFormat;
import java.util.*;

public class AttendanceHistoryActivity extends AppCompatActivity {

    Spinner spinnerTimetable, spinnerLecture, spinnerStudent;
    TextView tvFromDate, tvToDate;
    Button btnSearchAttendance;
    RecyclerView recyclerAttendanceHistory;

    DatabaseHelper dbHelper;
    Calendar fromDateCalendar, toDateCalendar;
    SimpleDateFormat sdf;

    List<TimetableModel> timetableModels;
    List<LectureModel> lectureModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        dbHelper = new DatabaseHelper(this);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        spinnerTimetable = findViewById(R.id.spinnerTimetable);
        spinnerLecture = findViewById(R.id.spinnerLecture);
        spinnerStudent = findViewById(R.id.spinnerStudent);
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnSearchAttendance = findViewById(R.id.btnSearchAttendance);
        recyclerAttendanceHistory = findViewById(R.id.recyclerAttendanceHistory);
        recyclerAttendanceHistory.setLayoutManager(new LinearLayoutManager(this));

        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();

        loadTimetables();
        loadStudents();

        tvFromDate.setOnClickListener(v -> pickDate(fromDateCalendar, tvFromDate));
        tvToDate.setOnClickListener(v -> pickDate(toDateCalendar, tvToDate));

        spinnerTimetable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimetableModel selectedTimetable = timetableModels.get(position);
                loadLectures(selectedTimetable.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSearchAttendance.setOnClickListener(v -> fetchAttendanceData());
    }

    private void loadTimetables() {
        timetableModels = dbHelper.getAllTimetables();
        List<String> names = new ArrayList<>();
        for (TimetableModel model : timetableModels) {
            names.add(model.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
        spinnerTimetable.setAdapter(adapter);
    }

    private void loadLectures(String timetableName) {
        lectureModels = dbHelper.getLectureModelsByTimetable(timetableName);
        List<String> lectureDisplayList = new ArrayList<>();
        for (LectureModel model : lectureModels) {
            lectureDisplayList.add(model.getSubject());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lectureDisplayList);
        spinnerLecture.setAdapter(adapter);
    }

    private void loadStudents() {
        List<String> students = new ArrayList<>();
        students.add("All Students");

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT id, firstName, lastName FROM Student", null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String first = cursor.getString(1);
                String last = cursor.getString(2);
                students.add(id + " - " + first + " " + last);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, students);
        spinnerStudent.setAdapter(adapter);
    }

    private void pickDate(Calendar calendar, TextView targetView) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            targetView.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fetchAttendanceData() {
        if (lectureModels.isEmpty()) return;

        int lecturePosition = spinnerLecture.getSelectedItemPosition();
        int lectureId = Integer.parseInt(lectureModels.get(lecturePosition).getId());

        String studentSelection = spinnerStudent.getSelectedItem().toString();
        String studentId = "all";
        if (!studentSelection.equals("All Students")) {
            studentId = studentSelection.split(" - ")[0]; // Extract ID
        }

        String fromDate = tvFromDate.getText().toString();
        String toDate = tvToDate.getText().toString();

        Intent intent = new Intent(this, AttendanceResultActivity.class);
        intent.putExtra("lectureId", lectureId);
        intent.putExtra("studentId", studentId);
        intent.putExtra("fromDate", fromDate);
        intent.putExtra("toDate", toDate);
        startActivity(intent);
    }
}
