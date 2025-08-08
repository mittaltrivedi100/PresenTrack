package com.example.myapplication;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.StudentAttendanceAdapter;
import com.example.myapplication.models.StudentAttendanceModel;
import com.example.myapplication.models.StudentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TakeAttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentAttendanceAdapter adapter;
    ArrayList<StudentAttendanceModel> studentList = new ArrayList<>();

    DatabaseHelper dbHelper;
    int lectureId;
    String className;
    String currentDate;


    HashMap<String, Integer> attendanceMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        dbHelper = new DatabaseHelper(this);

        lectureId = getIntent().getIntExtra("lectureId", -1);
        className = getIntent().getStringExtra("className");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        setTitle("Take Attendance - " + className);

        recyclerView = findViewById(R.id.recyclerStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadStudents();

        adapter = new StudentAttendanceAdapter(studentList, attendanceMap, (studentId) -> {
            // toggle attendance on click
            int currentStatus = attendanceMap.get(studentId) == 1 ? 0 : 1;
            attendanceMap.put(studentId, currentStatus);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        studentList.clear();
        attendanceMap.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE section=?", new String[]{className});
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
            String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
            String section = cursor.getString(cursor.getColumnIndex("section"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));  // Note 'phone', not 'contactNumber'
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String fatherName = cursor.getString(cursor.getColumnIndex("fatherName"));
            String dob = cursor.getString(cursor.getColumnIndex("dob"));
            String address = cursor.getString(cursor.getColumnIndex("address")); // You missed this earlier


            StudentAttendanceModel student = new StudentAttendanceModel( id, firstName, lastName, email, phone, section, password, fatherName, dob);

            studentList.add(student);

            // Default present
            attendanceMap.put(id, 1);
        }
        cursor.close();

        // Load existing attendance for this lecture and date, if any
        Cursor attCursor = db.rawQuery("SELECT studentId, status FROM Attendance WHERE lectureId=? AND date=?",
                new String[]{String.valueOf(lectureId), currentDate});

        if (attCursor.moveToFirst()) {
            do {
                int studentId = attCursor.getInt(attCursor.getColumnIndex("studentId"));
                int status = attCursor.getInt(attCursor.getColumnIndex("status"));
                attendanceMap.put(String.valueOf(studentId), status);
            } while (attCursor.moveToNext());
        }
        attCursor.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAttendance();
    }

    private void saveAttendance() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (String studentId : attendanceMap.keySet()) {
            int status = attendanceMap.get(studentId);

            // Check if attendance exists for student/lecture/date
            Cursor c = db.rawQuery("SELECT id FROM Attendance WHERE lectureId=? AND studentId=? AND date=?",
                    new String[]{String.valueOf(lectureId), String.valueOf(studentId), currentDate});

            if (c.moveToFirst()) {
                // Update existing
                int attendanceId = c.getInt(c.getColumnIndex("id"));
                db.execSQL("UPDATE Attendance SET status=? WHERE id=?", new Object[]{status, attendanceId});
            } else {
                // Insert new
                db.execSQL("INSERT INTO Attendance (lectureId, studentId, status, date) VALUES (?, ?, ?, ?)",
                        new Object[]{lectureId, studentId, status, currentDate});
            }
            c.close();
        }
        Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show();
    }
}

