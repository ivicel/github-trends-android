package info.ivicel.github.githubtrends.ui;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class SimpleItemTouchCallback extends ItemTouchHelper.SimpleCallback {
    private static final long VIBRATION_EFFECT_TIME = 30L;
    private ItemAdapterHelper mAdapterHelper;
    
    public SimpleItemTouchCallback(ItemAdapterHelper adapter) {
        super(ItemTouchHelper.START | ItemTouchHelper.END |
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END);
        mAdapterHelper = adapter;
    }
    
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
    
        final int fromPosition = viewHolder.getAdapterPosition();
        final int toPosition = target.getAdapterPosition();
        return mAdapterHelper.onMove(fromPosition, toPosition);
    }
    
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapterHelper.onSwiped(viewHolder.getAdapterPosition());
    }
    
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            Vibrator vibrator = (Vibrator)viewHolder.itemView.getContext()
                    .getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createOneShot(VIBRATION_EFFECT_TIME,
                            3);
                    vibrator.vibrate(effect);
                } else {
                    vibrator.vibrate(VIBRATION_EFFECT_TIME);
                }
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
    
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }
}
