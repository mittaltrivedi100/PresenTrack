package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔁 Check if teacher is already logged in
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String userType = prefs.getString("userType", "");
        String teacherId = prefs.getString("teacher_id", "");

        if (isLoggedIn && userType.equals("teacher") && !teacherId.isEmpty()) {
            Intent intent = new Intent(TeacherLoginActivity.this, TeacherDashboardActivity.class);
            intent.putExtra("teacherId", Integer.parseInt(teacherId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return;
        }

        // 💡 Show login UI
        setContentView(R.layout.activity_teacher_login);

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
                    edtEmail.setError("Invalid email format");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 8) {
                    edtPassword.setError("Password must be at least 8 characters");
                    return;
                }

                boolean isValid = dbHelper.checkTeacherLogin(email, password);

                if (isValid) {
                    String teacherId = dbHelper.getTeacherIdByEmail(email);

                    if (teacherId == null || teacherId.isEmpty()) {
                        Toast.makeText(TeacherLoginActivity.this, "Teacher ID not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // 🔐 Save session
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", "teacher");
                    editor.putString("teacher_id", teacherId);
                    editor.apply();

                    Intent intent = new Intent(TeacherLoginActivity.this, TeacherDashboardActivity.class);
                    intent.putExtra("teacherId", Integer.parseInt(teacherId));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                } else {
                    Toast.makeText(TeacherLoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherLoginActivity.this, TeacherRegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TeacherLoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
