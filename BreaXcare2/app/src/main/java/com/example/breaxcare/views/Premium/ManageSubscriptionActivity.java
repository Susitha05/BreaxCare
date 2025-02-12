package com.example.breaxcare.views.Premium;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

public class ManageSubscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_subscription);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        TextView currentPlanText = findViewById(R.id.currentplantext);
        TextView availablePlanText = findViewById(R.id.availableplantext);
        CheckBox availablePlanCheckBox = findViewById(R.id.checkbox_availablePlan);
        CardView availableCard = findViewById(R.id.availableCard);
        Button changePlanButton = findViewById(R.id.btn_changePlan);

        // Fetch user email from UserSession
        UserSession userSession = new UserSession(this);
        String userEmail = userSession.getUserEmail();

        DBhelper dbHelper = new DBhelper(this);

        int userId = -1; // Default value for userId
        if (userEmail != null) {
            // Get userId using getUserIdByEmail
            userId = dbHelper.getUserIdByEmail(userEmail);

            if (userId != -1) {
                // Fetch Subscription_Type using userId
                String subscriptionType = dbHelper.getSubscriptionTypeByUserId(userId);

                if (subscriptionType != null) {
                    if (subscriptionType.equals("2.99$ / Month")) {
                        currentPlanText.setText("2.99$ / Month");
                        availablePlanText.setText("29.99$ / Year");
                    } else if (subscriptionType.equals("29.99$ / Year")) {
                        currentPlanText.setText("29.99$ / Year");
                        availablePlanText.setText("2.99$ / Month");
                    }

                } else {
                    currentPlanText.setText("No Subscription Found");
                }
            } else {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Set listener for checkbox
        availablePlanCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                availableCard.setBackgroundTintList(getResources().getColorStateList(R.color.Cardgreen, null));
            } else {
                availableCard.setBackgroundTintList(getResources().getColorStateList(R.color.default_card_color, null));
            }
        });

        // Handle change plan button click
        final int finalUserId = userId; // Capture the already retrieved userId
        changePlanButton.setOnClickListener(v -> {
            if (availablePlanCheckBox.isChecked()) {
                String newPlan = availablePlanText.getText().toString();
                if (!newPlan.isEmpty() && finalUserId != -1) {
                    boolean isUpdated = dbHelper.updateSubscriptionType(finalUserId, newPlan);

                    if (isUpdated) {
                        Toast.makeText(this, "Plan updated successfully", Toast.LENGTH_SHORT).show();
                        currentPlanText.setText(newPlan); // Reflect the change in UI
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update plan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select the available plan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
