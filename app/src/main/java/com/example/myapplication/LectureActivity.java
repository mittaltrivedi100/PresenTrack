package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.LectureAdapter;
import com.example.myapplication.models.LectureModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LectureActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddLecture;
    private List<LectureModel> lectureList;
    private LectureAdapter adapter;
    private DatabaseHelper dbHelper;
    private String timetableName;

    private AlertDialog addLectureDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        timetableName = getIntent().getStringExtra("timetableName");

        recyclerView = findViewById(R.id.recyclerViewLectures);
        fabAddLecture = findViewById(R.id.fabAddLecture);
        dbHelper = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lectureList = new ArrayList<>();
        adapter = new LectureAdapter(this, lectureList, dbHelper);
        recyclerView.setAdapter(adapter);

        loadLectures(); // Initial load

        fabAddLecture.setOnClickListener(v -> showAddLectureDialog());
    }

    private void loadLectures() {
        lectureList.clear();
        lectureList.addAll(dbHelper.getLecturesForTimetable(timetableName));
        adapter.notifyDataSetChanged();
    }

    private void showAddLectureDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_lecture, null);

        Spinner spinnerSection = dialogView.findViewById(R.id.spinnerSection);
        Spinner spinnerTeacher = dialogView.findViewById(R.id.spinnerTeacherName);
        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        EditText edtSubject = dialogView.findViewById(R.id.edtSubject);
        TimePicker timePickerStart = dialogView.findViewById(R.id.timePickerStart);
        TimePicker timePickerEnd = dialogView.findViewById(R.id.timePickerEnd);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Setup Spinners

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllSections());
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(sectionAdapter);

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllTeachersName());
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getDaysOfWeek());
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        addLectureDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            if (spinnerSection.getSelectedItem() == null || spinnerTeacher.getSelectedItem() == null || spinnerDay.getSelectedItem() == null) {
                Toast.makeText(this, "Please select all dropdowns", Toast.LENGTH_SHORT).show();
                return;
            }

            String sectionName = spinnerSection.getSelectedItem().toString();
            String teacherName = spinnerTeacher.getSelectedItem().toString();
            String day = spinnerDay.getSelectedItem().toString();
            String subject = edtSubject.getText().toString().trim();

            int startHour = timePickerStart.getHour();
            int startMinute = timePickerStart.getMinute();
            int endHour = timePickerEnd.getHour();
            int endMinute = timePickerEnd.getMinute();

            if (subject.isEmpty()) {
                Toast.makeText(this, "Subject is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startHour == endHour && startMinute == endMinute) {
                Toast.makeText(this, "Start time and end time cannot be the same", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isLectureDuplicate(timetableName, sectionName, teacherName, day, subject, startHour, startMinute, endHour, endMinute)) {
                Toast.makeText(this, "Lecture with same details already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            long insertedId = dbHelper.insertLecture(
                    timetableName, sectionName, teacherName, day, subject,
                    startHour, startMinute, endHour, endMinute
            );

            if (insertedId != -1) {
                String id = String.valueOf(insertedId); // Use real database ID
                LectureModel lecture = new LectureModel(id, timetableName, sectionName, teacherName,
                        day, subject, startHour, startMinute, endHour, endMinute);
                lectureList.add(lecture);
                adapter.notifyItemInserted(lectureList.size() - 1);
                Toast.makeText(this, "Lecture added successfully", Toast.LENGTH_SHORT).show();
                addLectureDialog.dismiss();
            } else {
                Toast.makeText(this, "Error adding lecture", Toast.LENGTH_SHORT).show();
            }

        });

        addLectureDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLectures(); // In case data changed from outside
    }

    @Override
    protected void onDestroy() {
        if (addLectureDialog != null && addLectureDialog.isShowing()) {
            addLectureDialog.dismiss();
        }
        super.onDestroy();
    }

    private String[] getDaysOfWeek() {
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }
}
