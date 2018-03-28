package info.ivicel.github.githubtrends.ui.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import info.ivicel.github.githubtrends.R;

public class RepoPageActivity extends BasicActivity {
    private static final String TAG = "RepoPageActivity";
    
    private static final String ARGUMRNT_URL = "arg_url";
    
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_web_page);
        
        Intent args = getIntent();
        String url = null;
        if (args != null) {
            url = args.getStringExtra(ARGUMRNT_URL);
        }
    
        mProgressBar = findViewById(R.id.progress_bar);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        WebView webView = findViewById(R.id.repo_web_view);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
    
            @Override
            public void onReceivedTitle(WebView view, String title) {
                mToolbar.setTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                
                return !url.contains("http") || super.shouldOverrideUrlLoading(view, url);
            }
    
            @TargetApi(21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
        });
        webView.loadUrl(url);
        Log.d(TAG, "onCreate: " + url);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            
            default:
                return super.onOptionsItemSelected(item);
        }
        
        return true;
    }
    
    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, RepoPageActivity.class);
        intent.putExtra(ARGUMRNT_URL, url);
        
        return intent;
    }
}
