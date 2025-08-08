package com.example.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.models.StudentModel;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private List<StudentModel> studentList;
    private DatabaseHelper db;
    private List<String> sectionList;

    public StudentAdapter(Context context, List<StudentModel> studentList, DatabaseHelper db) {
        this.context = context;
        this.studentList = studentList;
        this.db = db;
        this.sectionList = db.getSections(); // Fetch all available sections
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);

        holder.tvName.setText(student.getFirstName() + " " + student.getLastName());
        holder.tvSectionId.setText("Section: " + student.getSection());
        holder.tvEmail.setText(student.getEmail());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(student, position));
        holder.btnDelete.setOnClickListener(v -> {
            db.deleteStudent(student.getId());
            studentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, studentList.size());
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(StudentModel student, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_student, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtFirstName = dialogView.findViewById(R.id.edtFirstName);
        EditText edtLastName = dialogView.findViewById(R.id.edtLastName);
        EditText edtFatherName = dialogView.findViewById(R.id.edtFatherName);
        EditText edtDob = dialogView.findViewById(R.id.edtDob);
        Spinner spinnerSection = dialogView.findViewById(R.id.spinnerSection);
        EditText edtPhone = dialogView.findViewById(R.id.edtContact);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);

        // Pre-fill fields
        edtFirstName.setText(student.getFirstName());
        edtLastName.setText(student.getLastName());
        edtFatherName.setText(student.getFatherName());
        edtDob.setText(student.getDob());
        edtPhone.setText(student.getPhone());
        edtEmail.setText(student.getEmail());
        edtAddress.setText(student.getAddress());
        edtPassword.setText(student.getPassword());

        // Setup section spinner
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sectionList);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(sectionAdapter);

        int sectionPosition = (student.getSection() != null) ? sectionList.indexOf(student.getSection()) : -1;
        if (sectionPosition >= 0) {
            spinnerSection.setSelection(sectionPosition);
        }

        // Save edited data
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String id = student.getId();

            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String fatherName = edtFatherName.getText().toString();
            String dob = edtDob.getText().toString();
            Object selectedSection = spinnerSection.getSelectedItem();
            String section = (selectedSection != null) ? selectedSection.toString() : "";
            String phone = edtPhone.getText().toString();
            String email = edtEmail.getText().toString();
            String address = edtAddress.getText().toString();
            String password = edtPassword.getText().toString();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int result = db.updateStudent(id, firstName, lastName, fatherName, dob, section, phone, email, address, password);
            if (result > 0) {
                StudentModel updated = new StudentModel(id, firstName, lastName, fatherName, dob, section, phone, email, address, password);
                studentList.set(position, updated);
                notifyItemChanged(position);
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSectionId, tvEmail;
        TextView btnEdit, btnDelete;
        CardView card;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSectionId = itemView.findViewById(R.id.tvSectionId);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            card = itemView.findViewById(R.id.cardStudent);
        }
    }
}
