package com.example.breaxcare.views.taskmaneger;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.treatments.TreatmentPlan;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.model.treatments.TreatmentPlanAdapter;
import com.example.breaxcare.views.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsActivity extends BaseActivity {

    private DBhelper dbHelper;
    private UserSession userSession;
    private RecyclerView recyclerView;
    private TreatmentPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_events);

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        recyclerView = findViewById(R.id.eventrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch user email and get user ID
        String email = userSession.getUserEmail();
        int userId = dbHelper.getUserIdByEmail(email);

        if (userId != -1) {

            String currentDate = getCurrentDate();


            List<TreatmentPlan> tasks = dbHelper.getTasksForUserAndDate(userId, currentDate);
            adapter = new TreatmentPlanAdapter(this, tasks);
            recyclerView.setAdapter(adapter); // Always set the adapter

            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(this, "No tasks for today.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
        }

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()); // Match database format
        return sdf.format(new Date());
    }
}
