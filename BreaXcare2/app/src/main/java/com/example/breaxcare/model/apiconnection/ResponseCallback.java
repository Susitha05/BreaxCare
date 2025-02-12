package com.example.breaxcare.model.apiconnection;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}