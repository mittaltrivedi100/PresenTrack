package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapters.ClassAdapter;
import com.example.myapplication.models.ClassModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.UUID;

public class ClassActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddClass;
    private ArrayList<ClassModel> classList;
    private ClassAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddClass = findViewById(R.id.fabAddClass);
        dbHelper = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        classList = dbHelper.getAllClasses();
        adapter = new ClassAdapter(this, classList, dbHelper);
        recyclerView.setAdapter(adapter);

        fabAddClass.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_class, null);
        EditText edtClassName = dialogView.findViewById(R.id.edtClassName);
        EditText edtSection = dialogView.findViewById(R.id.edtSection);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        btnSave.setOnClickListener(v -> {
            String name = edtClassName.getText().toString().trim();
            String section = edtSection.getText().toString().trim();

            if (name.isEmpty() || section.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isClassExists(name, section)) {
                Toast.makeText(this, "Class with same name and section already exists", Toast.LENGTH_LONG).show();
                return;
            }

            String id = UUID.randomUUID().toString();
            dbHelper.insertClass(id, name, section);
            classList.add(new ClassModel(id, name, section));
            adapter.notifyItemInserted(classList.size() - 1);
            Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
