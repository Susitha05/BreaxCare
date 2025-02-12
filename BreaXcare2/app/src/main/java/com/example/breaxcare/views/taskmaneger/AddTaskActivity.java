package com.example.breaxcare.views.taskmaneger;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTitle, edtDate, edtTime, edtDescription;
    private Spinner spinnerTaskType;
    private Button btnSave, btnCancel;
    private DBhelper dbHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        edtTitle = findViewById(R.id.username);
        edtDate = findViewById(R.id.date);
        edtTime = findViewById(R.id.time);
        edtDescription = findViewById(R.id.description);
        spinnerTaskType = findViewById(R.id.TaskTypeSpinner);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);


        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        if (selectedDate != null) {
            edtDate.setText(selectedDate);
        }


        setupSpinner();


        edtTime.setOnClickListener(v -> showTimePickerDialog());


        btnSave.setOnClickListener(v -> saveTask());


        btnCancel.setOnClickListener(v -> finish());
    }


    private void setupSpinner() {

        String[] taskTypes = {"Consultation", "Follow-up", "Medication", "Check-up", "Emergency"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, taskTypes);


        spinnerTaskType.setAdapter(adapter);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    edtTime.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }


    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String time = edtTime.getText().toString().trim();
        String taskType = spinnerTaskType.getSelectedItem().toString();  // Get selected task type from spinner
        String description = edtDescription.getText().toString().trim();


        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(description)) {

            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }


        String email = userSession.getUserEmail();  // Assuming UserSession.getInstance() gives you access to session info

        if (TextUtils.isEmpty(email)) {

            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        long userId = dbHelper.getUserIdByEmail(email);

        if (userId == -1) {

            Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            return;
        }


        long result = dbHelper.addTreatmentPlan(title, date, time, taskType, description, userId);

        if (result != -1) {

            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }
}
