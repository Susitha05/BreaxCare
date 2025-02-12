package com.example.breaxcare.views;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.breaxcare.R;
import com.example.breaxcare.views.usershandler.LoginFragment;
import com.example.breaxcare.views.usershandler.SignupFragment;

public class StartActivity extends AppCompatActivity {

    private Button btnToggledLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize buttons
        btnToggledLogin = findViewById(R.id.btn_Toggledlogin);
        btnSignup = findViewById(R.id.btn_signup);

        // Set click listeners for toggler buttons to switch fragments
        btnToggledLogin.setOnClickListener(v -> {
            // Switch to LoginFragment
            loadFragment(new LoginFragment());
            // Update button styles
            btnToggledLogin.setBackgroundTintList(getColorStateList(R.color.primaryColor));
            btnSignup.setBackgroundTintList(getColorStateList(R.color.white));
            btnSignup.setTextColor(getColor(R.color.black));
        });

        btnSignup.setOnClickListener(v -> {
            // Switch to SignupFragment
            loadFragment(new SignupFragment());
            // Update button styles
            btnSignup.setBackgroundTintList(getColorStateList(R.color.primaryColor));
            btnToggledLogin.setBackgroundTintList(getColorStateList(R.color.white));
            btnToggledLogin.setTextColor(getColor(R.color.black));
        });

        // Set initial fragment (LoginFragment)
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
            btnToggledLogin.setBackgroundTintList(getColorStateList(R.color.primaryColor));
        }
    }

    // Method to load a fragment dynamically
    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        // Get the FragmentTransaction object
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the content of the container with the new fragment
        transaction.replace(R.id.fragment_container, fragment);

        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    // Override onBackPressed() to finish the activity and exit the app
    @Override
    public void onBackPressed() {
        // Finish the StartActivity and exit the app
        super.onBackPressed();
        finish();  // Finish the activity
    }
}
