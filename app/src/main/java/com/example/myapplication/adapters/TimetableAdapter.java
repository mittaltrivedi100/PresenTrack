package com.example.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.LectureActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.TimetableModel;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {

    private Context context;
    private List<TimetableModel> timetableList;
    private DatabaseHelper db;

    public TimetableAdapter(Context context, List<TimetableModel> timetableList, DatabaseHelper db) {
        this.context = context;
        this.timetableList = timetableList;
        this.db = db;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timetable_item, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        TimetableModel timetable = timetableList.get(position);

        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to open LectureActivity
            Intent intent = new Intent(context, LectureActivity.class);

            // Get the timetableName from the timetableList based on position
            String timetableName = timetableList.get(position).getName();

            // Put the timetableName as an extra in the Intent
            intent.putExtra("timetableName", timetableName);

            // Start the LectureActivity
            context.startActivity(intent);
        });



        holder.tvTimetableName.setText(timetable.getName());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(timetable, position));

        holder.btnDelete.setOnClickListener(v -> {
            db.deleteTimetable(timetable.getId());
            timetableList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, timetableList.size());
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(TimetableModel timetable, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_timetable, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText edtTimetableName = dialogView.findViewById(R.id.edtTimetableName);

        edtTimetableName.setText(timetable.getName());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = edtTimetableName.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, "Please enter timetable name", Toast.LENGTH_SHORT).show();
                return;
            }

            timetable.setName(name);
            db.updateTimetable(timetable);
            timetableList.set(position, timetable);
            notifyItemChanged(position);
            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return timetableList.size();
    }

    public static class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimetableName;
        TextView btnEdit;
        TextView btnDelete;
        CardView card;

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimetableName = itemView.findViewById(R.id.tvTimetableName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            card = itemView.findViewById(R.id.cardTimetable);
        }
    }
}
