package info.ivicel.github.githubtrends;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import info.ivicel.github.githubtrends.util.FavorHelper;
import info.ivicel.github.githubtrends.util.LangsHelper;


public class AppApplication extends Application {
    private static final String TAG = "AppApplication";
    private static RequestQueue sQueue;
    
    @Override
    public void onCreate() {
        super.onCreate();
    
        LangsHelper.init(this);
        FavorHelper.init(this);
        sQueue = Volley.newRequestQueue(this);
    }
    
    public static RequestQueue getQueue() {
        return sQueue;
    }
}
