package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.myapplication.models.AttendanceEntry;  // Use AttendanceEntry here
// Removed import of AttendanceRecord

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

public class StudentDashboardActivity extends AppCompatActivity {

    TextView tvFromDate, tvToDate;
    Spinner spinnerSubject, spinnerDay;
    Button btnFilter;
    Button btnLogout;
    TableLayout tableAttendance;
    LinearLayout progressContainer;
    PieChart pieChart;


    DatabaseHelper dbHelper;
    String studentId;
    SimpleDateFormat sdf;
    Calendar fromCalendar, toCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getString("student_id", null);
        if (studentId == null) {
            Toast.makeText(this, "Login session expired!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
        pieChart = findViewById(R.id.pieChart);

        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerDay = findViewById(R.id.spinnerDay);
        btnFilter = findViewById(R.id.btnFilter);
        tableAttendance = findViewById(R.id.tableAttendance);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
            editor.clear(); // Clear all login session
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to login activity
            Intent intent = new Intent(StudentDashboardActivity.this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
        });



        tvFromDate.setOnClickListener(v -> pickDate(fromCalendar, tvFromDate));
        tvToDate.setOnClickListener(v -> pickDate(toCalendar, tvToDate));

        loadSubjects();
        loadDays();

        btnFilter.setOnClickListener(v -> applyFilters());
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Minimize the app instead of going back to login
    }

    private void pickDate(Calendar calendar, TextView target) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            target.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadSubjects() {
        List<String> subjects = dbHelper.getSubjectsForStudent(studentId);
        subjects.add(0, "All");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        spinnerSubject.setAdapter(adapter);
    }

    private void loadDays() {
        List<String> days = Arrays.asList("All", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","Sunday");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        spinnerDay.setAdapter(adapter);
    }

    private void applyFilters() {
        String subject = spinnerSubject.getSelectedItem().toString();
        String day = spinnerDay.getSelectedItem().toString();
        String fromDate = tvFromDate.getText().toString();
        String toDate = tvToDate.getText().toString();

        List<AttendanceEntry> filtered = dbHelper.filterAttendance(studentId, subject, day, fromDate, toDate);
        showTable(filtered);
        showChart(filtered);
    }

    private void showTable(List<AttendanceEntry> records) {
        tableAttendance.removeAllViews();

        TableRow header = new TableRow(this);
        header.addView(createCell("Date", true));
        header.addView(createCell("Subject", true));
        header.addView(createCell("Status", true));
        tableAttendance.addView(header);

        for (AttendanceEntry r : records) {
            TableRow row = new TableRow(this);
            row.addView(createCell(r.getDate(), false));
            row.addView(createCell(r.getSubject(), false));
            row.addView(createCell(r.isPresent() ? "Present" : "Absent", false));
            tableAttendance.addView(row);
        }
    }

    private TextView createCell(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 8, 12, 8);
        tv.setTextSize(isHeader ? 16 : 14);
        tv.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        return tv;
    }

    private void showChart(List<AttendanceEntry> records) {
        Map<String, Integer> total = new LinkedHashMap<>();
        Map<String, Integer> present = new LinkedHashMap<>();

        for (AttendanceEntry r : records) {
            String subject = r.getSubject();
            total.put(subject, total.getOrDefault(subject, 0) + 1);
            if (r.isPresent()) present.put(subject, present.getOrDefault(subject, 0) + 1);
        }

        List<PieEntry> entries = new ArrayList<>();

        for (String subject : total.keySet()) {
            float percent = present.getOrDefault(subject, 0) * 100f / total.get(subject);
            entries.add(new PieEntry(percent, subject));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Attendance %");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

}
