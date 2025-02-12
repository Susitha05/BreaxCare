package com.example.breaxcare.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.views.Premium.PremiumMemberActivity;
import com.example.breaxcare.views.usershandler.SettingsActivity;

public class TopMenuFragment extends Fragment {

    private TextView txtGreeting, txtUsername;
    private Button btnNonPremium, btnPremium;
    private DBhelper dbHelper;
    private UserSession userSession;

    public TopMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize DBhelper and UserSession
        dbHelper = new DBhelper(getContext());
        userSession = new UserSession(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_menu, container, false);

        // Initialize TextViews and Buttons
        txtGreeting = view.findViewById(R.id.txt_greeting);
        txtUsername = view.findViewById(R.id.txt_username);
        btnNonPremium = view.findViewById(R.id.btn_nonPremium);
        btnPremium = view.findViewById(R.id.btn_premium);
        ImageView icProfile = view.findViewById(R.id.ic_profile);

        // Set the greeting message based on the time of day
        setGreetingMessage();

        // Get user email from session and fetch account details from DB
        String userEmail = userSession.getUserEmail();
        String username = dbHelper.getUsernameByEmail(userEmail);
        String accountType = dbHelper.getAccountTypeByEmail(userEmail);
        int userId = dbHelper.getUserIdByEmail(userEmail);  // Get the userId

        // Set the username in the TextView
        txtUsername.setText(username);

        // Set profile picture in circular shape
        setProfileImage(userId, icProfile);

        // Set button visibility based on accountType
        if ("Premium".equalsIgnoreCase(accountType)) {
            btnPremium.setVisibility(View.VISIBLE);
            btnNonPremium.setVisibility(View.GONE);
        } else {
            btnPremium.setVisibility(View.GONE);
            btnNonPremium.setVisibility(View.VISIBLE);
        }

        // Add click listeners for navigation
        ImageView icSettings = view.findViewById(R.id.ic_settings);

        // Navigate to SettingsActivity
        icSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        // Navigate to NonPremiumActivity
        btnNonPremium.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NonPremiumActivity.class);
            startActivity(intent);
        });

        // Navigate to PremiumMemberActivity
        btnPremium.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumMemberActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Method to set greeting message based on time
    private void setGreetingMessage() {
        int currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greetingMessage;

        if (currentHour >= 0 && currentHour < 12) {
            greetingMessage = "Good Morning";
        } else if (currentHour >= 12 && currentHour < 18) {
            greetingMessage = "Good Afternoon";
        } else {
            greetingMessage = "Good Evening";
        }

        txtGreeting.setText(greetingMessage);
    }

    // Method to set the profile image in a circular shape
    private void setProfileImage(int userId, ImageView imageView) {
        // Retrieve the user image from the database
        Bitmap bitmap = dbHelper.getUserImage(userId);

        if (bitmap != null) {
            // Set the image in circular shape
            imageView.setImageBitmap(getCircularBitmap(bitmap));
        } else {
            // Set a default image if there's no profile picture
            imageView.setImageResource(R.drawable.ic_profilepic);
        }
    }

    // Method to create a circular bitmap from a given image
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = Math.min(width, height) / 2;

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(output);

        // Create a circular path
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Draw the circle
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Set the paint mode to SRC_IN to display the image in a circular shape
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }
}
