package com.staceybellerose.simplewidgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.staceybellerose.simplewidgets.fragments.AlertDialogFragment;
import com.staceybellerose.simplewidgets.providers.WidgetConfigurator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to configure a Home Screen Widget
 */
public class WidgetConfigureActivity extends AppCompatActivity
        implements AlertDialogFragment.OnDismissListener, OnCheckedChangeListener, OnClickListener {

    /**
     * Radio Button indicating Light or Dark Theme
     */
    @BindView(R.id.radio_dark)
    RadioButton mButtonDark;
    /**
     * Checkbox indicating whether to display a shaded background
     */
    @BindView(R.id.checkbox_background)
    CheckBox mCheckBoxBackground;
    /**
     * Checkbox indicating whether to display the General Search icon
     */
    @BindView(R.id.include_search)
    CheckBox mCheckSearch;
    /**
     * Checkbox indicating whether to display the Voice Search icon
     */
    @BindView(R.id.include_voice)
    CheckBox mCheckVoice;
    /**
     * Checkbox indicating whether to use small icons
     */
    @BindView(R.id.checkbox_small)
    CheckBox mCheckSmall;
    /**
     * The toolbar
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    /**
     * The save button
     */
    @BindView(R.id.save_button)
    Button mSaveButton;
    /**
     * Help text for background checkbox
     */
    @BindView(R.id.background_help)
    TextView mBackgroundHelp;
    /**
     * Help text for small icons checkbox
     */
    @BindView(R.id.small_help)
    TextView mSmallHelp;
    /**
     * The Widget ID to be configured
     */
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        setResult(RESULT_CANCELED);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getWidgetId();
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        mCheckBoxBackground.setOnCheckedChangeListener(this);
        mCheckSmall.setOnCheckedChangeListener(this);
        mCheckVoice.setOnCheckedChangeListener(this);
        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (buttonView == mCheckSmall) {
            if (isChecked) {
                mSmallHelp.setText(R.string.settings_small_help_on);
            } else {
                mSmallHelp.setText(R.string.settings_small_help_off);
            }
        } else if (buttonView == mCheckBoxBackground) {
            if (isChecked) {
                mBackgroundHelp.setText(R.string.settings_background_help_on);
            } else {
                mBackgroundHelp.setText(R.string.settings_background_help_off);
            }
        }
    }

    @Override
    public void onClick(final View view) {
        if (view == mSaveButton) {
            if (validateInput()) {
                storeWidgetConfiguration();
                configureWidget();
            }
        }
    }

    /**
     * Get the widget ID from the calling intent
     */
    private void getWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        } else {
            mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        }
    }

    /**
     * Validate the input to make sure the widget is displayable
     *
     * @return Flag indicating whether the input is valid
     */
    private boolean validateInput() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check for valid intents
        boolean isSearchAvailable = isIntentAvailable(WidgetConfigurator.GLOBAL_SEARCH);
        boolean isVoiceAvailable = isIntentAvailable(WidgetConfigurator.VOICE_SEARCH);
        if (mCheckSearch.isChecked() && !isSearchAvailable) {
            AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_NO_SEARCH_APP).show(fragmentManager);
        } else if (mCheckVoice.isChecked() && !isVoiceAvailable) {
            AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_NO_VOICE_APP).show(fragmentManager);
        } else if (!mCheckSearch.isChecked() && !mCheckVoice.isChecked()) {
            AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_NO_OPTIONS_SELECTED).show(fragmentManager);
        } else {
            return true;
        }
        return false;
    }

    /**
     * Store the preferences for this widget
     */
    @SuppressLint("ApplySharedPref")
    private void storeWidgetConfiguration() {
        // Store preferences for widget
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        boolean isSmall = mCheckSmall.isChecked() && mCheckSearch.isChecked() && mCheckVoice.isChecked();
        WidgetConfigurator.Preferences.putThemeFlag(editor, mAppWidgetId, mButtonDark.isChecked());
        WidgetConfigurator.Preferences.putBackgroundFlag(editor, mAppWidgetId, mCheckBoxBackground.isChecked());
        WidgetConfigurator.Preferences.putSearchFlag(editor, mAppWidgetId, mCheckSearch.isChecked());
        WidgetConfigurator.Preferences.putVoiceFlag(editor, mAppWidgetId, mCheckVoice.isChecked());
        WidgetConfigurator.Preferences.putSmallFlag(editor, mAppWidgetId, isSmall);
        editor.commit();
    }

    /**
     * Configure the widget based on the settings selected
     */
    public void configureWidget() {
        RemoteViews remoteViews = WidgetConfigurator.configureWidget(this, mAppWidgetId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    /**
     * Close the Activity when the AlertDialogFragment is dismissed
     *
     * @param dialogType the dialog type that was displayed
     */
    public void onAlertFragmentDismissed(final int dialogType) {
        if (dialogType != AlertDialogFragment.DIALOG_NO_OPTIONS_SELECTED) {
            finish();
        }
    }

    /**
     * Check to see if an Intent is available for a given Action
     *
     * @param action The Action to check
     * @return whether an Intent is available for the Action provided
     */
    public boolean isIntentAvailable(final String action) {
        final PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
