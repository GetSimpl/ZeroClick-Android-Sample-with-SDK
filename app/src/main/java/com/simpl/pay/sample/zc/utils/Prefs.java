package com.simpl.pay.sample.zc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Prefs {
    public static final String MERCHANT_ID = "merchant_id";
    public static final String ZERO_CLICK_TOKEN = "zero_click_token";
    public static final String PHONE_NO = "phone_no";
    public static final String EMAIL = "email";
    public static final String AMOUNT_IN_APPROVAL = "amount_in_approval";

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences("user_data", MODE_PRIVATE);
    }

    public static String getStringValue(Context context, String key) {
        return getSharedPrefs(context).getString(key, "");
    }

    public static boolean getBooleanValue(Context context, String key) {
        return getSharedPrefs(context).getBoolean(key, true);
    }

    public static void setStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setBooleanValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.clear();
        editor.apply();
    }
}
