package com.example.myapplication.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.StudentAttendanceModel;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(String studentId);
    }

    private ArrayList<StudentAttendanceModel> studentList;
    private HashMap<String, Integer> attendanceMap;
    private OnStudentClickListener listener;

    public StudentAttendanceAdapter(ArrayList<StudentAttendanceModel> studentList,
                                    HashMap<String, Integer> attendanceMap,
                                    OnStudentClickListener listener) {
        this.studentList = studentList;
        this.attendanceMap = attendanceMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_attendance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAttendanceAdapter.ViewHolder holder, int position) {
        StudentAttendanceModel student = studentList.get(position);
        String fullName = student.getFirstName() + " " + student.getLastName();
        String studentId = student.getId();

        holder.tvInfo.setText("ID: " + studentId + " | " + fullName);

        int status = attendanceMap.getOrDefault(studentId, 1);
        if (status == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#CCFFCC")); // Present
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFCCCC")); // Absent
        }

        holder.itemView.setOnClickListener(v -> listener.onStudentClick(studentId));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvStudentInfo);
        }
    }
}
