package com.example.breaxcare.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.util.Pair;

import com.example.breaxcare.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize the views
        ImageView splashImage = findViewById(R.id.splashimg);
        TextView splashText = findViewById(R.id.splashtxt);

        // Apply fade-in animation to both ImageView and TextView
        applyFadeInAnimation(splashImage, splashText);
    }

    private void applyFadeInAnimation(ImageView imageView, TextView textView) {
        // Create fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);  // from invisible to visible
        fadeIn.setDuration(1000);  // 1 second for fade-in
        fadeIn.setFillAfter(true);  // Maintain the state after animation

        // Apply the fade-in animation to both views
        imageView.startAnimation(fadeIn);
        textView.startAnimation(fadeIn);

        // Once fade-in is done, proceed to fade-out
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Optionally, do something when animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Once fade-in ends, start fade-out after a short delay
                applyFadeOutAnimation(imageView, textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Optionally, do something when animation repeats
            }
        });
    }

    private void applyFadeOutAnimation(ImageView imageView, TextView textView) {
        // Create fade-out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);  // from visible to invisible
        fadeOut.setDuration(800);  // 0.8 seconds for fade-out (slightly faster for a smoother transition)
        fadeOut.setFillAfter(true);  // Maintain the state after animation

        // Set animation listener for fade-out
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Optionally, do something when animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // After fade-out completes, navigate to StartActivity with bottom-to-top transition
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);

                // Create custom bottom-to-top animation
                ActivityOptions options = ActivityOptions.makeCustomAnimation(SplashActivity.this,
                        R.anim.slide_up_in,  // Custom bottom-to-top animation
                        R.anim.slide_down_out); // Custom bottom-to-top exit animation

                // Start the next activity with the transition
                startActivity(intent, options.toBundle());
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Optionally, do something when animation repeats
            }
        });

        // Apply the fade-out animation to both views
        imageView.startAnimation(fadeOut);
        textView.startAnimation(fadeOut);
    }
}
