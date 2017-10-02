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

/**
 * Class to implement the Search Widget
 */
@SuppressWarnings("InlinedApi")
public class SearchWidgetProvider extends AppWidgetProvider {
    /**
     * Shared Preference key prefix for widget settings
     */
    public static final String WIDGET = "widget_";
    /**
     * Shared Preference key prefix for widget theme setting
     */
    public static final String THEME = "theme_";
    /**
     * Shared Preference key prefix for widget background setting
     */
    public static final String BACKGROUND = "background_";
    /**
     * Shared Preference key prefix for widget Global Search setting
     */
    public static final String INCLUDE_SEARCH = "include_search_";
    /**
     * Shared Preference key prefix for widget Voice Search setting
     */
    public static final String INCLUDE_VOICE = "include_voice_";
    /**
     * Shared Preference key prefix for widget Small Icon setting
     */
    public static final String SMALL = "small_";
    /**
     * Action to activate Global Search
     */
    public static final String GLOBAL_SEARCH = android.app.SearchManager.INTENT_ACTION_GLOBAL_SEARCH;
    /**
     * Action to activate Voice Search
     */
    public static final String VOICE_SEARCH = android.speech.RecognizerIntent.ACTION_WEB_SEARCH;

    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences.Editor editor = sPref.edit();
            editor.remove(WIDGET + THEME + appWidgetId);
            editor.remove(WIDGET + BACKGROUND + appWidgetId);
            editor.remove(WIDGET + INCLUDE_SEARCH + appWidgetId);
            editor.remove(WIDGET + INCLUDE_VOICE + appWidgetId);
            editor.remove(WIDGET + SMALL + appWidgetId);
            editor.commit();
        }
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
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

    /**
     * Get a PendingIntent for Global Search
     *
     * @param context The Context
     * @return a PendingIntent to launch Global Search
     */
    public static PendingIntent getSearchIntent(final Context context) {
        Intent intent = new Intent(GLOBAL_SEARCH);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    /**
     * Get a PendingIntent for Voice Search
     *
     * @param context The Context
     * @return a PendingIntent to launch Voice Search
     */
    public static PendingIntent getVoiceIntent(final Context context) {
        Intent intent = new Intent(VOICE_SEARCH);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    /**
     * Get the Search image based on widget configuration
     *
     * @param darkTheme Flag indicating whether to use the Dark theme
     * @param small Flag indicating whether to use Small icons
     * @return Drawable Resource ID of the appropriate Search image
     */
    public static int getSearchImage(final boolean darkTheme, final boolean small) {
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

    /**
     * Get the Voice Search image based of widget configuration
     *
     * @param darkTheme Flag indicating whether to use the Dark theme
     * @param small Flag indicating whether to use Small icons
     * @return Drawable Resource ID of the appropriate Search image
     */
    public static int getVoiceImage(final boolean darkTheme, final boolean small) {
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