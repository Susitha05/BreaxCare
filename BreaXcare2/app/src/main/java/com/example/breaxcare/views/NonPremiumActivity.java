package com.example.breaxcare.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;

public class NonPremiumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_non_premium);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to upgrade to premium
        Button btnUpgradePremium = findViewById(R.id.btn_upgradePremium);
        btnUpgradePremium.setOnClickListener(v -> {
            // Navigate to PaymentActivity
            Intent intent = new Intent(NonPremiumActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        // Button to go back and close current activity
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            // Close the current activity
            finish();
        });
    }
}
