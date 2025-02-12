package com.example.breaxcare.views.usershandler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.views.StartActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private UserSession userSession;
    private SharedPreferences sharedPreferences;
    private SwitchMaterial toggleNotifications, toggleBiometrics, toggleDailySuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Initialize UserSession
        userSession = new UserSession(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SettingsPreferences", MODE_PRIVATE);

        // Back button functionality
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Sign-out button functionality
        findViewById(R.id.btn_signout).setOnClickListener(v -> {
            userSession.clearSession();
            Intent intent = new Intent(SettingsActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize toggles
        toggleNotifications = findViewById(R.id.toggle_notifications);
        toggleBiometrics = findViewById(R.id.toggle_biometrics);
        toggleDailySuggestions = findViewById(R.id.toggle_daily_suggestions);

        // Load and set saved states for toggles
        toggleNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", false));
        toggleBiometrics.setChecked(sharedPreferences.getBoolean("biometrics_enabled", false));
        toggleDailySuggestions.setChecked(sharedPreferences.getBoolean("daily_suggestions_enabled", false));

        // Set toggle listeners
        toggleNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleNotificationToggle(isChecked);
        });

        toggleBiometrics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleBiometricsToggle(isChecked);
        });

        toggleDailySuggestions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleDailySuggestionsToggle(isChecked);
        });

        // Button functionalities
        findViewById(R.id.btn_terms).setOnClickListener(v -> openTermsAndConditions());
        findViewById(R.id.btn_privacy).setOnClickListener(v -> openPrivacyPolicy());
        findViewById(R.id.btn_help).setOnClickListener(v -> openHelpFeedback());
        findViewById(R.id.btn_about).setOnClickListener(v -> openAboutUs());
        findViewById(R.id.btn_delete_account).setOnClickListener(v -> confirmAccountDeletion());
    }

    private void handleNotificationToggle(boolean isChecked) {
        if (isChecked) {
            openNotificationSettings();
        }
        savePreference("notifications_enabled", isChecked);
    }

    private void handleBiometricsToggle(boolean isChecked) {
        showToast(isChecked ? "Biometrics Authentication enabled" : "Biometrics Authentication disabled");
        savePreference("biometrics_enabled", isChecked);
    }

    private void handleDailySuggestionsToggle(boolean isChecked) {
        showToast(isChecked ? "Daily suggestions enabled" : "Daily suggestions disabled");
        savePreference("daily_suggestions_enabled", isChecked);
    }

    // Open notification settings
    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
        showToast("Opened notification settings");
    }

    // Helper method to save preferences
    private void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper methods for additional buttons
    private void openTermsAndConditions() {
        showToast("Opening Terms and Conditions");
        // Implement logic to navigate to Terms and Conditions screen
    }

    private void openPrivacyPolicy() {
        showToast("Opening Privacy Policy");
        // Implement logic to navigate to Privacy Policy screen
    }

    private void openHelpFeedback() {
        showToast("Opening Help & Feedback");
        // Implement logic to navigate to Help & Feedback screen
    }

    private void openAboutUs() {
        showToast("Opening About Us");
        // Implement logic to navigate to About Us screen
    }

    private void confirmAccountDeletion() {
        showToast("Confirming Account Deletion");
        // Implement logic to handle account deletion with confirmation dialog
    }
}
