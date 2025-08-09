package com.godvpn.nativehello;
import android.content.Context;
import android.content.SharedPreferences;

public class AuthStore {
    private static final String P = "auth_prefs";
    private static final String K = "logged_in";

    public static boolean isLoggedIn(Context c) {
        return c.getSharedPreferences(P, Context.MODE_PRIVATE).getBoolean(K, false);
    }
    public static void setLoggedIn(Context c, boolean v) {
        SharedPreferences sp = c.getSharedPreferences(P, Context.MODE_PRIVATE);
        sp.edit().putBoolean(K, v).apply();
    }
    public static void logout(Context c) { setLoggedIn(c, false); }
}
