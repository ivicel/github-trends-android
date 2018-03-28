package info.ivicel.github.githubtrends.util;


import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import info.ivicel.github.githubtrends.model.Repo;

public class FavorHelper {
    private static List<Repo> favoriteLangs;
    
    private FavorHelper() {}
    
    public static void init(Context context) {
        String jsonString = SharePrefHelper.getFavoriteLanguage(context);
        Gson gson = new Gson();
        favoriteLangs = gson.fromJson(jsonString,
                new TypeToken<List<Repo>>(){}.getType());
    
        if (jsonString == null) {
            favoriteLangs = new LinkedList<>();
        }
    }
    
    public static void remove(Context context, Repo repo) {
        favoriteLangs.remove(repo);
        SharePrefHelper.saveFavoriteLanguage(context, toJsonString());
    }
    
    public static boolean toggle(Context context, Repo repo) {
        boolean contains = favoriteLangs.contains(repo);
        if (contains) {
            favoriteLangs.remove(repo);
        } else {
            favoriteLangs.add(repo);
        }
        SharePrefHelper.saveFavoriteLanguage(context, toJsonString());
        return !contains;
    }
    
    public static boolean contains(Repo repo) {
        return favoriteLangs.size() != 0 && favoriteLangs.contains(repo);
    }
    
    public static List<Repo> getFavors() {
        return favoriteLangs;
    }
    
    private static String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(favoriteLangs);
    }
}
