package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.TeacherAdapter;
import com.example.myapplication.models.TeacherModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class TeacherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TeacherAdapter teacherAdapter;
    private ArrayList<TeacherModel> teacherList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        recyclerView = findViewById(R.id.recyclerViewTeachers);
        fabAdd = findViewById(R.id.fabAddTeacher);
        dbHelper = new DatabaseHelper(this);

        teacherList = dbHelper.getAllTeachers();
        teacherAdapter = new TeacherAdapter(this, teacherList, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(teacherAdapter);

        fabAdd.setOnClickListener(v -> openAddTeacherDialog());
    }

    private void openAddTeacherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_teacher, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Reference all input fields
        EditText edtId = dialogView.findViewById(R.id.edtId);
        EditText edtDesignation = dialogView.findViewById(R.id.edtDesignation);
        EditText edtFirstName = dialogView.findViewById(R.id.edtFirstName);
        EditText edtLastName = dialogView.findViewById(R.id.edtLastName);
        EditText edtFatherName = dialogView.findViewById(R.id.edtFatherName);
        EditText edtDob = dialogView.findViewById(R.id.edtDob);
        EditText edtQualification = dialogView.findViewById(R.id.edtQualification);
        EditText edtExperience = dialogView.findViewById(R.id.edtExperience);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtJoiningDate = dialogView.findViewById(R.id.edtJoiningDate);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Date pickers for DOB and Joining Date
        edtDob.setOnClickListener(v -> showDatePickerDialog(edtDob));
        edtJoiningDate.setOnClickListener(v -> showDatePickerDialog(edtJoiningDate));

        btnSave.setOnClickListener(v -> {
            // Get input values
            String id = edtId.getText().toString().trim();
            String designation = edtDesignation.getText().toString().trim();
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String fatherName = edtFatherName.getText().toString().trim();
            String dob = edtDob.getText().toString().trim();
            String qualification = edtQualification.getText().toString().trim();
            String experience = edtExperience.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String joiningDate = edtJoiningDate.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(designation) || TextUtils.isEmpty(firstName) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(TeacherActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!phone.matches("[0-9]{10,}")) {
                Toast.makeText(TeacherActivity.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }


            if (password.length() < 8) {
                Toast.makeText(TeacherActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(TeacherActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }


            TeacherModel model = new TeacherModel(id, designation, firstName, lastName, fatherName, dob,
                    qualification, experience, phone, email, address, joiningDate, password);

            long inserted = dbHelper.insertTeacher(
                    id, designation, firstName, lastName, fatherName, dob,
                    qualification, experience, phone, email, address, joiningDate, password
            );

            if (inserted != -1) {
                teacherList.add(model);
                teacherAdapter.notifyItemInserted(teacherList.size() - 1);
                Toast.makeText(this, "Teacher added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to add teacher", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherActivity.this,
                (view, year, month, dayOfMonth) ->
                        editText.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
