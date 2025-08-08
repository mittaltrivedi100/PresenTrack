package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.TeacherLectureModel;

import java.util.List;

public class TeacherLectureAdapter extends RecyclerView.Adapter<TeacherLectureAdapter.LectureViewHolder> {

    public interface OnLectureClickListener {
        void onLectureClick(TeacherLectureModel lecture);
    }

    private Context context;
    private List<TeacherLectureModel> lectureList;
    private OnLectureClickListener listener;

    public TeacherLectureAdapter(Context context, List<TeacherLectureModel> lectureList, OnLectureClickListener listener) {
        this.context = context;
        this.lectureList = lectureList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teacher_lecture, parent, false);
        return new LectureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        TeacherLectureModel lecture = lectureList.get(position);

        holder.txtLectureName.setText("Subject: " + lecture.getSubject());
        holder.txtClassSection.setText("Section: " + lecture.getSectionName());
        holder.txtTimetable.setText("Timetable: " + lecture.getTimetableName());
        holder.txtTime.setText("Time: " + lecture.getStartTime() + " - " + lecture.getEndTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLectureClick(lecture);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public static class LectureViewHolder extends RecyclerView.ViewHolder {
        TextView txtLectureName, txtClassSection, txtTimetable, txtTime;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLectureName = itemView.findViewById(R.id.txtLectureName);
            txtClassSection = itemView.findViewById(R.id.txtClassSection);
            txtTimetable = itemView.findViewById(R.id.txtTimetable);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
