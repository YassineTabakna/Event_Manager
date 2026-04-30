package com.example.eventmanager.domain;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME  = "session_prefs";
    private static final String KEY_ID     = "user_id";
    private static final String KEY_LOGGED = "is_logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(int userId) {
        prefs.edit()
                .putBoolean(KEY_LOGGED, true)
                .putInt(KEY_ID, userId)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_ID, -1);
    }
}