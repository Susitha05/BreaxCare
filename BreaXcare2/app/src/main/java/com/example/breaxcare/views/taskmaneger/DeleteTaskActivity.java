package com.example.breaxcare.views.taskmaneger;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.treatments.Task;
import com.example.breaxcare.model.UserSession;

import java.util.List;

public class DeleteTaskActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnDelete;
    private TaskAdapter taskAdapter;
    private DBhelper dbHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_task);


        recyclerView = findViewById(R.id.recyclerView);
        btnDelete = findViewById(R.id.btn_delete);
        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        loadTasks();


        btnDelete.setOnClickListener(v -> deleteSelectedTask());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadTasks() {

        String email = userSession.getUserEmail();

        if (email != null) {

            long userId = dbHelper.getUserIdByEmail(email);


            List<Task> tasks = dbHelper.getTasksByUserId(userId);

            if (tasks != null && !tasks.isEmpty()) {

                taskAdapter = new TaskAdapter(tasks);
                recyclerView.setAdapter(taskAdapter);
            } else {

                Toast.makeText(this, "No tasks found for this user", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedTask() {

        Task selectedTask = taskAdapter.getSelectedTask();

        if (selectedTask != null) {

            boolean isDeleted = dbHelper.deleteTask(selectedTask.getId());

            if (isDeleted) {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                // Refresh the task list
                loadTasks();
            } else {
                Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No task selected", Toast.LENGTH_SHORT).show();
        }
    }
}
