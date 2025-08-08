package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AttendanceListActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.AttendanceLectureModel;

import java.util.List;

public class AttendanceLectureAdapter extends RecyclerView.Adapter<AttendanceLectureAdapter.LectureViewHolder> {

    List<AttendanceLectureModel> lectureList;
    Context context;

    public AttendanceLectureAdapter(List<AttendanceLectureModel> list, Context ctx) {
        this.lectureList = list;
        this.context = ctx;
    }

    @Override
    public LectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_lecture_item, parent, false);
        return new LectureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LectureViewHolder holder, int position) {
        AttendanceLectureModel lecture = lectureList.get(position);

        holder.subject.setText("Subject: " + lecture.subject);
        holder.className.setText("Class: " + lecture.className);
        holder.time.setText("Time: " + lecture.startTimeHour + ":" + String.format("%02d", lecture.startTimeMinute)
                + " - " + lecture.endTimeHour + ":" + String.format("%02d", lecture.endTimeMinute));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendanceListActivity.class);
            intent.putExtra("lectureId", lecture.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public static class LectureViewHolder extends RecyclerView.ViewHolder {
        TextView subject, className, time;

        public LectureViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.tvSubject);
            className = itemView.findViewById(R.id.tvClass);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
