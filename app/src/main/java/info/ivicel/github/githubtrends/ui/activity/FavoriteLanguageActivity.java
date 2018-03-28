package info.ivicel.github.githubtrends.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Repo;
import info.ivicel.github.githubtrends.ui.RepoRecyclerAdapter;
import info.ivicel.github.githubtrends.util.FavorHelper;

public class FavoriteLanguageActivity extends BasicActivity {
    private RepoRecyclerAdapter mRepoListAdapter;
    private boolean isRepoChanged = false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);
    
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.favorite_string);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    
        mRepoListAdapter = new FavorRepositoryAdapter(this);
        RecyclerView rv = findViewById(R.id.simple_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mRepoListAdapter);
        mRepoListAdapter.updateRepositories(FavorHelper.getFavors());
    }
    
    public static Intent newIntent(Context context) {
        return new Intent(context, FavoriteLanguageActivity.class);
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
    
    @Override
    protected void onStop() {
        super.onStop();
        if (isRepoChanged) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(
                    new Intent(Constants.ACTION_REPO_CHANGED));
        }
    }
    
    private class FavorRepositoryAdapter extends RepoRecyclerAdapter {
    
        public FavorRepositoryAdapter(Context context) {
            super(context);
        }
    
        @NonNull
        @Override
        public FavorRepositoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                int viewType) {
            View v = getLayoutInflater().inflate(getItemResource(), parent, false);
            return new ViewHolder(v);
        }
    
        class ViewHolder extends RepoRecyclerAdapter.ViewHolder {
    
            public ViewHolder(View itemView) {
                super(itemView);
            }
    
            @Override
            protected void setOnFavorClickListener(final Repo repo) {
                mFavorBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FavorHelper.remove(mContext, repo);
                        mRepoListAdapter.notifyItemRemoved(getAdapterPosition());
                        isRepoChanged = true;
                    }
                });
            }
        }
    }
}
