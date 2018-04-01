package info.ivicel.github.githubtrends.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.ui.activity.BasicActivity;

public class AboutActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.about_text);
        setSupportActionBar(toolbar);

    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
}
