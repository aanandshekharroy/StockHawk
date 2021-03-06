package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

//import com.example.sam_chordas.stockhawk.R;

/**
 * Implementation of App Widget functionality.
 */
public class StocksWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stocks_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setRemoteAdapter(R.id.list_stocks,new Intent(context,StockWidgetService.class));
        Intent launchIntent=new Intent(context, MyStocksActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,launchIntent,0);
        views.setOnClickPendingIntent(R.id.widget,pendingIntent);

        // Instruct the com.sam_chordas.android.stockhawk.widget manager to update the com.sam_chordas.android.stockhawk.widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
//            RemoteViews remoteViews=new RemoteViews(getP)
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first com.sam_chordas.android.stockhawk.widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last com.sam_chordas.android.stockhawk.widget is disabled
    }
}

