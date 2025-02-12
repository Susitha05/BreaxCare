package com.example.breaxcare.model.Biiling;

public class BillingHistoryItem {
    private String paymentDate;
    private String cardNo;
    private String cardType;

    public BillingHistoryItem(String paymentDate, String cardNo, String cardType) {
        this.paymentDate = paymentDate;
        this.cardNo = cardNo;
        this.cardType = cardType;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getCardType() {
        return cardType;
    }
}
