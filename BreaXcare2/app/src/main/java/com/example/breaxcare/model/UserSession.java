package com.example.breaxcare.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREFS_NAME = "UserPrefs";  // SharedPreferences file name
    private static final String KEY_EMAIL = "email";  // Key for storing email
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";  // Key for storing login status

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user credentials and set login status
    public void createUserSession(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get stored user credentials
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Clear session data (log out)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
