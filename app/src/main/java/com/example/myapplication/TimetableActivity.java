package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.TimetableAdapter;
import com.example.myapplication.models.TimetableModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TimetableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<TimetableModel> timetableList;
    private TimetableAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerTimetable);
        fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTimetables();

        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadTimetables() {
        timetableList = db.getAllTimetables();
        adapter = new TimetableAdapter(this, timetableList, db);
        recyclerView.setAdapter(adapter);
    }

    private void showAddDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_timetable, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtTimetableName = dialogView.findViewById(R.id.edtTimetableName);

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = edtTimetableName.getText().toString().trim();

            if (db.isTimetableNameExists(name)) {
                Toast.makeText(this, "Timetable with this name already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter timetable name", Toast.LENGTH_SHORT).show();
                return;
            }

            TimetableModel model = new TimetableModel(name);
            db.insertTimetable(model);
            loadTimetables();
            dialog.dismiss();
        });

        dialog.show();
    }
}
