package com.example.breaxcare.views.checkuphandler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;
import com.example.breaxcare.views.DashboardActivity;

public class AnswerActivity extends AppCompatActivity {
    private TextView answerview, description;
    private ImageView stateimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        description = findViewById(R.id.description);
        stateimg = findViewById(R.id.state);


        Intent intent = getIntent();
        String answer = intent.getStringExtra("label");

        answerview = findViewById(R.id.answer);
        if (answer != null) {
            answerview.setText("You Your State is " +answer);
        } else {
            answerview.setText("No data received.");
        }
        if(answer.equals("Risk")){
            description.setText("You are in a risk state. Please consider an image check-up for further confirmation.");
            stateimg.setImageResource(R.drawable.ic_unhealthy);
        } else if(answer.equals("Check")) {
            description.setText("Your state is uncertain. It is recommended to proceed with an image check-up for clarity.");
            stateimg.setImageResource(R.drawable.checkups);
        }
        else{
            description.setText("You are in a normal state. For further confirmation, consider an image check-up.");
            stateimg.setImageResource(R.drawable.ic_healthy);
            description.setTextSize(20);
        }
        findViewById(R.id.done).setOnClickListener(v -> {
            Intent intent1 = new Intent(AnswerActivity.this, DashboardActivity.class);
            startActivity(intent1);
        });
    }
}