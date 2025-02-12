package com.example.breaxcare.views.Premium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PremiumMemberActivity extends AppCompatActivity {

    private UserSession userSession;
    private DBhelper dbHelper;
    private String userEmail;
    private int userId;

    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvBillingDate;
    private ImageView profileImageView; // ImageView for profile picture

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_premium_member);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize session and database helper
        userSession = new UserSession(this);
        dbHelper = new DBhelper(this);

        // Get user email from session and user ID from the database
        userEmail = userSession.getUserEmail();
        userId = dbHelper.getUserIdByEmail(userEmail);

        // Initialize TextViews and ImageView
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvBillingDate = findViewById(R.id.tv_billingDate);
        profileImageView = findViewById(R.id.ic_profileimg); // Initialize profile image view

        // Fetch user data and set it in TextViews
        String username = dbHelper.getUsernameByUserId(userId);
        String paymentDate = dbHelper.getPaymentDateByUserId(userId);
        String subscriptionType = dbHelper.getSubscriptionTypeByUserId(userId);

        tvUsername.setText(username != null ? username : "N/A");
        tvEmail.setText(userEmail != null ? userEmail : "N/A");

        if (paymentDate != null && subscriptionType != null) {
            tvBillingDate.setText("Billing Date : " + calculateNextBillingDate(paymentDate, subscriptionType));
        } else {
            tvBillingDate.setText("N/A");
        }

        // Set profile image
        setProfileImage(userId);

        // Button click listeners
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_updatepayment).setOnClickListener(v -> {
            Intent intent = new Intent(PremiumMemberActivity.this, UpdatePaymentCardActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_subscription).setOnClickListener(v -> {
            Intent intent = new Intent(PremiumMemberActivity.this, ManageSubscriptionActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_cards).setOnClickListener(v -> {
            Intent intent = new Intent(PremiumMemberActivity.this, SavedCardActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_billing).setOnClickListener(v -> {
            Intent intent = new Intent(PremiumMemberActivity.this, BillingHistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_redeem).setOnClickListener(v -> {
            Intent intent = new Intent(PremiumMemberActivity.this, RedeemActivity.class);
            startActivity(intent);
        });
    }

    private String calculateNextBillingDate(String paymentDate, String subscriptionType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(paymentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if ("2.99$ / Month".equals(subscriptionType)) {
                calendar.add(Calendar.MONTH, 1);
            } else if ("29.99$ / Year".equals(subscriptionType)) {
                calendar.add(Calendar.YEAR, 1);
            }

            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    // Method to set the profile image in a circular shape
    private void setProfileImage(int userId) {
        // Retrieve the user image from the database
        Bitmap bitmap = dbHelper.getUserImage(userId);

        if (bitmap != null) {
            // Set the image in circular shape
            profileImageView.setImageBitmap(getCircularBitmap(bitmap));
        } else {
            // Set a default image if there's no profile picture
            profileImageView.setImageResource(R.drawable.ic_profilepic);
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
