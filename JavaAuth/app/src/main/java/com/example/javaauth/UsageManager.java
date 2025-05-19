package com.example.javaauth;
import android.content.Context;
import android.content.SharedPreferences;

public class UsageManager {

    private static UsageManager instance;
    public boolean isPremiumUser = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String premiumUserKey = "is_premium_user";
    private String urlCountKey = "free_user_url_count";
    private int freeTierLimit = 5;

    private UsageManager(Context context) {
        sharedPreferences = context.getSharedPreferences("url_usage", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isPremiumUser = sharedPreferences.getBoolean(premiumUserKey, false); // Leer valor persistido
    }

    public static synchronized UsageManager getInstance(Context context) {
        if (instance == null) {
            instance = new UsageManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setPremiumUser(boolean premiumUser) {
        this.isPremiumUser = premiumUser;
        editor.putBoolean(premiumUserKey, premiumUser);
        editor.apply(); // Guardar el valor
    }

    public boolean incrementUrlCount() {
        if (isPremiumUser) {
            return true;
        }

        int currentCount = sharedPreferences.getInt(urlCountKey, 0);
        if (currentCount < freeTierLimit) {
            editor.putInt(urlCountKey, currentCount + 1);
            editor.apply();
            return true;
        }
        return false;
    }

    public void resetUrlCount() {
        editor.putInt(urlCountKey, 0);
        editor.apply();
    }

    public int getRemainingUses() {
        if (isPremiumUser) {
            return Integer.MAX_VALUE;
        }
        int currentCount = sharedPreferences.getInt(urlCountKey, 0);
        return freeTierLimit - currentCount;
    }
}