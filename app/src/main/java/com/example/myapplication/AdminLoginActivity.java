package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String userType = prefs.getString("userType", "");
        String adminId = prefs.getString("admin_id", "");

        if (isLoggedIn && userType.equals("admin") && !adminId.isEmpty()) {
            Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 💡 Show login UI
        setContentView(R.layout.activity_admin_login);

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

                // 🛡️ Validation
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

                boolean isValid = dbHelper.checkAdminLogin(email, password);
                if (isValid) {
                    String adminId = dbHelper.getAdminIdByEmail(email);

                    if (adminId == null || adminId.isEmpty()) {
                        Toast.makeText(AdminLoginActivity.this, "Admin ID not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(AdminLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // 🔐 Save session
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", "admin");
                    editor.putString("admin_id", adminId);
                    editor.apply();

                    Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 💥 Clear stack
                    startActivity(intent);
// No need to call finish() now

                } else {
                    Toast.makeText(AdminLoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, AdminRegisterActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminLoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
                // Implement password recovery here
            }
        });
    }
}
