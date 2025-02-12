package com.example.breaxcare.views.Premium;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.Biiling.Card;
import com.example.breaxcare.model.Biiling.CardAdapter;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.util.List;

public class UpdatePaymentCardActivity extends AppCompatActivity {

    private RecyclerView payCardRecyclerView;
    private CardAdapter cardAdapter;
    private DBhelper dbHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_payment_card);

        // Handle system insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Initialize database helper and user session
        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        // Initialize RecyclerView for displaying cards
        payCardRecyclerView = findViewById(R.id.payCardRecyclerView);
        payCardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch user email and corresponding user ID
        String userEmail = userSession.getUserEmail();
        int userId = dbHelper.getUserIdByEmail(userEmail);

        // Fetch the user's cards from the database
        List<Card> cardList = dbHelper.getCardsByUserId(userId);

        // Set up the CardAdapter with card data
        cardAdapter = new CardAdapter(cardList);
        payCardRecyclerView.setAdapter(cardAdapter);

        // Fetch the default card number for the user
        String defaultCardNo = dbHelper.getDefaultCardForUser(userId); // Implement this method in DBHelper

        if (defaultCardNo != null) {
            cardAdapter.setDefaultSelectedCard(defaultCardNo);
        }

        // Handle card selection
        cardAdapter.setOnItemClickListener(card -> {
            // Set the selected card in the adapter
            cardAdapter.setSelectedCard(card);
        });

        // Handle save card button click
        findViewById(R.id.btn_saveCard).setOnClickListener(v -> {
            // Get the selected card from the adapter
            Card selectedCard = cardAdapter.getSelectedCard();

            if (selectedCard != null) {
                // Save the selected card as the ongoing payment card in the database
                dbHelper.setOngoingPaymentCard(userId, selectedCard.getCardNo());
                Toast.makeText(this, "Payment card updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                // Show a message if no card is selected
                Toast.makeText(this, "Please select a card before saving.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
