package info.ivicel.github.githubtrends.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import info.ivicel.github.githubtrends.AppApplication;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Contributor;
import info.ivicel.github.githubtrends.model.Repo;
import info.ivicel.github.githubtrends.ui.activity.RepoPageActivity;
import info.ivicel.github.githubtrends.util.FavorHelper;
import info.ivicel.github.githubtrends.helper.ImageLruCache;

public class RepoRecyclerAdapter extends RecyclerView.Adapter<RepoRecyclerAdapter.ViewHolder> {
    private static final String TAG = "RepoRecyclerAdapter";
    
    private List<Repo> mRepos;
    private Context mContext;
    private ImageLoader loader;
    
    public RepoRecyclerAdapter(Context context) {
        mContext = context;
        loader = new ImageLoader(AppApplication.getQueue(),
                new ImageLruCache(mContext));
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                getItemResource(), parent, false);
        
        return new ViewHolder(view);
    }
    
    @LayoutRes
    protected int getItemResource() {
        return R.layout.repo_item;
    }
    
    public void updateRepositories(List<Repo> repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindViewHolder(loader, mRepos.get(position));
    }
    
    @Override
    public int getItemCount() {
        return mRepos == null ? 0 : mRepos.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView mTitleView;
        protected TextView mDescView;
        protected LinearLayout mContributors;
        protected ToggleButton mFavorBtn;
        protected Context mContext;
        
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mTitleView = itemView.findViewById(R.id.repo_name);
            mDescView = itemView.findViewById(R.id.repo_desc);
            mContributors = itemView.findViewById(R.id.contributors);
            mFavorBtn = itemView.findViewById(R.id.repo_favo);
        }
    
        public void bindViewHolder(ImageLoader loader, final Repo repo) {
            final String repoName = repo.owner + "/" + repo.name;
            mTitleView.setText(repoName);
            if (!TextUtils.isEmpty(repo.description)) {
                mDescView.setText(repo.description);
                mDescView.setVisibility(View.VISIBLE);
            }
    
            if (FavorHelper.contains(repo)) {
                mFavorBtn.setBackgroundResource(R.drawable.ic_star);
            } else {
                mFavorBtn.setBackgroundResource(R.drawable.ic_star_black);
            }
            
            mContributors.removeAllViews();
            if (repo.contributors.size() > 0) {
                initContributor(loader, repo.contributors);
            } else {
                itemView.findViewById(R.id.repo_built_by).setVisibility(View.GONE);
            }
            setOnItemClickListener(repo.link);
            setOnFavorClickListener(repo);
        }
        
        protected void setOnItemClickListener(final String url) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = RepoPageActivity.newIntent(itemView.getContext(), url);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    
        protected void setOnFavorClickListener(final Repo repo) {
            mFavorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean reuslt = FavorHelper.toggle(v.getContext(), repo);
                    if (reuslt) {
                        mFavorBtn.setBackgroundResource(R.drawable.ic_star);
                    } else {
                        mFavorBtn.setBackgroundResource(R.drawable.ic_star_black);
                    }
                }
            });
        }
    
        protected void initContributor(ImageLoader loader, List<Contributor> contributors) {
            for (final Contributor contributor : contributors) {
                final ImageView iv = (ImageView)LayoutInflater.from(mContext)
                        .inflate(R.layout.contributor_image, mContributors, false);
    
                loader.get(contributor.avatar, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                            boolean isImmediate) {
                        Bitmap bitmap = response.getBitmap();
                        if (bitmap == null) {
                            return;
                        }
                        iv.setImageBitmap(bitmap);
                        mContributors.addView(iv);
                        iv.setContentDescription(contributor.name);
                    }
        
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                });
            }
        }
    }
}
