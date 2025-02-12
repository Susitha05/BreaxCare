package com.example.breaxcare.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    private Spinner planSelectSpinner;
    private RadioGroup radioGroup;
    private EditText ccName, cardNo, expireDate, cvv;
    private Button btnPay;
    private DBhelper dbHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        planSelectSpinner = findViewById(R.id.planSelectSpinner);
        radioGroup = findViewById(R.id.radio_group);
        ccName = findViewById(R.id.ccName);
        cardNo = findViewById(R.id.cardNo);
        expireDate = findViewById(R.id.expireDate);
        cvv = findViewById(R.id.cvv);
        btnPay = findViewById(R.id.btn_pay);

        // Initialize database helper and session
        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        // Create an array for the spinner items
        String[] plans = new String[]{"2.99$ / Month", "29.99$ / Year"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plans);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        planSelectSpinner.setAdapter(adapter);

        // Set button click listener
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch user email from session
                String email = userSession.getUserEmail();

                if (email == null || email.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get user ID from database based on email
                int userId = dbHelper.getUserIdByEmail(email);

                if (userId == -1) {
                    Toast.makeText(PaymentActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get selected plan from spinner
                String selectedPlan = planSelectSpinner.getSelectedItem().toString();

                // Get selected card type
                int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioId);
                String selectedCardType = selectedRadioButton.getText().toString();

                // Get card details from inputs
                String cardHolderName = ccName.getText().toString();
                String cardNumber = cardNo.getText().toString();
                String expirationDate = expireDate.getText().toString();
                String cvvNumber = cvv.getText().toString();

                // Validate inputs
                if (cardHolderName.isEmpty() || cardNumber.isEmpty() || expirationDate.isEmpty() || cvvNumber.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert data into the Premium Subscription table
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String paymentDate = sdf.format(new Date());

                boolean isInserted = dbHelper.insertPremiumSubscription(userId, selectedPlan, selectedCardType, cardHolderName, cardNumber, expirationDate, cvvNumber, paymentDate);

                // Update account type in Users table to "Premium"
                boolean isUpdated = dbHelper.updateAccountType(userId, "Premium");
                dbHelper.setOngoingPaymentCard(userId, cardNumber);
                if (isInserted && isUpdated) {
                    Toast.makeText(PaymentActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                    Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(PaymentActivity.this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
