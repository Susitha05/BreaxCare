package com.example.breaxcare.views.Premium;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.model.Biiling.BillingHistoryAdapter;
import com.example.breaxcare.model.Biiling.BillingHistoryItem;

import java.util.List;

public class BillingHistoryActivity extends AppCompatActivity {

    private DBhelper dbHelper;
    private UserSession userSession;
    private RecyclerView billingRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_billing_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        // Get the user's email and ID
        String userEmail = userSession.getUserEmail();
        int userId = dbHelper.getUserIdByEmail(userEmail);

        // Fetch the billing history for the user
        List<BillingHistoryItem> billingHistory = dbHelper.getBillingHistoryByUserId(userId);

        // Initialize the RecyclerView
        billingRecyclerView = findViewById(R.id.billingRecyclerView);
        billingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter
        BillingHistoryAdapter adapter = new BillingHistoryAdapter(billingHistory);
        billingRecyclerView.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }
}
