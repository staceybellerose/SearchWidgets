package com.staceybellerose.simplewidgets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.staceybellerose.simplewidgets.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Display an error message in a Dialog Fragment
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIALOG_NO_SEARCH_APP, DIALOG_NO_VOICE_APP, DIALOG_NO_OPTIONS_SELECTED})
    public @interface DialogType { }
    /**
     * Dialog to display if Global Search is not available
     */
    public static final int DIALOG_NO_SEARCH_APP = 1;
    /**
     * Dialog to display if Voice Search is not available
     */
    public static final int DIALOG_NO_VOICE_APP = 2;
    /**
     * Dialog to display if no options were selected during setup
     */
    public static final int DIALOG_NO_OPTIONS_SELECTED = 3;
    /**
     * Bundle key for dialog type
     */
    private static final String TYPE = "dialog_type";

    /**
     * The Activity including this fragment
     */
    private OnDismissListener mListener;
    /**
     * Which dialog type was selected
     */
    @DialogType
    private int mDialogType;

    /**
     * Empty Constructor
     */
    public AlertDialogFragment() { }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnDismissListener) {
            mListener = (OnDismissListener) context;
        } else {
            throw new RuntimeException("OnDismissListener not implemented in calling activity");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Bundle args = getArguments();
        //noinspection ResourceType Safe because we created it in newInstance
        mDialogType = (args != null) ? args.getInt(TYPE, DIALOG_NO_OPTIONS_SELECTED) : DIALOG_NO_OPTIONS_SELECTED;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final int titleId = getTitleId(mDialogType);
        final int messageId = getMessageId(mDialogType);
        @StringRes final int cancelStringId
                = (mDialogType == DIALOG_NO_VOICE_APP) ? android.R.string.cancel : android.R.string.ok;
        builder.setTitle(titleId)
                .setMessage(messageId)
                .setNegativeButton(cancelStringId, this);
        if (mDialogType == DIALOG_NO_VOICE_APP) {
            builder.setPositiveButton(R.string.dialog_voice_ok, this);
        }
        return builder.create();
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=voice+search&c=apps"));
            startActivity(intent);
            dismiss();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            dismiss();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onAlertFragmentDismissed(mDialogType);
    }

    /**
     * Get the title text of the alert dialog based on the dialog type
     *
     * @param type the dialog type
     * @return A string resource ID for the title text
     */
    @StringRes
    private int getTitleId(@DialogType final int type) {
        switch (type) {
            case DIALOG_NO_SEARCH_APP:
                return R.string.dialog_search_title;
            case DIALOG_NO_VOICE_APP:
                return R.string.dialog_voice_title;
            case DIALOG_NO_OPTIONS_SELECTED:
            default:
                return R.string.dialog_nothing_selected_title;
        }
    }

    /**
     * Get the message text of the alert dialog based on the dialog type
     *
     * @param type the dialog type
     * @return A string resource ID for the message text
     */
    @StringRes
    private int getMessageId(@DialogType final int type) {
        switch (type) {
            case DIALOG_NO_SEARCH_APP:
                return R.string.dialog_search_message;
            case DIALOG_NO_VOICE_APP:
                return R.string.dialog_voice_message;
            case DIALOG_NO_OPTIONS_SELECTED:
            default:
                return R.string.dialog_nothing_selected_message;
        }
    }

    /**
     * Show the dialog fragment
     * @param fragmentManager the fragment manager
     */
    public void show(final FragmentManager fragmentManager) {
        show(fragmentManager, "Alert Dialog");
    }

    /**
     * Create a new instance of this fragment
     *
     * @param type one of Constants.DIALOG_NO_SEARCH_APP or Constants.DIALOG_NO_VOICE_APP
     * @return a new instance of this fragment
     */
    public static AlertDialogFragment newInstance(@DialogType final int type) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }

    /**
     * OnDismissListener is an interface that any Activity adding this fragment must implement
     */
    public interface OnDismissListener {
        /**
         * Notify the Activity that the fragment has been dismissed
         *
         * @param dialogType the type of dialog
         */
        void onAlertFragmentDismissed(@DialogType int dialogType);
    }
}
