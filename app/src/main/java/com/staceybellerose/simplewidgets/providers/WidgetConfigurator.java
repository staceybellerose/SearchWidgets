package com.staceybellerose.simplewidgets.providers;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.RemoteViews;

import com.staceybellerose.simplewidgets.R;

/**
 * A utility class to configure home screen widgets
 */
public final class WidgetConfigurator {
    /**
     * Action to activate Global Search
     */
    public static final String GLOBAL_SEARCH = android.app.SearchManager.INTENT_ACTION_GLOBAL_SEARCH;
    /**
     * Action to activate Voice Search
     */
    public static final String VOICE_SEARCH = android.speech.RecognizerIntent.ACTION_WEB_SEARCH;
    /**
     * Shared Preference key prefix for widget settings
     */
    private static final String WIDGET = "widget_";
    /**
     * Shared Preference key prefix for widget theme setting
     */
    private static final String THEME = "theme_";
    /**
     * Shared Preference key prefix for widget background setting
     */
    private static final String BACKGROUND = "background_";
    /**
     * Shared Preference key prefix for widget Global Search setting
     */
    private static final String INCLUDE_SEARCH = "include_search_";
    /**
     * Shared Preference key prefix for widget Voice Search setting
     */
    private static final String INCLUDE_VOICE = "include_voice_";
    /**
     * Shared Preference key prefix for widget Small Icon setting
     */
    private static final String SMALL = "small_";

    /**
     * Private constructor
     */
    private WidgetConfigurator() { }

    /**
     * Configure a widget based on its saved configuration.
     *
     * @param context The Context in which this receiver is running
     * @param appWidgetId the ID of the widget to configure
     * @return a RemoteViews view hierarchy
     */
    public static RemoteViews configureWidget(final Context context, final int appWidgetId) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sPref.getBoolean(WIDGET + THEME + appWidgetId, true);
        boolean includeBackground = sPref.getBoolean(WIDGET + BACKGROUND + appWidgetId, false);
        boolean includeSearch = sPref.getBoolean(WIDGET + INCLUDE_SEARCH + appWidgetId, false);
        boolean includeVoice = sPref.getBoolean(WIDGET + INCLUDE_VOICE + appWidgetId, false);
        boolean isSmall = sPref.getBoolean(WIDGET + SMALL + appWidgetId, false);
        int layoutId = getLayoutId(includeSearch, includeVoice);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);
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
        return remoteViews;
    }

    /**
     * Get the appropriate layout ID based on the widget configuration.
     *
     * @param includeSearch Flag to indicate whether to show text search icon
     * @param includeVoice Flag to indicate whether to show voice search icon
     * @return a layout ID
     */
    @LayoutRes
    private static int getLayoutId(final boolean includeSearch, final boolean includeVoice) {
        int layoutId;
        if (includeSearch && includeVoice) {
            layoutId = R.layout.widget_searchvoice;
        } else if (includeSearch) {
            layoutId = R.layout.widget_search;
        } else if (includeVoice) {
            layoutId = R.layout.widget_voice;
        } else {
            layoutId = -1; // invalid option
        }
        return layoutId;
    }

    /**
     * Get a PendingIntent for Global Search
     *
     * @param context The Context in which this receiver is running
     * @return a PendingIntent to launch Global Search
     */
    private static PendingIntent getSearchIntent(final Context context) {
        Intent intent = new Intent(GLOBAL_SEARCH);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    /**
     * Get a PendingIntent for Voice Search
     *
     * @param context The Context in which this receiver is running
     * @return a PendingIntent to launch Voice Search
     */
    private static PendingIntent getVoiceIntent(final Context context) {
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
    private static int getSearchImage(final boolean darkTheme, final boolean small) {
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
    private static int getVoiceImage(final boolean darkTheme, final boolean small) {
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

    /**
     * Widget preference manager
     */
    public static class Preferences {
        /**
         * Delete preferences from a deleted widget.
         *
         * @param context The Context in which this receiver is running
         * @param appWidgetId the ID of the widget being deleted
         */
        public static void delete(final Context context, final int appWidgetId) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sPref.edit();
            editor.remove(WIDGET + THEME + appWidgetId);
            editor.remove(WIDGET + BACKGROUND + appWidgetId);
            editor.remove(WIDGET + INCLUDE_SEARCH + appWidgetId);
            editor.remove(WIDGET + INCLUDE_VOICE + appWidgetId);
            editor.remove(WIDGET + SMALL + appWidgetId);
            editor.apply();
        }

        /**
         * Save preferences for a new widget.
         *
         * @param context The Context in which this receiver is running
         * @param appWidgetId the ID of the new widget
         * @param darkTheme Flag indicating whether the widget uses the dark theme
         * @param includeBackground Flag indicating whether the widget displays a background
         * @param includeSearch Flag indicating whether the widget shows the text search icon
         * @param includeVoice Flag indicating whether the widget shows the voice search icon
         * @param isSmall Flag indicating whether the widget uses small icons
         */
        @SuppressLint("ApplySharedPref")
        public static void save(final Context context, final int appWidgetId, final boolean darkTheme,
                                final boolean includeBackground, final boolean includeSearch,
                                final boolean includeVoice, final boolean isSmall) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sPref.edit();
            editor.putBoolean(WIDGET + THEME + appWidgetId, darkTheme);
            editor.putBoolean(WIDGET + BACKGROUND + appWidgetId, includeBackground);
            editor.putBoolean(WIDGET + INCLUDE_SEARCH + appWidgetId, includeSearch);
            editor.putBoolean(WIDGET + INCLUDE_VOICE + appWidgetId, includeVoice);
            editor.putBoolean(WIDGET + SMALL + appWidgetId, isSmall);
            // Commit is used rather than apply because these preferences may need to be immediately read
            editor.commit();
        }

        /**
         * Restore the preferences to a widget restored from backup.
         *
         * @param context The Context in which this receiver is running
         * @param oldId the widget's old ID
         * @param newId the widget's new ID
         */
        public static void restore(final Context context, final int oldId, final int newId) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean darkTheme = sPref.getBoolean(WIDGET + THEME + oldId, true);
            boolean includeBackground = sPref.getBoolean(WIDGET + BACKGROUND + oldId, false);
            boolean includeSearch = sPref.getBoolean(WIDGET + INCLUDE_SEARCH + oldId, false);
            boolean includeVoice = sPref.getBoolean(WIDGET + INCLUDE_VOICE + oldId, false);
            boolean isSmall = sPref.getBoolean(WIDGET + SMALL + oldId, false);
            save(context, newId, darkTheme, includeBackground, includeSearch, includeVoice, isSmall);
            delete(context, oldId);
        }
    }
}
