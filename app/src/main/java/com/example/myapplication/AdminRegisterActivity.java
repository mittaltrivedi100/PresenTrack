package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AdminRegisterActivity extends AppCompatActivity {

    EditText edtAdminId, edtFirstName, edtLastName, edtEmail, edtMobile, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvAlreadyAccount;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        // Initialize views
        edtAdminId = findViewById(R.id.edtAdminId);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);

        dbHelper = new DatabaseHelper(this);

        // Register button click
        btnRegister.setOnClickListener(v -> registerAdmin());

        // Redirect to login
        tvAlreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(AdminRegisterActivity.this, AdminLoginActivity.class));
            finish();
        });
    }

    private void registerAdmin() {
        String adminId = edtAdminId.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (adminId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if admin ID already exists
        if (dbHelper.isAdminIdExists(adminId)) {
            Toast.makeText(this, "Admin ID already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert admin
        boolean inserted = dbHelper.insertAdmin(adminId, firstName, lastName, email, mobile, password);

        if (inserted) {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
