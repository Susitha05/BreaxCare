package com.example.breaxcare.model.treatments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;

import java.util.List;

public class TreatmentPlanAdapter extends RecyclerView.Adapter<TreatmentPlanAdapter.ViewHolder> {

    private Context context;
    private List<TreatmentPlan> tasks;

    public TreatmentPlanAdapter(Context context, List<TreatmentPlan> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TreatmentPlan task = tasks.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskTime.setText(task.getTime());
        holder.taskDescription.setText(task.getDescription());

        // Change background color based on task type
        String taskType = task.getTaskType();
        switch (taskType) {
            case "Consultation":
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4081")));
                break;
            case "Follow-up":
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case "Medication":
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3388FF")));
                break;
            case "Check-up":
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D5A902")));
                break;
            case "Emergency":
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            default:
                holder.cardEvent.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskTitle, taskTime, taskDescription;
        View cardEvent;

        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskTime = itemView.findViewById(R.id.task_time);
            taskDescription = itemView.findViewById(R.id.task_description);
            cardEvent = itemView.findViewById(R.id.card_event);  // Assuming 'card_event' is the ID for the card view
        }
    }
}
