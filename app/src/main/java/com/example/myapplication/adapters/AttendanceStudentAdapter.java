package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.AttendanceStudentModel;

import java.util.List;

public class AttendanceStudentAdapter extends RecyclerView.Adapter<AttendanceStudentAdapter.ViewHolder> {

    private List<AttendanceStudentModel> studentList;
    private Context context;

    public AttendanceStudentAdapter(List<AttendanceStudentModel> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId;
        CheckBox cbPresent;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvStudentName);
            tvId = view.findViewById(R.id.tvStudentId);
            cbPresent = view.findViewById(R.id.cbPresent);
        }
    }

    @NonNull
    @Override
    public AttendanceStudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceStudentAdapter.ViewHolder holder, int position) {
        AttendanceStudentModel student = studentList.get(position);
        holder.tvName.setText(student.getFullName());
        holder.tvId.setText("ID: " + student.getId());
        holder.cbPresent.setChecked(student.isPresent());
        holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> student.setPresent(isChecked));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
