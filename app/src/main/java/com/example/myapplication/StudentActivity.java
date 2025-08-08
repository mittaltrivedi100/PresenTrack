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

import com.example.myapplication.adapters.StudentAdapter;
import com.example.myapplication.models.StudentModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private StudentAdapter studentAdapter;
    private ArrayList<StudentModel> studentList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAddStudent);
        dbHelper = new DatabaseHelper(this);

        studentList = dbHelper.getAllStudents();
        studentAdapter = new StudentAdapter(this, studentList, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(studentAdapter);

        fabAdd.setOnClickListener(v -> openAddStudentDialog());
        Spinner spinnerFilter = findViewById(R.id.spinnerSectionFilter);

        ArrayList<String> sections = dbHelper.getSections();
        if (!sections.contains("All Sections")) {
            sections.add(0, "All Sections");
        }

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sections);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(sectionAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSection = parent.getItemAtPosition(position).toString();
                if (selectedSection.equals("All Sections")) {
                    studentList.clear();
                    studentList.addAll(dbHelper.getAllStudents());
                } else {
                    studentList.clear();
                    studentList.addAll(dbHelper.getStudentsBySection(selectedSection));
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void openAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_student, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Input references
        EditText edtId = dialogView.findViewById(R.id.edtStudentId);
        EditText edtFirstName = dialogView.findViewById(R.id.edtFirstName);
        EditText edtLastName = dialogView.findViewById(R.id.edtLastName);
        EditText edtFatherName = dialogView.findViewById(R.id.edtFatherName);
        EditText edtDob = dialogView.findViewById(R.id.edtDob);
        Spinner spinnerSection = dialogView.findViewById(R.id.spinnerSection);
        EditText edtPhone = dialogView.findViewById(R.id.edtContact);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Populate section spinner dynamically from DB
        ArrayList<String> sections = dbHelper.getSections();
        if (sections.isEmpty()) {
            sections.add("A");
            sections.add("B");
        }

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sections);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(sectionAdapter);


        edtDob.setOnClickListener(v -> showDatePickerDialog(edtDob));

        btnSave.setOnClickListener(v -> {
            String id = edtId.getText().toString().trim();
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String fatherName = edtFatherName.getText().toString().trim();
            String dob = edtDob.getText().toString().trim();
            String section = spinnerSection.getSelectedItem().toString();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(section) || TextUtils.isEmpty(email)) {
                Toast.makeText(StudentActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }


            if (dbHelper.isStudentIdExists(id)) {
                Toast.makeText(StudentActivity.this, "Student ID already exists", Toast.LENGTH_LONG).show();
                return;
            }
            if (!phone.matches("[0-9]{10,}")) {
                Toast.makeText(StudentActivity.this, "Enter valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isStudentEmailExists(email)) {
                Toast.makeText(StudentActivity.this, "Email already registered", Toast.LENGTH_LONG).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(StudentActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 8) {
                Toast.makeText(StudentActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            long inserted = dbHelper.insertStudent(
                    id, firstName, lastName, fatherName, dob, section,
                    phone, email, address, password
            );

            if (inserted != -1) {
                StudentModel model = new StudentModel(
                        id, firstName, lastName, fatherName, dob,
                        section, phone, email, address, password
                );
                studentList.add(model);
                studentAdapter.notifyItemInserted(studentList.size() - 1);
                Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to add student", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(StudentActivity.this,
                (view, year, month, dayOfMonth) ->
                        editText.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
