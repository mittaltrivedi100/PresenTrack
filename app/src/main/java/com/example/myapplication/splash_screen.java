package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    private static final int SPLASH_TIME = 1000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            // Get saved login info
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            String userType = prefs.getString("userType", "");


            // If logged in, go to their dashboard
            if (isLoggedIn) {
                switch (userType) {
                    case "admin":
                        startActivity(new Intent(this, AdminDashboardActivity.class));
                        break;
                    case "teacher":
                        startActivity(new Intent(this, TeacherDashboardActivity.class));
                        break;
                    case "student":
                        startActivity(new Intent(this, StudentDashboardActivity.class));
                        break;
                    default:
                        startActivity(new Intent(this, RoleSelectionActivity.class));
                        break;
                }
            } else {
                // Not logged in → Go to Role selection
                startActivity(new Intent(this, RoleSelectionActivity.class));
            }

            finish(); // close splash screen
        }, SPLASH_TIME);
    }
}
