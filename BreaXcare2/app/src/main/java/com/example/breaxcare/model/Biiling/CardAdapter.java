package com.example.breaxcare.model.Biiling;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Card> cardList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = -1; // Track the selected position

    public CardAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);

        if (card != null) {
            holder.cardType.setText(card.getCardType() != null ? card.getCardType() : "Unknown");
            holder.cardNo.setText(formatCardNo(card.getCardNo()));
            holder.expiryDate.setText(card.getExpiryDate() != null ? card.getExpiryDate() : "N/A");

            // Set the card image based on the card type
            int cardImageResId = getCardImageResId(card.getCardType());
            holder.cardImage.setImageResource(cardImageResId);

            // Highlight the selected card
            CardView cardView = (CardView) holder.itemView;
            if (selectedPosition == position) {
                cardView.setCardBackgroundColor(Color.parseColor("#52a447")); // Selected color
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#DFDFDF")); // Default color
            }

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    selectedPosition = position; // Update selected position
                    notifyDataSetChanged(); // Refresh to apply color changes
                    onItemClickListener.onItemClick(card);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void clearSelection() {
        selectedPosition = -1; // Reset selected position
        notifyDataSetChanged(); // Refresh to remove selection highlight
    }

    public Card getSelectedCard() {
        if (selectedPosition != -1) {
            return cardList.get(selectedPosition);
        }
        return null;
    }

    public void setDefaultSelectedCard(String defaultCardNo) {
        // Find the position of the card matching the given card number
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).getCardNo().equals(defaultCardNo)) {
                selectedPosition = i; // Set the default selected position
                notifyDataSetChanged(); // Refresh to apply selection
                break;
            }
        }
    }

    public void setSelectedCard(Card selectedCard) {
        // Find the position of the selected card and update selectedPosition
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).equals(selectedCard)) {
                selectedPosition = i;
                notifyDataSetChanged(); // Refresh to apply color changes
                break;
            }
        }
    }
    public interface OnItemClickListener {
        void onItemClick(Card card);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView cardType, cardNo, expiryDate;
        ImageView cardImage;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardType = itemView.findViewById(R.id.Card_Type);
            cardNo = itemView.findViewById(R.id.Card_No);
            expiryDate = itemView.findViewById(R.id.expireDate);
            cardImage = itemView.findViewById(R.id.Card_Img);
        }
    }

    // Method to format card number (e.g., XXXX-XXXX-XXXX-1234)
    private String formatCardNo(String cardNo) {
        if (cardNo != null && cardNo.length() >= 4) {
            return "XXXX-XXXX-XXXX-" + cardNo.substring(cardNo.length() - 4);
        }
        return "Invalid Card No"; // Handle invalid card numbers
    }

    // Helper method to get card image resource ID
    private int getCardImageResId(String cardType) {
        if (cardType == null) return R.drawable.ic_visa; // Default card image
        switch (cardType) {
            case "Visa":
                return R.drawable.ic_visa;
            case "Mastercard":
                return R.drawable.ic_mastercard;
            case "American Express":
                return R.drawable.ic_american_express;
            default:
                return R.drawable.ic_visa; // Default fallback image
        }
    }
}
