package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherRegisterActivity extends AppCompatActivity {

    EditText edtTeacherId, edtDesignation, edtFirstName, edtLastName, edtFatherName, edtDOB,
            edtQualification, edtExperience, edtPhone, edtEmail, edtAddress, edtJoiningDate,
            edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvAlreadyAccount;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        // Initialize Views
        edtTeacherId = findViewById(R.id.edtTeacherId);
        edtDesignation = findViewById(R.id.edtDesignation);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtFatherName = findViewById(R.id.edtFatherName);
        edtDOB = findViewById(R.id.edtDOB);
        edtQualification = findViewById(R.id.edtQualification);
        edtExperience = findViewById(R.id.edtExperience);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtJoiningDate = findViewById(R.id.edtJoiningDate);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);

        dbHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> registerTeacher());

        tvAlreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(TeacherRegisterActivity.this, TeacherLoginActivity.class));
            finish();
        });
    }

    private void registerTeacher() {
        String idStr = edtTeacherId.getText().toString().trim();
        String designation = edtDesignation.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String fatherName = edtFatherName.getText().toString().trim();
        String dob = edtDOB.getText().toString().trim();
        String qualification = edtQualification.getText().toString().trim();
        String experience = edtExperience.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String joiningDate = edtJoiningDate.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate all required fields
        if (idStr.isEmpty() || designation.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                fatherName.isEmpty() || dob.isEmpty() || qualification.isEmpty() ||
                experience.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                address.isEmpty() || joiningDate.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate integer ID
        if (!idStr.matches("\\d+")) {
            Toast.makeText(this, "Teacher ID must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone
        if (phone.length() < 10) {
            Toast.makeText(this, "Phone number must be at least 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password match and strength
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if teacher ID already exists
        if (dbHelper.isTeacherIdExists(idStr)) {
            Toast.makeText(this, "Teacher ID already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to insert
        long result = dbHelper.insertTeacher(idStr, designation, firstName, lastName, fatherName,
                dob, qualification, experience, phone, email, address, joiningDate, password);

        if (result != -1) {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TeacherLoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
