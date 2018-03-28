package info.ivicel.github.githubtrends.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import info.ivicel.github.githubtrends.R;


public class DiscardConfirmDialog extends DialogFragment {
    private static final String TAG = "DiscardConfirmDialog";
    private static final String ARGUMENT_TITLE = "arg_title";
    private static final String ARGUMENT_MESSAGE = "arg_message";

    private DialogInterface.OnClickListener mOnClickListener;
    
    private String title;
    private String message;
    
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getArguments();
        if (args != null) {
            title = getContext().getString(args.getInt(ARGUMENT_TITLE));
            message = getContext().getString(args.getInt(ARGUMENT_MESSAGE));
        }
    }
    
    public static DiscardConfirmDialog newInstance(@StringRes int title, @StringRes int message) {
        DiscardConfirmDialog dialog = new DiscardConfirmDialog();
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_TITLE, title);
        args.putInt(ARGUMENT_MESSAGE, message);
        dialog.setArguments(args);
        
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.save, mOnClickListener)
                .setNegativeButton(R.string.discard, mOnClickListener)
                .create();
    }
    
    public void setOnConfirmClickListener(DialogInterface.OnClickListener listener) {
        mOnClickListener = listener;
    }
}
