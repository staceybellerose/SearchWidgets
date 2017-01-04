package com.staceybellerose.simplewidgets.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.staceybellerose.simplewidgets.R;

@SuppressWarnings("InlinedApi")
public class SearchWidgetProvider extends AppWidgetProvider {
    public static final String WIDGET = "widget_";
    public static final String THEME = "theme_";
    public static final String BACKGROUND = "background_";
    public static final String INCLUDE_SEARCH = "include_search_";
    public static final String INCLUDE_VOICE = "include_voice_";
    public static final String SMALL = "small_";
    public static final String GLOBAL_SEARCH = android.app.SearchManager.INTENT_ACTION_GLOBAL_SEARCH;
    public static final String VOICE_SEARCH = android.speech.RecognizerIntent.ACTION_WEB_SEARCH;

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        for (int appWidgetId : appWidgetIds) {
            sPref.edit().remove(WIDGET + THEME + appWidgetId).commit();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        for (int appWidgetId : appWidgetIds) {
            boolean darkTheme = sPref.getBoolean(WIDGET + THEME + appWidgetId, true);
            boolean includeBackground = sPref.getBoolean(WIDGET + BACKGROUND + appWidgetId, false);
            boolean includeSearch = sPref.getBoolean(WIDGET + INCLUDE_SEARCH + appWidgetId, false);
            boolean includeVoice = sPref.getBoolean(WIDGET + INCLUDE_VOICE + appWidgetId, false);
            boolean isSmall;

            int layout;
            if (includeSearch && includeVoice) {
                layout = R.layout.widget_searchvoice;
                isSmall = sPref.getBoolean(WIDGET + SMALL + appWidgetId, false);
            } else if (includeVoice) {
                layout = R.layout.widget_voice;
                isSmall = false;
            } else {
                layout = R.layout.widget_search;
                isSmall = false;
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layout);

            if (includeSearch) {
                remoteViews.setOnClickPendingIntent(R.id.btn_search, getSearchIntent(context));
                remoteViews.setImageViewResource(R.id.btn_search, getSearchImage(darkTheme, isSmall));
            }
            if (includeVoice) {
                remoteViews.setOnClickPendingIntent(R.id.btn_voice, getVoiceIntent(context));
                remoteViews.setImageViewResource(R.id.btn_voice, getVoiceImage(darkTheme, isSmall));
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
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    public static PendingIntent getSearchIntent(Context context) {
        Intent intent = new Intent(GLOBAL_SEARCH);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public static PendingIntent getVoiceIntent(Context context) {
        Intent intent = new Intent(VOICE_SEARCH);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public static int getSearchImage(boolean darkTheme, boolean small) {
        if (small) {
            return darkTheme
                    ? R.drawable.ic_btn_search_small
                    : R.drawable.ic_btn_search_small_light;
        } else {
            return darkTheme
                    ? R.drawable.ic_btn_search
                    : R.drawable.ic_btn_search_light;
        }
    }

    public static int getVoiceImage(boolean darkTheme, boolean small) {
        if (small) {
            return darkTheme
                    ? R.drawable.ic_btn_speak_now_small
                    : R.drawable.ic_btn_speak_now_small_light;
        } else {
            return darkTheme
                    ? R.drawable.ic_btn_speak_now
                    : R.drawable.ic_btn_speak_now_light;
        }
    }
}