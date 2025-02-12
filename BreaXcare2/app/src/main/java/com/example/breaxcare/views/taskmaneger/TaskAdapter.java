package com.example.breaxcare.views.taskmaneger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.treatments.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private Task selectedTask;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        Task task = tasks.get(position);


        holder.titleTextView.setText(task.getTitle());
        holder.dateTextView.setText(task.getDate());
        holder.timeTextView.setText(task.getTime());
        holder.descriptionTextView.setText(task.getDescription()); // Set description text


        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                selectedTask = task;
            } else {
                selectedTask = null;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public Task getSelectedTask() {
        return selectedTask;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, dateTextView, timeTextView, descriptionTextView; // Added descriptionTextView
        CheckBox checkBox;

        public TaskViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.task_title);
            dateTextView = itemView.findViewById(R.id.task_date);
            timeTextView = itemView.findViewById(R.id.task_time);
            descriptionTextView = itemView.findViewById(R.id.task_description); // Initialize descriptionTextView
            checkBox = itemView.findViewById(R.id.task_checkbox);
        }
    }
}
