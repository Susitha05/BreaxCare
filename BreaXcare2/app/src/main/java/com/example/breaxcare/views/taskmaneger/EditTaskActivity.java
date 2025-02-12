package com.example.breaxcare.views.taskmaneger;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    private Spinner titleSpinner, taskTypeSpinner;
    private EditText dateEditText, timeEditText, descriptionEditText;
    private Button btnCancel, btnSave;
    private ImageView calendarIcon, timeIcon;
    private DBhelper dbHelper;
    private UserSession userSession;
    private int userId; // Store the user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);


        String userEmail = userSession.getUserEmail();
        userId = dbHelper.getUserIdByEmail(userEmail);

        // Initialize views
        titleSpinner = findViewById(R.id.TitleSpinner);
        taskTypeSpinner = findViewById(R.id.TaskTypeSpinner);
        dateEditText = findViewById(R.id.date);
        timeEditText = findViewById(R.id.time);
        descriptionEditText = findViewById(R.id.description);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        calendarIcon = findViewById(R.id.dateIcon);
        timeIcon = findViewById(R.id.timeIcon);


        setupTitleSpinner();
        setupTaskTypeSpinner();


        calendarIcon.setOnClickListener(v -> showDatePicker());
        dateEditText.setOnClickListener(v -> showDatePicker());

        timeIcon.setOnClickListener(v -> showTimePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());

        btnCancel.setOnClickListener(v -> finish()); // Close activity
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void setupTitleSpinner() {
        List<String> titles = dbHelper.getTitlesByUserId(userId);
        if (titles.isEmpty()) {
            Toast.makeText(this, "No tasks found for this user!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleAdapter);

        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = parent.getItemAtPosition(position).toString();
                populateFields(selectedTitle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void populateFields(String title) {

        HashMap<String, String> data = dbHelper.getDataByTitleAndUserId(title, userId);

        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "No data found for selected task", Toast.LENGTH_SHORT).show();
            return;
        }


        dateEditText.setText(data.getOrDefault(DBhelper.COLUMN_DATE, ""));
        timeEditText.setText(data.getOrDefault(DBhelper.COLUMN_TIME, ""));
        descriptionEditText.setText(data.getOrDefault(DBhelper.COLUMN_DESCRIPTION, ""));


        String taskType = data.getOrDefault(DBhelper.COLUMN_TASK_TYPE, "");
        if (taskTypeSpinner.getAdapter() != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) taskTypeSpinner.getAdapter();
            int taskTypePosition = adapter.getPosition(taskType);

            if (taskTypePosition != -1) {
                taskTypeSpinner.setSelection(taskTypePosition);
            } else {
                Toast.makeText(this, "Task type not found in the list", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupTaskTypeSpinner() {

        String[] taskTypes = {"Consultation", "Follow-up", "Medication", "Check-up", "Emergency"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, taskTypes);


        taskTypeSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> dateEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void saveTask() {
        String title = titleSpinner.getSelectedItem().toString();
        String taskType = taskTypeSpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (title.isEmpty() || taskType.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        long result = dbHelper.updateTreatmentPlanByUserIdAndTitle(title, date, time, taskType, description, userId);

        if (result > 0) {
            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
        }
    }
}
