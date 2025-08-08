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
import com.example.myapplication.models.ClassModel;
import com.example.myapplication.DatabaseHelper;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ClassModel> classList;
    private DatabaseHelper db;

    public ClassAdapter(Context context, ArrayList<ClassModel> classList, DatabaseHelper db) {
        this.context = context;
        this.classList = classList;
        this.db = db;
    }

    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);
        holder.tvClassName.setText(classModel.getName());
        holder.tvSection.setText("Section: " + classModel.getSection());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(classModel, position));
        holder.btnDelete.setOnClickListener(v -> {
            db.deleteClass(classModel.getId());
            classList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, classList.size());
            Toast.makeText(context, "Class deleted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(ClassModel model, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_class, null);
        builder.setView(view);

        EditText edtClassName = view.findViewById(R.id.edtClassName);
        EditText edtSection = view.findViewById(R.id.edtSection);
        Button btnSave = view.findViewById(R.id.btnSave);

        edtClassName.setText(model.getName());
        edtSection.setText(model.getSection());

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtClassName.getText().toString();
            String section = edtSection.getText().toString();

            if (name.isEmpty() || section.isEmpty()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            db.updateClass(model.getId(), name, section);
            model.setName(name);
            model.setSection(section);
            notifyItemChanged(position);

            Toast.makeText(context, "Class updated successfully", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvSection,btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvSection = itemView.findViewById(R.id.tvSectionName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
