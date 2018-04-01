package info.ivicel.github.githubtrends.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import info.ivicel.github.githubtrends.AppApplication;
import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Language;
import info.ivicel.github.githubtrends.model.Repo;
import info.ivicel.github.githubtrends.request.GsonRequest;
import info.ivicel.github.githubtrends.util.AttrHelper;


public class RepoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "RepoListFragment";
    private static final String ARG_LANG = "argument_language";
    private static final String ARG_SINCE_TIME = "argument_since_time";
    
    private RepoRecyclerAdapter mRepoListAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private Language language;
    private String since;
    
    private BroadcastReceiver mThemeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefreshLayout();
        }
    };
    
    private BroadcastReceiver mRepoChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mRepoListAdapter.notifyDataSetChanged();
        }
    };
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        lbm.registerReceiver(mThemeChangedReceiver,
                        new IntentFilter(Constants.ACTION_THEME_CHANGED));
        lbm.registerReceiver(mRepoChangedReceiver, new IntentFilter(Constants.ACTION_REPO_CHANGED));
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            language = (Language)args.getSerializable(ARG_LANG);
            since = args.getString(ARG_SINCE_TIME);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.repo_list, container, false);
    
        mRecyclerView = view.findViewById(R.id.simple_recycler_view);
        mRepoListAdapter = new RepoRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(mRepoListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeColors(getThemePrimaryColor(getContext(), R.attr.colorPrimary));
        
        mSwipeRefresh.setOnRefreshListener(this);
        
        doRequest();

        return view;
    }
    
    @ColorInt
    private int getThemePrimaryColor(Context context, @AttrRes int res) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(res, typedValue, true);
        int color;
        if (Build.VERSION.SDK_INT >= 23) {
            color = context.getColor(typedValue.resourceId);
        } else {
            color = context.getResources().getColor(typedValue.resourceId);
        }
        return color;
    }
    
    public void updateTimeSince(String since) {
        this.since = since;
        AppApplication.getQueue().cancelAll(getRequestTag());
        doRequest();
    }
    
    public static RepoListFragment newInstance(Language lang, String since) {
        RepoListFragment fragment = new RepoListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LANG, lang);
        args.putString(ARG_SINCE_TIME, since);
        fragment.setArguments(args);
        return fragment;
    }
    
    private void doRequest() {
        String url = Constants.BASE_REQUEST_URL + "?language=" + language.getPath() +
                "&since=" + since;
        GsonRequest request = new GsonRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefresh.setRefreshing(false);
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.Listener<List<Repo>>() {
            @Override
            public void onResponse(List<Repo> response) {
                mSwipeRefresh.setRefreshing(false);
                mRepoListAdapter.updateRepositories(response);
            }
        });
    
        request.setTag(getRequestTag());
        AppApplication.getQueue().add(request);
        mSwipeRefresh.setRefreshing(true);
    }

    private String getRequestTag() {
        return language.getPath() + ":" + since;
    }
    
    @Override
    public void onRefresh() {
        doRequest();
    }
    
    public void onRefreshLayout() {
        TypedValue colorBackground = AttrHelper.resolveAttr(getContext(), R.attr.colorPrimary);
        mSwipeRefresh.setColorSchemeColors(colorBackground.data);
        
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            View v = mRecyclerView.getChildAt(i);
            if (v != null) {
                v.findViewById(R.id.repo_flag).setBackgroundResource(colorBackground.resourceId);
            }
        }
    
        clearRecyclerCache();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mThemeChangedReceiver);
        lbm.unregisterReceiver(mRepoChangedReceiver);
    }
    
    private void clearRecyclerCache() {
        try {
            Field field = RecyclerView.class.getDeclaredField("mRecycler");
            field.setAccessible(true);
            Method method = RecyclerView.Recycler.class.getDeclaredMethod("clear");
            method.setAccessible(true);
            method.invoke(field.get(mRecyclerView));
            RecyclerView.RecycledViewPool viewPool = mRecyclerView.getRecycledViewPool();
            viewPool.clear();
        } catch (NoSuchFieldException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            // e.printStackTrace();
        }
    }
}
