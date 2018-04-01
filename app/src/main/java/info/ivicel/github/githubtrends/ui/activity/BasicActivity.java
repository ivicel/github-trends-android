package info.ivicel.github.githubtrends.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toolbar;

import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.util.AttrHelper;
import info.ivicel.github.githubtrends.util.SharePrefHelper;


@SuppressLint("Registered")
public class BasicActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SharePrefHelper.getTheme(this));
    
        TypedValue tv = AttrHelper.resolveAttr(this, R.attr.colorPrimary);
        setStatusAndTaskColor(tv.data);
    }
    
    protected void setStatusAndTaskColor(@ColorInt int colorPrimary) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(colorPrimary);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null,
                    null, colorPrimary);
            setTaskDescription(td);
        }
    }
}
