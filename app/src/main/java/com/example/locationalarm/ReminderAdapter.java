package com.example.locationalarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<Reminder> reminders;
    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.tvTitle.setText(reminder.title);
        holder.tvDateTime.setText(reminder.date + " | " + reminder.time);
        holder.btnDelete.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("Reminders")
                    .child(uid)
                    .child(reminder.id)
                    .removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(v.getContext(), "התזכורת נמחקה", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
    @Override
    public int getItemCount() {
        return reminders.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime;
        ImageView btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDateTime = itemView.findViewById(R.id.tvItemDateTime);
            btnDelete = itemView.findViewById(R.id.btnDeleteReminder);
        }
    }
}