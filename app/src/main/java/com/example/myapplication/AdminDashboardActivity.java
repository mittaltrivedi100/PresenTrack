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



public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout cardClass, cardStudent, cardStaff, cardTimetable;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        cardClass = findViewById(R.id.cardClass);
        cardStudent = findViewById(R.id.cardStudent);
        cardStaff = findViewById(R.id.cardTeacher);
        cardTimetable = findViewById(R.id.cardTimetable);

        cardClass.setOnClickListener(view -> startActivity(new Intent(this, ClassActivity.class)));
        cardStudent.setOnClickListener(view -> startActivity(new Intent(this, StudentActivity.class)));
        cardStaff.setOnClickListener(view -> startActivity(new Intent(this, TeacherActivity.class)));
        cardTimetable.setOnClickListener(view -> startActivity(new Intent(this, TimetableActivity.class)));
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
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_admin_profile, null);
        builder.setView(view);

        EditText editFirstName = view.findViewById(R.id.editFirstName);
        EditText editLastName = view.findViewById(R.id.editLastName);
        EditText editEmail = view.findViewById(R.id.editEmail);
        EditText editMobile = view.findViewById(R.id.editMobile);
        EditText editPassword = view.findViewById(R.id.editPassword);
        Button btnSave = view.findViewById(R.id.btnSaveProfile);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String adminId = prefs.getString("admin_id", null);

        if (adminId == null) {
            Toast.makeText(this, "Error: Admin session not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Admin WHERE id=?", new String[]{adminId});
        if (cursor.moveToFirst()) {
            editFirstName.setText(cursor.getString(1));
            editLastName.setText(cursor.getString(2));
            editEmail.setText(cursor.getString(3));
            editMobile.setText(cursor.getString(4));
            editPassword.setText(cursor.getString(5));
        }
        cursor.close();

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String mobile = editMobile.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    mobile.length() < 10 || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("firstName", firstName);
            values.put("lastName", lastName);
            values.put("email", email);
            values.put("mobile", mobile);
            values.put("password", password);

            int result = db.update("Admin", values, "id=?", new String[]{adminId});
            if (result > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Confirm logout
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

