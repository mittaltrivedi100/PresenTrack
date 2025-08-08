package com.example.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.models.TeacherModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private Context context;
    private List<TeacherModel> teacherList;
    private DatabaseHelper db;

    public TeacherAdapter(Context context, List<TeacherModel> teacherList, DatabaseHelper db) {
        this.context = context;
        this.teacherList = teacherList;
        this.db = db;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_item, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = teacherList.get(position);

        holder.tvName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        holder.tvDesignation.setText("Designation: " + teacher.getDesignation());
        holder.tvEmail.setText(teacher.getEmail());
        holder.btnEdit.setOnClickListener(v -> showEditDialog(teacher, position));

        holder.btnDelete.setOnClickListener(v -> {
            db.deleteTeacher(teacher.getId());
            teacherList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, teacherList.size());
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(TeacherModel teacher, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_teacher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtId = dialogView.findViewById(R.id.edtId);
        EditText edtDesignation = dialogView.findViewById(R.id.edtDesignation);
        EditText edtFirstName = dialogView.findViewById(R.id.edtFirstName);
        EditText edtLastName = dialogView.findViewById(R.id.edtLastName);
        EditText edtFatherName = dialogView.findViewById(R.id.edtFatherName);
        EditText edtDob = dialogView.findViewById(R.id.edtDob);
        EditText edtQualification = dialogView.findViewById(R.id.edtQualification);
        EditText edtExperience = dialogView.findViewById(R.id.edtExperience);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtJoiningDate = dialogView.findViewById(R.id.edtJoiningDate);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);

        // Pre-fill values
        edtId.setText(teacher.getId());
        edtId.setEnabled(false); // ✅ User cannot change ID
        edtDesignation.setText(teacher.getDesignation());
        edtFirstName.setText(teacher.getFirstName());
        edtLastName.setText(teacher.getLastName());
        edtFatherName.setText(teacher.getFatherName());
        edtDob.setText(teacher.getDob());
        edtQualification.setText(teacher.getQualification());
        edtExperience.setText(teacher.getExperience());
        edtPhone.setText(teacher.getPhone());
        edtEmail.setText(teacher.getEmail());
        edtAddress.setText(teacher.getAddress());
        edtJoiningDate.setText(teacher.getJoiningDate());
        edtPassword.setText(teacher.getPassword());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {

            String designation = edtDesignation.getText().toString();
            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String fatherName = edtFatherName.getText().toString();
            String dob = edtDob.getText().toString();
            String qualification = edtQualification.getText().toString();
            String experience = edtExperience.getText().toString();
            String phone = edtPhone.getText().toString();
            String email = edtEmail.getText().toString();
            String address = edtAddress.getText().toString();
            String joiningDate = edtJoiningDate.getText().toString();

            String password = edtPassword.getText().toString();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            TeacherModel updated = new TeacherModel(
                    teacher.getId(),
                    designation,
                    firstName,
                    lastName,
                    fatherName,
                    dob,
                    qualification,
                    experience,
                    phone,
                    email,
                    address,
                    joiningDate,
                    password
            );
//            updated.setId(teacher.getId()); // ✅ Set the ID for updating
            db.updateTeacher(updated);
            teacherList.set(position, updated);
            notifyItemChanged(position);
            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation,tvEmail;
        TextView btnEdit;
        TextView btnDelete;

        CardView card;


        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            card = itemView.findViewById(R.id.cardTeacher);
        }
    }
}