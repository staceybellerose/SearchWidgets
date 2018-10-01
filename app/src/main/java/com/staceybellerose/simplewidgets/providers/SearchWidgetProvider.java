package com.staceybellerose.simplewidgets.providers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Class to implement the Search Widget
 */
public class SearchWidgetProvider extends AppWidgetProvider {

    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigurator.Preferences.delete(context, appWidgetId);
        }
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = WidgetConfigurator.configureWidget(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onRestored(final Context context, final int[] oldWidgetIds, final int[] newWidgetIds) {
        for (int i = 0; i < oldWidgetIds.length; i++) {
            int oldId = oldWidgetIds[i];
            int newId = newWidgetIds[i];
            WidgetConfigurator.Preferences.restore(context, oldId, newId);
        }
    }
}
