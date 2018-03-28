package info.ivicel.github.githubtrends.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.LinkedList;
import java.util.List;

import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Language;
import info.ivicel.github.githubtrends.ui.dialog.DiscardConfirmDialog;
import info.ivicel.github.githubtrends.util.LangsHelper;

public class AddLanguageActivity extends BasicActivity {
    private List<Language> mLanguages;
    private List<Language> mSelectionLanguages = new LinkedList<>();
    private AddLanguageAdapter mAdapter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);
    
        mLanguages = LangsHelper.getInstance().getUnselectedLangs();
        RecyclerView view = findViewById(R.id.simple_recycler_view);
        mAdapter = new AddLanguageAdapter();
        view.setLayoutManager(new GridLayoutManager(this, 2));
        view.setAdapter(mAdapter);
    
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_language_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    class AddLanguageAdapter extends RecyclerView.Adapter<AddLanguageAdapter.ViewHolder> {
    
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                int viewType) {
            View v = LayoutInflater.from(AddLanguageActivity.this)
                    .inflate(R.layout.add_language_item,
                            parent, false);
            return new ViewHolder(v);
        }
    
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindViewHolder(mLanguages.get(position));
        }
    
        @Override
        public int getItemCount() {
            return mLanguages == null ? 0 : mLanguages.size();
        }
    
        class ViewHolder extends RecyclerView.ViewHolder {
            private CheckBox mCheckBox;
    
            public ViewHolder(View itemView) {
                super(itemView);
                mCheckBox = ((CheckBox)itemView);
            }
    
            public void bindViewHolder(final Language language) {
                final boolean checked = mSelectionLanguages.contains(language);
                mCheckBox.setText(language.getName());
                mCheckBox.setChecked(checked);
                mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCheckBox.isChecked()) {
                            mSelectionLanguages.add(language);
                        } else {
                            mSelectionLanguages.remove(language);
                        }
                    }
                });
            }
        }
    }
    
    public static Intent newIntent(Context context) {
        return new Intent(context, AddLanguageActivity.class);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_language, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
                
            case R.id.menu_add_language_clear:
                clearSelectedLanguages();
                break;
                
            case R.id.menu_add_language_save:
                onSaveSelectedLanguages();
                break;
                
            default:
                return super.onOptionsItemSelected(item);
        }
        
        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (mSelectionLanguages.size() > 0) {
            final DiscardConfirmDialog dialog = DiscardConfirmDialog.newInstance(
                    R.string.discard_title, R.string.discard_messaeg);
            dialog.setOnConfirmClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        onSaveSelectedLanguages();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        AddLanguageActivity.this.finish();
                    }
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
    }
    
    private void clearSelectedLanguages() {
        mAdapter.notifyDataSetChanged();
        mSelectionLanguages.clear();
    }
    
    private void onSaveSelectedLanguages() {
        LangsHelper.getInstance().notifyLanguageInserted(mSelectionLanguages);
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(Constants.ACTION_LANGUAGE_CHANGED));
        
        finish();
    }
}
