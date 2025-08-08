package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapters.AttendanceAdapter;
import com.example.myapplication.models.AttendanceDisplayModel;
import com.example.myapplication.models.ViewAttendanceLectureModel;
import com.example.myapplication.models.ViewAttendanceStudentModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceViewActivity extends AppCompatActivity {

    private Spinner spinnerTimetable, spinnerLecture;
    private EditText editTextDate;
    private RecyclerView recyclerViewAttendance;

    private DatabaseHelper dbHelper;

    private List<String> timetableNames;
    private List<ViewAttendanceLectureModel> lectureList;
    private List<ViewAttendanceStudentModel> studentList;
    private List<AttendanceDisplayModel> attendanceDisplayList;

    private AttendanceAdapter attendanceAdapter;

    private String selectedTimetableName = "";
    private int selectedLectureId = -1;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_view);

        dbHelper = new DatabaseHelper(this);

        spinnerTimetable = findViewById(R.id.spinnerTimetable);
        spinnerLecture = findViewById(R.id.spinnerLecture);
        editTextDate = findViewById(R.id.editTextDate);
        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);

        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));

        attendanceDisplayList = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(attendanceDisplayList);
        recyclerViewAttendance.setAdapter(attendanceAdapter);

        loadTimetableNames();

        spinnerTimetable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    selectedTimetableName = timetableNames.get(position);
                    loadLecturesByTimetableName(selectedTimetableName);
                    attendanceDisplayList.clear();
                    attendanceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerLecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < lectureList.size()) {
                    ViewAttendanceLectureModel lecture = lectureList.get(position);
                    selectedLectureId = Integer.parseInt(lecture.getId());
                    attendanceDisplayList.clear();
                    attendanceAdapter.notifyDataSetChanged();
                    loadAttendanceList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        editTextDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void loadTimetableNames() {
        timetableNames = dbHelper.getAllTimetableNames(); // returns List<String>
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timetableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimetable.setAdapter(adapter);
    }
    private void loadLecturesByTimetableName(String timetableName) {
        lectureList = dbHelper.getLecturesByTimetableName(timetableName);
        List<String> lectureNames = new ArrayList<>();
        for (ViewAttendanceLectureModel lecture : lectureList) {
            lectureNames.add(lecture.getName()); 
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lectureNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLecture.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    month = month + 1;
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);
                    editTextDate.setText(selectedDate);
                    loadAttendanceList();
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void loadAttendanceList() {
        if (selectedLectureId == -1 || selectedDate.isEmpty()) {
            attendanceDisplayList.clear();
            attendanceAdapter.notifyDataSetChanged();
            return;
        }

        studentList = dbHelper.getStudentsByLecture(selectedLectureId, selectedDate);
        attendanceDisplayList.clear();

        for (ViewAttendanceStudentModel student : studentList) {
            Integer status = dbHelper.getAttendanceStatus(Integer.parseInt(student.getId()), selectedLectureId, selectedDate);
            attendanceDisplayList.add(new AttendanceDisplayModel(student.getId(), student.getName(), status));
        }

        attendanceAdapter.notifyDataSetChanged();
    }
}
