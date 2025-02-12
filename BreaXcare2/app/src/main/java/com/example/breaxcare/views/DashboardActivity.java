package com.example.breaxcare.views;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.views.checkuphandler.CheckupActivity;
import com.example.breaxcare.views.checkuphandler.QuestionnaireActivity;
import com.example.breaxcare.views.events.ConsultationActivity;
import com.example.breaxcare.views.events.FAQActivity;
import com.example.breaxcare.views.events.YourTrendsActivity;
import com.example.breaxcare.views.taskmaneger.TreatmentPlanActivity;

public class DashboardActivity extends BaseActivity {
    private TextView risk_txt,check_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        risk_txt = findViewById(R.id.txt_risk);
        check_date = findViewById(R.id.txt_lcdate);

        DBhelper dbhelper = new DBhelper(this);
        UserSession userSession = new UserSession(this);
        String userEmail = userSession.getUserEmail();
        String userid = String.valueOf(dbhelper.getUserIdByEmail(userEmail));
        String [] checkup =dbhelper.getCheckupDetails(userid);

//        fadeOutCard(findViewById(R.id.card_tplan));
//        fadeOutCard(findViewById(R.id.card_mhistory));
//        fadeOutCard(findViewById(R.id.card_consult));
//        fadeOutCard(findViewById(R.id.card_ytrends));

        if (checkup != null && checkup.length<4){
            risk_txt.setText(checkup[0]);
            check_date.setText(checkup[1]);
            Log.d("risk",checkup[0]);
            if(checkup[0].equals("Low Risk")){
                risk_txt.setTextColor(Color.GREEN);
            } else if(checkup[0].equals("High Risk")){
                risk_txt.setTextColor(Color.RED);
            }else{

            }
        }else{

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.buttonAction).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CheckupActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_tplan).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TreatmentPlanActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_faq).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FAQActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_mhistory).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MedicalHistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_ytrends).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, YourTrendsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_consult).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ConsultationActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_qquest).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, QuestionnaireActivity.class);
            startActivity(intent);
        });

    }

    public void fadeOutCard(View cardView) {
        // Create an overlay view
        View overlay = new View(cardView.getContext());
        overlay.setBackgroundColor(0x80000000); // 50% transparent black
        overlay.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Make overlay absorb touches
        overlay.setClickable(true);
        overlay.setFocusable(true);

        // Add overlay to the card
        if (cardView instanceof FrameLayout) {
            ((FrameLayout) cardView).addView(overlay);
        } else if (cardView instanceof LinearLayout) {
            ((LinearLayout) cardView).addView(overlay);
        }

        // Fade-in animation
        overlay.setAlpha(0f);
        overlay.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(overlay, "alpha", 0f, 1f);
        fadeIn.setDuration(500); // 500ms animation
        fadeIn.start();

        // Make the card itself unclickable
        cardView.setClickable(false);
        cardView.setFocusable(false);
    }


}
