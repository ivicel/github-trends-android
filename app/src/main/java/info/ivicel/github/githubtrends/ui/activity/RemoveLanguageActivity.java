package info.ivicel.github.githubtrends.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import info.ivicel.github.githubtrends.Constants;
import info.ivicel.github.githubtrends.R;
import info.ivicel.github.githubtrends.model.Language;
import info.ivicel.github.githubtrends.ui.ItemAdapterHelper;
import info.ivicel.github.githubtrends.ui.SimpleItemTouchCallback;
import info.ivicel.github.githubtrends.ui.dialog.DiscardConfirmDialog;
import info.ivicel.github.githubtrends.util.LangsHelper;

public class RemoveLanguageActivity extends BasicActivity {
    private static final String TAG = "RemoveLanguageActivity";

    private List<Language> mSelectedLanguages;
    private RemoverAdapter mRemoverAdapter;
    private boolean isLanguageChanged = false;
    private List<Language> mRemoveLanguage;
    
    private ItemAdapterHelper mAdapterHelper = new ItemAdapterHelper() {
        @Override
        public boolean onMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mSelectedLanguages, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mSelectedLanguages, i, i - 1);
                }
            }

            mRemoverAdapter.notifyItemMoved(fromPosition, toPosition);
            if (!isLanguageChanged) {
                isLanguageChanged = true;
                invalidateOptionsMenu();
            }
            
            return true;
        }
    
        @Override
        public void onSwiped(int position) {
            if (mSelectedLanguages.size() == 1) {
                AlertDialog dialog = new AlertDialog.Builder(
                        RemoveLanguageActivity.this)
                        .setMessage(R.string.language_remove_alert)
                        .setPositiveButton(android.R.string.ok, null)
                        .create();
                dialog.show();
                return;
            }
            
            if (!isLanguageChanged) {
                isLanguageChanged = true;
                invalidateOptionsMenu();
            }
            Language language = mSelectedLanguages.remove(position);
            mRemoveLanguage.add(language);
            mRemoverAdapter.notifyItemRemoved(position);
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);
    
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.remove_language_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        mSelectedLanguages = new LinkedList<>(LangsHelper.getInstance().getSelectedLanguages());
        mRemoveLanguage = new ArrayList<>(mSelectedLanguages.size());
        RecyclerView rv = findViewById(R.id.simple_recycler_view);
        mRemoverAdapter = new RemoverAdapter();
        rv.setAdapter(mRemoverAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SimpleItemTouchCallback callback = new SimpleItemTouchCallback(mAdapterHelper);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
        DividerItemDecoration decoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(decoration);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_remove_language_done).setVisible(isLanguageChanged);
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_remove_language, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
                
            case R.id.menu_remove_language_done:
                notifyLanguageChanged();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (isLanguageChanged) {
            DiscardConfirmDialog dialog = DiscardConfirmDialog.newInstance(R.string.discard_title,
                    R.string.discard_messaeg);
            dialog.setOnConfirmClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        notifyLanguageChanged();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        RemoveLanguageActivity.this.finish();
                    }
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
    }
    
    private void notifyLanguageChanged() {
        if (isLanguageChanged) {
            LangsHelper.getInstance().notifyLanguageChanged(mSelectedLanguages);
            LocalBroadcastManager.getInstance(this).sendBroadcast(
                    new Intent(Constants.ACTION_LANGUAGE_CHANGED));
        }
        finish();
    }
    
    public static Intent newIntent(Context context) {
        return new Intent(context, RemoveLanguageActivity.class);
    }
    
    class RemoverAdapter extends RecyclerView.Adapter<RemoverAdapter.ViewHolder> {
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.remove_language_item, parent, false);
            return new ViewHolder(v);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder,
                int position) {
            holder.bindViewHolder(position);
        }
        
        @Override
        public int getItemCount() {
            return mSelectedLanguages.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }
            
            public void bindViewHolder(final int position) {
                final Language language = mSelectedLanguages.get(position);
                ((TextView)itemView).setText(language.getName());
            }
        }
    }
}
