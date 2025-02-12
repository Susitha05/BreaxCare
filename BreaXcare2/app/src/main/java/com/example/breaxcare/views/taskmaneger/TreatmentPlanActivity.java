package com.example.breaxcare.views.taskmaneger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;

public class TreatmentPlanActivity extends AppCompatActivity {

    private String selectedDate; // Variable to store the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_plan);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CalendarView calendarView = findViewById(R.id.calendarView);
        Button btnAddTask = findViewById(R.id.btn_addTask);
        Button btnEditTask = findViewById(R.id.btn_EditTask);
        Button btnDeleteTask = findViewById(R.id.btn_deleteTask);
        Button btnNotes = findViewById(R.id.btn_notes);
        Button btnBack = findViewById(R.id.btn_back);


        selectedDate = getCurrentDateFromCalendar(calendarView);


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });


        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(TreatmentPlanActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });


        btnEditTask.setOnClickListener(v -> {
            Intent intent = new Intent(TreatmentPlanActivity.this, EditTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });


        btnDeleteTask.setOnClickListener(v -> {
            Intent intent = new Intent(TreatmentPlanActivity.this, DeleteTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });


        btnNotes.setOnClickListener(v -> {
            Intent intent = new Intent(TreatmentPlanActivity.this, NotesActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });


        btnBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Helper method to get the current date from the CalendarView.
     */
    private String getCurrentDateFromCalendar(CalendarView calendarView) {
        long currentDate = calendarView.getDate();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentDate);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
}
