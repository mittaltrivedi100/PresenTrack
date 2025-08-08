package com.example.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.LectureModel;
import com.example.myapplication.DatabaseHelper;

import java.util.List;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> {

    private Context context;
    private List<LectureModel> lectureList;
    private DatabaseHelper db;

    public LectureAdapter(Context context, List<LectureModel> lectureList, DatabaseHelper db) {
        this.context = context;
        this.lectureList = lectureList;
        this.db = db;
    }

    @NonNull
    @Override
    public LectureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lecture_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureAdapter.ViewHolder holder, int position) {
        LectureModel lecture = lectureList.get(position);

        holder.tvSubject.setText(lecture.getSubject());
        holder.tvTeacher.setText(lecture.getTeacherName());
        holder.tvDay.setText(lecture.getDay());
        holder.tvTime.setText(lecture.getStartTime() + " - " + lecture.getEndTime());
        holder.tvTimetableName.setText(lecture.getTimetableName());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(lecture, position));
        holder.btnDelete.setOnClickListener(v -> {
            db.deleteLecture(lecture.getId());
            lectureList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lectureList.size());
            Toast.makeText(context, "Lecture deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(LectureModel model, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_lecture, null);
        builder.setView(view);

        Spinner spinnerSection = view.findViewById(R.id.spinnerSection); // updated ID
        Spinner spinnerTeacher = view.findViewById(R.id.spinnerTeacherName);
        Spinner spinnerDay = view.findViewById(R.id.spinnerDay);
        EditText edtSubject = view.findViewById(R.id.edtSubject);
        TimePicker startTimePicker = view.findViewById(R.id.timePickerStart);
        TimePicker endTimePicker = view.findViewById(R.id.timePickerEnd);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Populate spinners
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, db.getAllSections());
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(sectionAdapter);

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, db.getAllTeachersName());
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","Sunday"});
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Set current values
        edtSubject.setText(model.getSubject());
        startTimePicker.setHour(model.getStartTimeHour());
        startTimePicker.setMinute(model.getStartTimeMinute());
        endTimePicker.setHour(model.getEndTimeHour());
        endTimePicker.setMinute(model.getEndTimeMinute());

        spinnerSection.setSelection(sectionAdapter.getPosition(model.getSection()));
        spinnerTeacher.setSelection(teacherAdapter.getPosition(model.getTeacherName()));
        spinnerDay.setSelection(dayAdapter.getPosition(model.getDay()));

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            if (spinnerSection.getSelectedItem() == null || spinnerTeacher.getSelectedItem() == null || spinnerDay.getSelectedItem() == null) {
                Toast.makeText(context, "Please select all dropdowns", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedSection = spinnerSection.getSelectedItem().toString();
            String selectedTeacher = spinnerTeacher.getSelectedItem().toString();
            String selectedDay = spinnerDay.getSelectedItem().toString();
            String subject = edtSubject.getText().toString().trim();
            int startHour = startTimePicker.getHour();
            int startMinute = startTimePicker.getMinute();
            int endHour = endTimePicker.getHour();
            int endMinute = endTimePicker.getMinute();

            if (subject.isEmpty()) {
                Toast.makeText(context, "Subject is required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.updateLecture(
                    model.getId(),
                    selectedSection,
                    selectedTeacher,
                    selectedDay,
                    model.getTimetableName(),
                    subject,
                    startHour,
                    startMinute,
                    endHour,
                    endMinute
            );

            if (success) {
                model.setSection(selectedSection);
                model.setTeacherName(selectedTeacher);
                model.setDay(selectedDay);
                model.setSubject(subject);
                model.setStartTimeHour(startHour);
                model.setStartTimeMinute(startMinute);
                model.setEndTimeHour(endHour);
                model.setEndTimeMinute(endMinute);

                notifyItemChanged(position);
                Toast.makeText(context, "Lecture updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Failed to update lecture", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvTeacher, tvDay, tvTime, tvTimetableName;
        TextView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTimetableName = itemView.findViewById(R.id.txtTimetableName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
