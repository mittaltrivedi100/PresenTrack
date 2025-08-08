package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class TeacherDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout cardMarkAttendance, cardUpdateAttendance, cardAttendanceHistory;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        dbHelper = new DatabaseHelper(this);
        int teacherId = getIntent().getIntExtra("teacherId", -1);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.teacher_navigation_view);
        toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        cardMarkAttendance = findViewById(R.id.cardMarkAttendance);
        cardUpdateAttendance = findViewById(R.id.cardUpdateAttendance);
        cardAttendanceHistory = findViewById(R.id.cardAttendanceHistory);

        cardMarkAttendance.setOnClickListener(view -> {
            Intent intent = new Intent(this, AttendanceListActivity.class);
            intent.putExtra("teacherId", teacherId); // Passing teacherId to the next activity
            startActivity(intent);
        });
        cardUpdateAttendance.setOnClickListener(view -> startActivity(new Intent(this, AttendanceViewActivity.class)));
        cardAttendanceHistory.setOnClickListener(view -> startActivity(new Intent(this, AttendanceHistoryActivity.class)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            showEditProfileDialog();
        } else if (id == R.id.nav_logout) {
            showLogoutConfirmation();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_teacher_profile, null);
        builder.setView(view);

        EditText editFirstName = view.findViewById(R.id.editFirstName);
        EditText editLastName = view.findViewById(R.id.editLastName);
        EditText editEmail = view.findViewById(R.id.editEmail);
        EditText editPhone = view.findViewById(R.id.editMobile); // Corrected to 'phone'
        EditText editPassword = view.findViewById(R.id.editPassword);
        Button btnSave = view.findViewById(R.id.btnSaveProfile);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", null);

        if (teacherId == null) {
            Toast.makeText(this, "Error: Teacher session not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Teacher WHERE id=?", new String[]{teacherId});
        if (cursor.moveToFirst()) {
            // Ensure correct column index for each field
            editFirstName.setText(cursor.getString(cursor.getColumnIndex("firstName")));
            editLastName.setText(cursor.getString(cursor.getColumnIndex("lastName")));
            editEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
            editPhone.setText(cursor.getString(cursor.getColumnIndex("phone")));  // Corrected to 'phone'
            editPassword.setText(cursor.getString(cursor.getColumnIndex("password")));
        }
        cursor.close();

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phone.length() < 10 || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("firstName", firstName);
            values.put("lastName", lastName);
            values.put("email", email);
            values.put("phone", phone);  // Corrected to 'phone'
            values.put("password", password);

            // Update teacher's profile in database
            int result = db.update("Teacher", values, "id=?", new String[]{teacherId});
            if (result > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(this, RoleSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
