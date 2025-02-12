package com.example.breaxcare.views.usershandler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.views.NewPassFragment;

import java.util.Random;

public class VerifyPassFragment extends Fragment {

    private static final String CHANNEL_ID = "OTP_NOTIFICATION_CHANNEL";
    private static final String ARG_EMAIL = "email";

    private String userEmail;

    private EditText etVerifyCode;
    private Button btnVerify;
    private TextView txtResendOTP;

    private SharedPreferences sharedPreferences;

    public VerifyPassFragment() {
        // Required empty public constructor
    }

    public static VerifyPassFragment newInstance(String email) {
        VerifyPassFragment fragment = new VerifyPassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
        }

        sharedPreferences = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_pass, container, false);

        // Bind UI elements
        etVerifyCode = view.findViewById(R.id.et_verifycode);
        btnVerify = view.findViewById(R.id.btn_verify);
        txtResendOTP = view.findViewById(R.id.txt_resendOTP);

        // Bind and set the passed email to the TextView
        TextView txtVerification = view.findViewById(R.id.txt_verification);
        if (userEmail != null) {
            txtVerification.setText(userEmail);
        }

        // Set listeners
        txtResendOTP.setOnClickListener(v -> resendOTP());
        btnVerify.setOnClickListener(v -> verifyOTP());

        return view;
    }

    private void resendOTP() {
        // Generate new OTP
        String newOtp = generateOTP();

        // Store OTP in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("OTP", newOtp);
        editor.apply();

        // Send OTP as notification
        sendNotification("Your new OTP is: " + newOtp);

        Toast.makeText(getContext(), "A new OTP has been sent.", Toast.LENGTH_SHORT).show();
    }

    private void verifyOTP() {
        String enteredOtp = etVerifyCode.getText().toString().trim();
        String storedOtp = sharedPreferences.getString("OTP", "");

        if (enteredOtp.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredOtp.equals(storedOtp)) {
            Toast.makeText(getContext(), "OTP verified successfully!", Toast.LENGTH_SHORT).show();

            // Open NewPassFragment and pass the email
            NewPassFragment newPassFragment = NewPassFragment.newInstance(userEmail);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newPassFragment)
                    .addToBackStack(null)
                    .commit();

        } else {
            Toast.makeText(getContext(), "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(999999);
        return String.format("%06d", otp);
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_google)
                .setContentTitle("Account Verification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OTP Notifications";
            String description = "Channel for OTP notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
