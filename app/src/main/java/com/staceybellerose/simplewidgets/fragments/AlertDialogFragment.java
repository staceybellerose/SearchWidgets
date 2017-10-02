package com.staceybellerose.simplewidgets.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.staceybellerose.simplewidgets.R;

/**
 * Display an error message in a Dialog Fragment
 */
public class AlertDialogFragment extends DialogFragment {
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
     * The Activity including this fragment
     */
    private OnDismissListener mListener;

    /**
     * Empty Constructor
     */
    public AlertDialogFragment() { }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDismissListener) {
            mListener = (OnDismissListener) activity;
        } else {
            throw new RuntimeException("OnDismissListener not implemented in calling activity");
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        int dialogType = getArguments().getInt("dialog_type");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (dialogType) {
            case DIALOG_NO_SEARCH_APP:
                builder.setTitle(R.string.dialog_search_title)
                        .setMessage(R.string.dialog_search_message)
                        .setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
            case DIALOG_NO_VOICE_APP:
                builder.setTitle(R.string.dialog_voice_title)
                        .setMessage(R.string.dialog_voice_message)
                        .setPositiveButton(R.string.dialog_voice_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri
                                                .parse("market://search?q=voice+search&c=apps"));
                                        startActivity(intent);
                                        dismiss();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
            case DIALOG_NO_OPTIONS_SELECTED:
                builder.setTitle(R.string.dialog_nothing_selected_title)
                        .setMessage(R.string.dialog_nothing_selected_message)
                        .setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
            default:
                return null;
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onAlertFragmentDismissed();
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
    public static AlertDialogFragment newInstance(final int type) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_type", type);
        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }

    /**
     * OnDismissListener is an interface that any Activity adding this fragment must implement
     */
    public interface OnDismissListener {
        /**
         * Notify the Activity that the fragment has been dismissed
         */
        void onAlertFragmentDismissed();
    }
}
