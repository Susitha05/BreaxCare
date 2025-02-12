package com.example.breaxcare.model;

public class MedicalHistory {
    private int userId;
    private String result;
    private String roundnessColor;
    private int whiteDotsCount;
    private double whiteDotsPercentage;
    private String riskLevel;
    private String date;

    public MedicalHistory(int userId, String result, String roundnessColor, int whiteDotsCount, double whiteDotsPercentage, String riskLevel, String date) {
        this.userId = userId;
        this.result = result;
        this.roundnessColor = roundnessColor;
        this.whiteDotsCount = whiteDotsCount;
        this.whiteDotsPercentage = whiteDotsPercentage;
        this.riskLevel = riskLevel;
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public String getResult() {
        return result;
    }

    public String getRoundnessColor() {
        return roundnessColor;
    }

    public int getWhiteDotsCount() {
        return whiteDotsCount;
    }

    public double getWhiteDotsPercentage() {
        return whiteDotsPercentage;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getDate() {
        return date;
    }
}
