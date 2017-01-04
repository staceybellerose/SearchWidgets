package com.staceybellerose.simplewidgets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import com.staceybellerose.simplewidgets.providers.SearchWidgetProvider;
import com.staceybellerose.simplewidgets.utils.Constants;

import java.util.List;

public class WidgetConfigureActivity extends FragmentActivity
        implements AlertDialogFragment.OnDismissListener {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private RadioButton buttonDark;
    private CheckBox checkBoxBackground;
    private CheckBox checkSearch;
    private CheckBox checkVoice;
    private CheckBox checkSmall;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        setResult(RESULT_CANCELED);

        buttonDark = (RadioButton) findViewById(R.id.radio_dark);
        checkBoxBackground = (CheckBox) findViewById(R.id.checkbox_background);
        checkSearch = (CheckBox) findViewById(R.id.include_search);
        checkVoice = (CheckBox) findViewById(R.id.include_voice);
        checkSmall = (CheckBox) findViewById(R.id.checkbox_small);
        final TextView backgroundHelp = (TextView) findViewById(R.id.background_help);
        final TextView smallHelp = (TextView) findViewById(R.id.small_help);
        Button saveButton = (Button) findViewById(R.id.save_button);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        sPref = PreferenceManager.getDefaultSharedPreferences(this);

        checkBoxBackground.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    backgroundHelp.setText(R.string.settings_background_help_on);
                } else {
                    backgroundHelp.setText(R.string.settings_background_help_off);
                }
            }
        });

        checkSearch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    smallHelp.setText(R.string.settings_small_help_on);
                } else {
                    smallHelp.setText(R.string.settings_small_help_off);
                }
            }
        });

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                configureWidget();
            }
        });
    }

    public void configureWidget() {
        // Read configuration settings
        boolean darkTheme = buttonDark.isChecked();
        boolean includeBackground = checkBoxBackground.isChecked();
        boolean includeSearch = checkSearch.isChecked();
        boolean includeVoice = checkVoice.isChecked();
        boolean isSmall;

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check for valid intents
        boolean isSearchAvailable = isIntentAvailable(SearchWidgetProvider.GLOBAL_SEARCH);
        boolean isVoiceAvailable = isIntentAvailable(SearchWidgetProvider.VOICE_SEARCH);
        if (includeSearch && !isSearchAvailable) {
            AlertDialogFragment fragment = AlertDialogFragment.newInstance(Constants.DIALOG_NO_SEARCH_APP);
            fragment.show(fragmentManager);
            return;
        }
        if (includeVoice && !isVoiceAvailable) {
            AlertDialogFragment fragment = AlertDialogFragment.newInstance(Constants.DIALOG_NO_VOICE_APP);
            fragment.show(fragmentManager);
            return;
        }

        // Configure widget
        int layout;
        if (includeSearch && includeVoice) {
            layout = R.layout.widget_searchvoice;
            isSmall = checkSmall.isChecked();
        } else if (includeSearch) {
            layout = R.layout.widget_search;
            isSmall = false;
        } else if (includeVoice) {
            layout = R.layout.widget_voice;
            isSmall = false;
        } else {
            AlertDialogFragment fragment = AlertDialogFragment.newInstance(Constants.DIALOG_NO_OPTIONS_SELECTED);
            fragment.show(fragmentManager);
            return;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), layout);
        if (includeSearch) {
            remoteViews.setImageViewResource(R.id.btn_search,
                    SearchWidgetProvider.getSearchImage(darkTheme, isSmall));
            remoteViews.setOnClickPendingIntent(R.id.btn_search,
                    SearchWidgetProvider.getSearchIntent(this));
        }
        if (includeVoice) {
            remoteViews.setImageViewResource(R.id.btn_voice,
                    SearchWidgetProvider.getVoiceImage(darkTheme, isSmall));
            remoteViews.setOnClickPendingIntent(R.id.btn_voice,
                    SearchWidgetProvider.getVoiceIntent(this));
        }
        if (includeBackground) {
            if (darkTheme) {
                remoteViews.setViewVisibility(R.id.background_dark, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.background_light, View.INVISIBLE);
            } else {
                remoteViews.setViewVisibility(R.id.background_dark, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.background_light, View.VISIBLE);
            }
        } else {
            remoteViews.setViewVisibility(R.id.background_dark, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.background_light, View.INVISIBLE);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        // Store preferences for widget
        Editor editor = sPref.edit();
        editor.putBoolean(SearchWidgetProvider.WIDGET + SearchWidgetProvider.THEME + appWidgetId,
                darkTheme);
        editor.putBoolean(SearchWidgetProvider.WIDGET + SearchWidgetProvider.BACKGROUND + appWidgetId,
                includeBackground);
        editor.putBoolean(SearchWidgetProvider.WIDGET + SearchWidgetProvider.INCLUDE_SEARCH + appWidgetId,
                includeSearch);
        editor.putBoolean(SearchWidgetProvider.WIDGET + SearchWidgetProvider.INCLUDE_VOICE + appWidgetId,
                includeVoice);
        editor.putBoolean(SearchWidgetProvider.WIDGET + SearchWidgetProvider.SMALL + appWidgetId,
                isSmall);
        editor.commit();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public void onAlertFragmentDismissed() {
        finish();
    }

    public boolean isIntentAvailable(String action) {
        final PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
