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
import com.staceybellerose.simplewidgets.utils.Constants;

public class AlertDialogFragment extends DialogFragment {
    OnDismissListener listener;

    public interface OnDismissListener {
        void onAlertFragmentDismissed();
    }

    public AlertDialogFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDismissListener) {
            listener = (OnDismissListener) activity;
        } else {
            throw new RuntimeException("OnDismissListener not implemented in calling activity");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt("dialog_type");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (id) {
            case Constants.DIALOG_NO_SEARCH_APP:
                builder.setTitle(R.string.dialog_search_title)
                        .setMessage(R.string.dialog_search_message)
                        .setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
            case Constants.DIALOG_NO_VOICE_APP:
                builder.setTitle(R.string.dialog_voice_title)
                        .setMessage(R.string.dialog_voice_message)
                        .setPositiveButton(R.string.dialog_voice_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
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
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
            case Constants.DIALOG_NO_OPTIONS_SELECTED:
                builder.setTitle(R.string.dialog_nothing_selected_title)
                        .setMessage(R.string.dialog_nothing_selected_message)
                        .setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismiss();
                                    }
                                });
                return builder.create();
        }
        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onAlertFragmentDismissed();
    }

    public void show(FragmentManager fm) {
        show(fm, "Alert Dialog");
    }

    public static AlertDialogFragment newInstance(int type) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_type", type);
        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }
}
