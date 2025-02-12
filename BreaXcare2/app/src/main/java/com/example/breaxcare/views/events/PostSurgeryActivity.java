package com.example.breaxcare.views.events;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;

import java.util.ArrayList;

public class PostSurgeryActivity extends AppCompatActivity {

    private TextView extxt, timer;
    private ImageView eximage;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 30000; // 30 seconds
    private final String[] exname = {"Wall Walking", "Elbow Winging", "Deep Breathing", "Shoulder Rolls", "Neck Stretches"};
    private final ArrayList<Integer> pics = new ArrayList<>();
    private int count = 0; // Track current exercise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_surgery);


        extxt = findViewById(R.id.extext);
        timer = findViewById(R.id.timer);
        eximage = findViewById(R.id.img);


        pics.add(R.drawable.hqdefault);
        pics.add(R.drawable.image11);
        pics.add(R.drawable.image10);
        pics.add(R.drawable.image12);
        pics.add(R.drawable.neck_stretch_exercise_illustration);


        updateExercise();

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.star).setOnClickListener(v -> startTimer());


        findViewById(R.id.back).setOnClickListener(v -> {
            if (count > 0) {
                count--;
                updateExercise();
                resetTimer();
            } else {
                Toast.makeText(this, "This is the first exercise.", Toast.LENGTH_SHORT).show();
            }
        });

        // Next Button
        findViewById(R.id.next).setOnClickListener(v -> {
            if (count < exname.length - 1) {
                count++;
                updateExercise();
                resetTimer();
            } else {
                Toast.makeText(this, "This is the last exercise.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        resetTimer();
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                timer.setText("Time's up!");
                Toast.makeText(PostSurgeryActivity.this, "Timer finished for this exercise.", Toast.LENGTH_SHORT).show();
            }
        };
        countDownTimer.start();
    }


    private void updateExercise() {
        extxt.setText(exname[count]);
        eximage.setImageResource(pics.get(count));
    }


    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timer.setText("Time: 30s"); // Reset timer text
    }
}
