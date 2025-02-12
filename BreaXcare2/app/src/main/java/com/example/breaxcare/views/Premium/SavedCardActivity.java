package com.example.breaxcare.views.Premium;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.Biiling.Card;
import com.example.breaxcare.model.Biiling.CardAdapter;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;

import java.util.List;

public class SavedCardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private DBhelper dbHelper;
    private UserSession userSession;
    private EditText etCardName, etCardNo, etExpiryDate, etCVV;
    private RadioGroup radioGroupCardType;
    private Card selectedCard;  // Track the selected card for update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_card);

        // Initialize views
        recyclerView = findViewById(R.id.cardRecyclerView);
        etCardName = findViewById(R.id.cardName);
        etCardNo = findViewById(R.id.cardNo);
        etExpiryDate = findViewById(R.id.expireDate);
        etCVV = findViewById(R.id.cvv);
        radioGroupCardType = findViewById(R.id.radioGroupCardType);

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        String userEmail = userSession.getUserEmail();
        int userId = dbHelper.getUserIdByEmail(userEmail);

        // Fetch the user's cards from the database
        List<Card> cardList = dbHelper.getCardsByUserId(userId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Set up item click listener
        cardAdapter.setOnItemClickListener(card -> {
            selectedCard = card;  // Store the selected card

            // Set the radio button based on the CardType
            setRadioButtonBasedOnCardType(card.getCardType());

            // Set the EditText fields with the card details
            etCardName.setText(card.getCardHolderName());
            etCardNo.setText(card.getCardNo());
            etExpiryDate.setText(card.getExpiryDate());
            etCVV.setText(card.getCvv());
        });

        // Handle save button click
        findViewById(R.id.btn_CardUpdate).setOnClickListener(v -> {
            String cardType = getSelectedCardType();
            Log.d("SavedCardActivity", "Selected Card Type (onSave): " + cardType);
            if (cardType.isEmpty()) {
                Toast.makeText(SavedCardActivity.this, "Please select a card type", Toast.LENGTH_SHORT).show();
                return;
            }

            String cardHolderName = etCardName.getText().toString();
            String cardNo = etCardNo.getText().toString();
            String expiryDate = etExpiryDate.getText().toString();
            String cvv = etCVV.getText().toString();

            if (selectedCard != null) {
                // Update existing card
                boolean updateSuccess = dbHelper.updateCardDetails(cardHolderName, cardNo, expiryDate, cvv, cardType);
                if (updateSuccess) {
                    Toast.makeText(SavedCardActivity.this, "Card details updated successfully", Toast.LENGTH_SHORT).show();
                    cardAdapter.notifyDataSetChanged();
                    onBackPressed();
                } else {
                    Toast.makeText(SavedCardActivity.this, "Error updating card details", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Insert a new card for the user
                boolean insertSuccess = dbHelper.insertAlterCard(userId, cardType, cardHolderName, cardNo, expiryDate, cvv);
                if (insertSuccess) {
                    Toast.makeText(SavedCardActivity.this, "New card added successfully", Toast.LENGTH_SHORT).show();
                    cardAdapter.notifyDataSetChanged();
                    onBackPressed();
                } else {
                    Toast.makeText(SavedCardActivity.this, "Error adding new card", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Handle clear button click
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            selectedCard = null; // Clear the selected card
            cardAdapter.clearSelection(); // Clear selection in the adapter
            radioGroupCardType.clearCheck(); // Clear radio button selection
            etCardName.setText("");
            etCardNo.setText("");
            etExpiryDate.setText("");
            etCVV.setText("");
        });
    }

    private void setRadioButtonBasedOnCardType(String cardType) {
        switch (cardType) {
            case "Visa":
                radioGroupCardType.check(R.id.radioVisa);
                break;
            case "Mastercard":
                radioGroupCardType.check(R.id.radioMasterCard);
                break;
            case "American Express":
                radioGroupCardType.check(R.id.radioAmex);
                break;
            default:
                radioGroupCardType.clearCheck();
                break;
        }
    }

    private String getSelectedCardType() {
        int selectedRadioButtonId = radioGroupCardType.getCheckedRadioButtonId();
        Log.d("SavedCardActivity", "Selected RadioButton ID (getSelectedCardType): " + selectedRadioButtonId);

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String selectedCardType = (String) selectedRadioButton.getContentDescription();
            Log.d("SavedCardActivity", "Selected Card Type: " + selectedCardType);
            return selectedCardType;
        }
        return "";
    }
}
