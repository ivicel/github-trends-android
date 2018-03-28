package info.ivicel.github.githubtrends.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ivicel.github.githubtrends.model.Language;

public class LangsHelper {
    private List<Language> mSelectedLangs;
    private Map<String, String> mAllLangs = new HashMap<>();
    @SuppressLint("StaticFieldLeak")
    private static LangsHelper sHelper;
    private Context mAppContext;
    
    public static LangsHelper getInstance() {
        if (sHelper == null) {
            throw new IllegalArgumentException("You must initialize an instance before call this");
        }
        return sHelper;
    }
    
    public static void init(Context context) {
        sHelper = new LangsHelper(context);
    }
    
    private LangsHelper(Context context) {
        AssetManager am = context.getAssets();
        mAppContext = context.getApplicationContext();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    am.open("langs.json")));
            Gson gson = new Gson();
            List<Language> languages = gson.fromJson(reader, new TypeToken<List<Language>>() {
            }.getType());
            for (Language lang : languages) {
                mAllLangs.put(lang.getName(), lang.getPath());
            }
        } catch (IOException e) {
        }
        initSelectedLangs();
    }
    
    private void initSelectedLangs() {
        String languages = SharePrefHelper.getSelectedLangs(mAppContext);
        mSelectedLangs = new Gson().fromJson(languages, new TypeToken<List<Language>>() {
        }.getType());
        
        if (mSelectedLangs == null) {
            setDefaultLangs();
        }
    }
    
    public List<Language> getSelectedLanguages() {
        return mSelectedLangs;
    }
    
    public List<Language> getUnselectedLangs() {
        List<Language> items = new ArrayList<>();
        for (String key : mAllLangs.keySet()) {
            final Language language = new Language(key, mAllLangs.get(key));
            if (!mSelectedLangs.contains(language)) {
                items.add(language);
            }
        }
        return items;
    }
    
    private void setDefaultLangs() {
        final String[] names = {"All Language", "C", "C++", "C#", "Java", "JavaScript", "Kotlin",
                "Python", "Ruby"};
        mSelectedLangs = new ArrayList<>();
        for (String name : names) {
            final String path = mAllLangs.get(name);
            Language language = new Language(name, path);
            mSelectedLangs.add(language);
        }
        
        notifyLanguageInserted(new ArrayList<Language>());
    }
    
    public List<Language> getAllLangs() {
        List<Language> languages = new ArrayList<>();
        for (Map.Entry<String, String> lang : mAllLangs.entrySet()) {
            Language language = new Language(lang.getKey(), lang.getValue());
            languages.add(language);
        }
        
        return languages;
    }
    
    public void notifyLanguageInserted(@NonNull Collection<Language> languages) {
        mSelectedLangs.addAll(languages);
        SharePrefHelper.saveSelectedLangs(mAppContext, toJsonString());
    }
    
    public void notifyLanguageChanged(@NonNull Collection<Language> c) {
        mSelectedLangs = new ArrayList<>(c);
        SharePrefHelper.saveSelectedLangs(mAppContext, toJsonString());
    }
    
    private String toJsonString() {
        return new Gson().toJson(mSelectedLangs,
                new TypeToken<List<Language>>(){}.getType());
    }
    
}
