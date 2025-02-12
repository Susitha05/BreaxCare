package com.example.breaxcare.views.events;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.views.BaseActivity;
import com.example.breaxcare.views.checkuphandler.SymptomTrackingActivity;

public class YourTrendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_trends);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_stracking).setOnClickListener(v -> {

            startActivity(new Intent(this, SymptomTrackingActivity.class));
        });

        findViewById(R.id.btn_dailyTips).setOnClickListener(v -> {

            startActivity(new Intent(this, DailyTipsActivity.class));
        });

        findViewById(R.id.btn_exercisePlans).setOnClickListener(v -> {

            startActivity(new Intent(this, ExerciseActivity.class));
        });

        findViewById(R.id.btn_dReccomand).setOnClickListener(v -> {

            startActivity(new Intent(this, DietaryRecommendationsActivity.class));
        });

        findViewById(R.id.btn_btrends).setOnClickListener(v -> {

            startActivity(new Intent(this, BreastHealthTrendsActivity.class));
        });
    }
}
