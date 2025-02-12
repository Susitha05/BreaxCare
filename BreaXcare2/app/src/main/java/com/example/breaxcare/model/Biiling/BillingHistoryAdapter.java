package com.example.breaxcare.model.Biiling;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BillingHistoryAdapter extends RecyclerView.Adapter<BillingHistoryAdapter.ViewHolder> {

    private List<BillingHistoryItem> billingHistory;

    public BillingHistoryAdapter(List<BillingHistoryItem> billingHistory) {
        this.billingHistory = billingHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillingHistoryItem item = billingHistory.get(position);

        // Check if paymentDate is null and hide it if so
        if (item.getPaymentDate() != null && !TextUtils.isEmpty(item.getPaymentDate())) {
            String formattedDate = formatDate(item.getPaymentDate());
            holder.paymentDateTextView.setText(formattedDate);
            holder.paymentDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.paymentDateTextView.setVisibility(View.GONE); // Hide if null or empty
        }

        // Format the card number to show only the last 4 digits
        String cardNo = item.getCardNo();
        if (cardNo != null && cardNo.length() >= 4) {
            String formattedCardNo = "XXXX-XXXX-XXXXX-" + cardNo.substring(cardNo.length() - 4);
            holder.cardNoTextView.setText(formattedCardNo);
        } else {
            holder.cardNoTextView.setText("Invalid Card Number");
        }

        // Set the card type
        holder.cardTypeTextView.setText(item.getCardType());
    }

    @Override
    public int getItemCount() {
        return billingHistory.size();
    }

    // Helper method to format the payment date to only show the date (yyyy-MM-dd)
    private String formatDate(String paymentDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(paymentDate); // Parse the input string
            return outputFormat.format(date); // Format to only show the date
        } catch (ParseException e) {
            e.printStackTrace();
            return paymentDate; // Return the original date if parsing fails
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView paymentDateTextView;
        TextView cardNoTextView;
        TextView cardTypeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paymentDateTextView = itemView.findViewById(R.id.paymentDateTextView);
            cardNoTextView = itemView.findViewById(R.id.cardNoTextView);
            cardTypeTextView = itemView.findViewById(R.id.cardTypeTextView);
        }
    }
}
