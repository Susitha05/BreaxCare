package com.example.breaxcare.views.usershandler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;

import java.util.Random;

import static android.Manifest.permission.POST_NOTIFICATIONS;

public class ForgotPassFragment extends Fragment {

    // SharedPreferences for OTP storage
    private SharedPreferences sharedPreferences;

    // UI elements
    private EditText etUsername, etPassword;
    private Button btnResetPassword;
    private TextView txtCancel;

    private DBhelper dbHelper;

    private static final String CHANNEL_ID = "OTP_Channel";

    public ForgotPassFragment() {

    }

    public static ForgotPassFragment newInstance(String param1, String param2) {
        ForgotPassFragment fragment = new ForgotPassFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        dbHelper = new DBhelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_pass, container, false);

        // Bind UI elements
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnResetPassword = view.findViewById(R.id.btn_rt_pass);
        txtCancel = view.findViewById(R.id.txt_cancel);

        // Set listeners
        btnResetPassword.setOnClickListener(v -> checkUserAndSendOTP());
        txtCancel.setOnClickListener(v -> goBackToLogin());

        return view;
    }

    // Check if email exists in DB and send OTP
    private void checkUserAndSendOTP() {
        String email = etUsername.getText().toString().trim();
        String lastPassword = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email exists in DB
        boolean userExists = dbHelper.checkUserEmail(email);

        if (userExists) {
            // Check notification permission before sending OTP
            if (ContextCompat.checkSelfPermission(getContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // If permission granted, generate OTP and send
                String otp = generateOTP();
                saveOTP(otp);
                Toast.makeText(getContext(), "OTP sent as a notification", Toast.LENGTH_SHORT).show();

                // Send notification
                sendOTPNotification(otp);

                // If everything is fine, open VerifyPassFragment
                openVerifyPassFragment(email);

            } else {
                // Request permission if not granted
                ActivityCompat.requestPermissions(getActivity(), new String[]{POST_NOTIFICATIONS}, 1);
            }

        } else {
            // Show error message if email doesn't exist
            Toast.makeText(getContext(), "Email not found. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Generate a 6-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(999999);
        return String.format("%06d", otp);
    }

    // Save OTP to SharedPreferences
    private void saveOTP(String otp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("OTP", otp);
        editor.apply();
    }

    // Send OTP Notification
    private void sendOTPNotification(String otp) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0 and above, you need to create a Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "OTP Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentTitle("Password Reset OTP")
                .setContentText("Your OTP for password reset is: " + otp)
                .setSmallIcon(R.drawable.ic_calender) // Make sure to have an icon for the notification
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    // Open VerifyPassFragment and pass email
    private void openVerifyPassFragment(String email) {
        VerifyPassFragment verifyPassFragment = VerifyPassFragment.newInstance(email);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, verifyPassFragment)
                .addToBackStack(null)
                .commit();
    }

    // Handle cancel action and go back to LoginFragment
    private void goBackToLogin() {
        // Replace fragment with LoginFragment
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to send OTP
                checkUserAndSendOTP();
            } else {
                // Permission denied, inform the user
                Toast.makeText(getContext(), "Notification permission is required to send OTP", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
