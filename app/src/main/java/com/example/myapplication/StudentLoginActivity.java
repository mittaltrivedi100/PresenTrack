package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔁 Auto-login check
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String userType = prefs.getString("userType", "");
        String studentId = prefs.getString("student_id", "");

        if (isLoggedIn && userType.equals("student") && !studentId.isEmpty()) {
            Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
            intent.putExtra("studentId", studentId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return;
        }

        // 💡 Show login screen
        setContentView(R.layout.activity_student_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Email is required");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError("Enter a valid email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 8) {
                    edtPassword.setError("Minimum 8 characters required");
                    return;
                }

                boolean isValid = dbHelper.checkStudentLogin(email, password);

                if (isValid) {
                    String studentId = dbHelper.getStudentIdByEmail(email);
                    if (studentId == null || studentId.isEmpty()) {
                        Toast.makeText(StudentLoginActivity.this, "Student ID not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(StudentLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", "student");
                    editor.putString("student_id", studentId);
                    editor.apply();

                    Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
                    intent.putExtra("studentId", studentId); // ✔ send it as a String

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(StudentLoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentLoginActivity.this, StudentRegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentLoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
                // Later: Implement recovery logic here
            }
        });
    }
}
