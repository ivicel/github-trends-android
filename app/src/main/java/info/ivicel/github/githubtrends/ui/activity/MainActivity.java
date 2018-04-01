package info.ivicel.github.githubtrends.ui.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Language;
import info.ivicel.github.githubtrends.ui.AboutActivity;
import info.ivicel.github.githubtrends.ui.RepoListFragment;
import info.ivicel.github.githubtrends.ui.dialog.ThemeChooserDialog;
import info.ivicel.github.githubtrends.util.AttrHelper;
import info.ivicel.github.githubtrends.util.LangsHelper;
import info.ivicel.github.githubtrends.util.SharePrefHelper;

public class MainActivity extends BasicActivity implements AdapterView.OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String[] SINCE_TIME = {"daily", "weekly", "monthly"};
    private List<Language> mSelectedLanguages;
    private int mCurSincePos;
    private RepoPagerAdapter mRepoPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private BroadcastReceiver mThemeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefreshLayout();
        }
    };
    
    private BroadcastReceiver mSelectedLangChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onLanguageChanged();
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle(null);
        }
        
        mDrawerLayout = findViewById(R.id.drawer_layout);
        
        String[] sinceTimeText = getResources().getStringArray(R.array.since_time);
        mCurSincePos = SharePrefHelper.getSinceTime(this);
        
        mSelectedLanguages = LangsHelper.getInstance().getSelectedLanguages();
        ViewPager viewPager = findViewById(R.id.view_pager);
        mRepoPagerAdapter = new RepoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mRepoPagerAdapter);
        
        SpinnerAdapter mSpinnerAdapter = new SpinnerAdapter(this, sinceTimeText);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setSelection(mCurSincePos);
        
        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(viewPager);
        
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mThemeChangedReceiver, new IntentFilter(Constants.ACTION_THEME_CHANGED));
        lbm.registerReceiver(mSelectedLangChangedReceiver,
                new IntentFilter(Constants.ACTION_LANGUAGE_CHANGED));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            
            default:
                return super.onOptionsItemSelected(item);
        }
        
        return true;
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mCurSincePos == position) {
            return;
        }
        for (int i = 0; i < mRepoPagerAdapter.getCount(); i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                    mRepoPagerAdapter.getFragmentTag(R.id.view_pager, i));
            if (fragment != null && fragment.isAdded() &&
                    (fragment instanceof RepoListFragment)) {
                ((RepoListFragment)fragment).updateTimeSince(SINCE_TIME[position]);
            }
        }
        
        mCurSincePos = position;
        SharePrefHelper.putSinceTime(this, mCurSincePos);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_theme:
                chooseTheme();
                break;
                
            case R.id.nav_favorite:
                Intent i = FavoriteLanguageActivity.newIntent(this);
                startActivity(i);
                break;
                
            case R.id.nav_add_lang:
                Intent intent = AddLanguageActivity.newIntent(this);
                startActivity(intent);
                break;
                
            case R.id.nav_remove_lang:
                Intent ir = RemoveLanguageActivity.newIntent(this);
                startActivity(ir);
                break;

            case R.id.nav_about:
                Intent i2 = AboutActivity.newIntent(this);
                startActivity(i2);
                
            default:
                return false;
        }
        
        mDrawerLayout.closeDrawer(Gravity.START);
        return true;
    }
    
    private void chooseTheme() {
        DialogFragment dialog = new ThemeChooserDialog();
        dialog.show(getSupportFragmentManager(), "theme_dialog");
    }
    
    public void onRefreshLayout() {
        TypedValue colorBackground = AttrHelper.resolveAttr(this, R.attr.colorPrimary);
        mTabLayout.setBackgroundResource(colorBackground.resourceId);
        mToolbar.setBackgroundResource(colorBackground.resourceId);
        setStatusAndTaskColor(colorBackground.data);
        
        View v = mNavigationView.getHeaderView(0);
        if (v != null) {
            v.setBackgroundResource(colorBackground.resourceId);
        }
        
    }
    
    private void onLanguageChanged() {
        mSelectedLanguages = LangsHelper.getInstance().getSelectedLanguages();
        mRepoPagerAdapter.notifyDataSetChanged();
        LangsHelper.getInstance().notifyLanguageInserted(new ArrayList<Language>(0));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mThemeChangedReceiver);
        lbm.unregisterReceiver(mSelectedLangChangedReceiver);
    }
    
    class SpinnerAdapter extends ArrayAdapter<String> {
        SpinnerAdapter(@NonNull Context context, String[] data) {
            super(context, android.R.layout.simple_spinner_item, data);
        }
        
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }
            final String text = "Trends " + getItem(position);
            ((TextView)convertView).setText(text);
            
            return convertView;
        }
    }
    
    class RepoPagerAdapter extends FragmentPagerAdapter {
        public RepoPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            return RepoListFragment.newInstance(mSelectedLanguages.get(position), SINCE_TIME[mCurSincePos]);
        }
        
        
        @Override
        public int getCount() {
            return mSelectedLanguages != null ? mSelectedLanguages.size() : 0;
        }
        
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mSelectedLanguages.get(position).getName();
        }
        
        public String getFragmentTag(int viewId, int id) {
            return "android:switcher:" + viewId + ":" + id;
        }
    }
}
