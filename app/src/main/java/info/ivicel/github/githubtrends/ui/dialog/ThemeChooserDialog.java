package info.ivicel.github.githubtrends.ui.dialog;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.AppTheme;
import info.ivicel.github.githubtrends.util.SharePrefHelper;

public class ThemeChooserDialog extends DialogFragment {
    
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.simple_recycler_view, container, false);
        
        RecyclerView rv = v.findViewById(R.id.simple_recycler_view);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv.setAdapter(new ThemeAdapter());

        return v;
    }
    
    private Bitmap getCacheBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        final Bitmap drawingCache = v.getDrawingCache();
        Bitmap bitmap = null;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            v.setDrawingCacheEnabled(false);
        }
        
        return bitmap;
    }
    
    private void showAnimation(final View decorView) {
        Bitmap cacheView = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheView != null) {
            final View view = new View(getContext());
            view.setBackground(new BitmapDrawable(getResources(), cacheView));
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup)decorView).addView(view, lp);
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
            animator.setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ViewGroup)decorView).removeView(view);
                }
            });
            animator.start();
        }
    }
    
    private class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {
    
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.theme_item, parent, false);
            
            return new ViewHolder(v);
        }
    
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder,
                int position) {
            holder.bindViewHolder(AppTheme.values()[position]);
        }
    
        @Override
        public int getItemCount() {
            return AppTheme.values().length;
        }
    
        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTextView;
    
            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView)itemView;

            }
    
            public void bindViewHolder(final AppTheme appTheme) {
                Resources.Theme theme = getResources().newTheme();
                theme.applyStyle(appTheme.v(), true);
                TypedArray typedArray = theme.obtainStyledAttributes(new int[]{R.attr.colorPrimary});
                int color = typedArray.getColor(0, Color.WHITE);
                typedArray.recycle();
                
                mTextView.setBackgroundColor(color);
                mTextView.setText(appTheme.name());
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeTheme(appTheme.v());
                    }
                });
            }
        }
    }
    
    private void changeTheme(int theme) {
        Activity activity = getActivity();
        if (theme == SharePrefHelper.getTheme(getContext()) || activity == null) {
            return;
        }
    
        SharePrefHelper.saveTheme(getContext(), theme);
        activity.setTheme(theme);
        final View decorView = activity.getWindow().getDecorView();
        showAnimation(decorView);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(
                new Intent(Constants.ACTION_THEME_CHANGED));
        dismiss();
    }
}
