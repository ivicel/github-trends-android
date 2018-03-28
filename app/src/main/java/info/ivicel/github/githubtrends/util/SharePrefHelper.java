package info.ivicel.github.githubtrends.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import info.ivicel.github.githubtrends.R;

public class SharePrefHelper {
    private static final String SINCE_TIME = "since_time";
    private static final String THEME = "theme";
    private static final String SELECTED_LANGUAGE = "selected_language";
    private static final String FAVORITE_LANGUAGE = "favorite_language";
    
    private SharePrefHelper() {}
    
    private static SharedPreferences g(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    private static SharedPreferences.Editor e(Context context) {
        return g(context).edit();
    }
    
    public static int getSinceTime(Context context) {
        return g(context).getInt(SINCE_TIME, 0);
    }
    
    public static void putSinceTime(Context context, int pos) {
        e(context).putInt(SINCE_TIME, pos).apply();
    }
    public static void saveTheme(Context context, int theme) {
        e(context).putInt(THEME, theme).apply();
    }
    
    public static int getTheme(Context context) {
        return g(context).getInt(THEME, R.style.CyanTheme);
    }
    
    public static String getSelectedLangs(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SELECTED_LANGUAGE, Context.MODE_PRIVATE);
        return sp.getString(SELECTED_LANGUAGE, null);
    }
    
    public static void saveSelectedLangs(Context context, String jsonString) {
        SharedPreferences sp = context.getSharedPreferences(SELECTED_LANGUAGE, Context.MODE_PRIVATE);
        sp.edit().putString(SELECTED_LANGUAGE, jsonString).apply();
    }
    
    public static String getFavoriteLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FAVORITE_LANGUAGE, Context.MODE_PRIVATE);
        return sp.getString(FAVORITE_LANGUAGE, null);
    }
    
    public static void saveFavoriteLanguage(Context context, String jsonString) {
        SharedPreferences sp = context.getSharedPreferences(FAVORITE_LANGUAGE, Context.MODE_PRIVATE);
        sp.edit().putString(FAVORITE_LANGUAGE, jsonString).apply();
    }
}
