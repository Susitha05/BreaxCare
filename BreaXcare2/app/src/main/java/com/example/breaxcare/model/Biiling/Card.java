package com.example.breaxcare.model.Biiling;

public class Card {
    private String cardType;
    private String cardNo;
    private String expiryDate;
    private String cardHolderName;  // Added card holder name
    private String cvv;  // Added CVV

    public Card(String cardType, String cardNo, String expiryDate, String cardHolderName, String cvv) {
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
        this.cvv = cvv;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCvv() {
        return cvv;
    }
}
