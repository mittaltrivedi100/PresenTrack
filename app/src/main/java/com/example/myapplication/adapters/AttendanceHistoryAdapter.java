package com.example.myapplication.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.AttendanceRecord;

import java.util.List;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder> {

    private List<AttendanceRecord> records;

    public AttendanceHistoryAdapter(List<AttendanceRecord> records) {
        this.records = records;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStudentName, tvDate, tvStatus;

        public ViewHolder(View view) {
            super(view);
            tvStudentName = view.findViewById(R.id.tvStudentName);
            tvDate = view.findViewById(R.id.tvDate);
            tvStatus = view.findViewById(R.id.tvStatus);
        }
    }

    @Override
    public AttendanceHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);

        holder.tvStudentName.setText(record.getStudentName());
        holder.tvDate.setText("Date: " + record.getDate());
        holder.tvStatus.setText(record.isPresent() ? "Present ✅" : "Absent ❌");
        holder.tvStatus.setTextColor(record.isPresent() ? 0xFF4CAF50 : 0xFFF44336); // green or red
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}

