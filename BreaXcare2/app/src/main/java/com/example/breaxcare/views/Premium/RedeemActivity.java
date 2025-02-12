package com.example.breaxcare.views.Premium;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breaxcare.R;
import com.google.android.material.button.MaterialButton;

public class RedeemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        EditText redeemCodeInput = findViewById(R.id.redeemCode);
        MaterialButton redeemButton = findViewById(R.id.btn_CardUpdate);

        redeemButton.setOnClickListener(v -> {
            String redeemCode = redeemCodeInput.getText().toString().trim();

            if (redeemCode.isEmpty()) {
                Toast.makeText(RedeemActivity.this, "Please enter a redeem code!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RedeemActivity.this, "Redeem Code: " + redeemCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
