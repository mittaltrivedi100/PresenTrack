package com.example.myapplication.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.AttendanceDisplayModel;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final List<AttendanceDisplayModel> attendanceList;

    public AttendanceAdapter(List<AttendanceDisplayModel> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_attendance_item, parent, false);
        return new AttendanceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceDisplayModel item = attendanceList.get(position);

        holder.tvStudentId.setText(item.getStudentId());
        holder.tvStudentName.setText(item.getStudentName());

        if (item.getStatus() == null) {
            holder.tvStatus.setText("No Record");
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
        } else if (item.getStatus() == 1) {
            holder.tvStatus.setText("Present");
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setText("Absent");
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentId, tvStudentName, tvStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStatus = itemView.findViewById(R.id.tvAttendanceStatus);
        }
    }
}


