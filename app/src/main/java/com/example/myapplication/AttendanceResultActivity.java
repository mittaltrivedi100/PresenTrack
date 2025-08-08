package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.AttendanceRecord;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

public class AttendanceResultActivity extends AppCompatActivity {

    private static final int CREATE_FILE_REQUEST_CODE = 1234;

    TableLayout tableAttendance;
    TextView tvTitle;
    DatabaseHelper dbHelper;
    boolean showNameColumn = true;
    List<AttendanceRecord> records;
    boolean isSingleStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        tvTitle = findViewById(R.id.tvResultTitle);
        tableAttendance = findViewById(R.id.tableAttendance);
        dbHelper = new DatabaseHelper(this);
        Button btnExportExcel = findViewById(R.id.btnExportExcel);

        // Receive intent data
        int lectureId = getIntent().getIntExtra("lectureId", -1);
        String studentId = getIntent().getStringExtra("studentId");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");

        if (lectureId == -1 || fromDate == null || toDate == null) {
            tvTitle.setText("Invalid data provided");
            return;
        }

        if ("all".equals(studentId)) {
            tvTitle.setText("📊 Attendance for All Students");
            records = dbHelper.getAttendanceForAllStudents(lectureId, fromDate, toDate);
            showNameColumn = true;
            isSingleStudent = false;
        } else {
            String studentName = dbHelper.getStudentNameById(studentId);
            tvTitle.setText("📊 Attendance for " + studentName);
            records = dbHelper.getAttendanceForSingleStudent(String.valueOf(lectureId), studentId, fromDate, toDate);
            showNameColumn = false;
            isSingleStudent = true;
        }

        showTable(records);

        btnExportExcel.setOnClickListener(v -> {
            if (records == null || records.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            } else {
                createFile();
            }
        });
    }

    private void showTable(List<AttendanceRecord> records) {
        tableAttendance.removeAllViews();

        TableRow headerRow = new TableRow(this);
        if (showNameColumn) headerRow.addView(createTextView("Student", true));
        headerRow.addView(createTextView("Date", true));
        headerRow.addView(createTextView("Subject", true));
        headerRow.addView(createTextView("Status", true));
        tableAttendance.addView(headerRow);

        for (AttendanceRecord record : records) {
            TableRow row = new TableRow(this);
            if (showNameColumn) row.addView(createTextView(record.getStudentName(), false));
            row.addView(createTextView(record.getDate(), false));
            row.addView(createTextView(record.getSubject(), false));
            row.addView(createTextView(record.isPresent() ? "Present" : "Absent", false));
            tableAttendance.addView(row);
        }

        if (records.isEmpty()) {
            TableRow noDataRow = new TableRow(this);
            TextView noData = new TextView(this);
            noData.setText("No attendance records found.");
            noData.setPadding(16, 16, 16, 16);
            noDataRow.addView(noData);
            tableAttendance.addView(noDataRow);
        }
    }

    private TextView createTextView(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 8, 12, 8);
        tv.setTextSize(isHeader ? 16 : 14);
        tv.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        return tv;
    }

    // Launch Storage Access Framework (SAF) to create file
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "Attendance_Report_" + System.currentTimeMillis() + ".xlsx");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                exportToExcel(uri);
            }
        }
    }

    // Save Excel file to selected Uri
    private void exportToExcel(Uri uri) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            int col = 0;
            if (showNameColumn) header.createCell(col++).setCellValue("Student");
            header.createCell(col++).setCellValue("Date");
            header.createCell(col++).setCellValue("Subject");
            header.createCell(col).setCellValue("Status");

            int rowNum = 1;
            for (AttendanceRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                int c = 0;
                if (showNameColumn) row.createCell(c++).setCellValue(record.getStudentName());
                row.createCell(c++).setCellValue(record.getDate());
                row.createCell(c++).setCellValue(record.getSubject());
                row.createCell(c).setCellValue(record.isPresent() ? "Present" : "Absent");
            }

            OutputStream out = getContentResolver().openOutputStream(uri);
            workbook.write(out);
            out.close();
            workbook.close();
            Toast.makeText(this, "Excel saved successfully!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save Excel file", Toast.LENGTH_SHORT).show();
        }
    }
}
