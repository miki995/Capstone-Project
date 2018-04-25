package com.inc.miki.minimax;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.inc.miki.minimax.Utilities.PlayerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;


public class WidgetProvider extends android.appwidget.AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

        Intent intent = new Intent(context, PlayerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.widgetPlayButton, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (PlayerService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String showTitle = extras.getString("showTitle");
                String show = extras.getString("show");

                int showLogo;
                switch (Objects.requireNonNull(show)) {
                    case "Roll to Hit (5th Ed. Dungeons and Dragons)":
                        showLogo = R.drawable.rth;
                        break;
                    case "The Bearded Vegans":
                        showLogo = R.drawable.vegans;
                        break;
                    case "Sky on Fire: A Star Wars RPG":
                        showLogo = R.drawable.sky;
                        break;
                    case "Epic Nitpick":
                        showLogo = R.drawable.epic;
                        break;
                    case "SkyFyre Comics":
                        showLogo = R.drawable.skyfire;
                        break;
                    case "Loose Endz":
                        showLogo = R.drawable.loose;
                        break;

                    default:
                        showLogo = R.drawable.sky;
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName myappWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(myappWidget);

                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
                    views.setTextViewText(R.id.widgetEpisodeName, showTitle);
                    try {
                        Bitmap showLogoBitmap = Picasso.get().load(showLogo).get();
                        views.setImageViewBitmap(R.id.widgetLogo, showLogoBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views);

                    Intent playButtonIntent = new Intent(PlayerService.ACTION_PAUSE);
                    PendingIntent pendingIntent = PendingIntent.getService(context, 0, playButtonIntent, 0);
                    views.setOnClickPendingIntent(R.id.widgetPlayButton, pendingIntent);

                }
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "Widget has been updated! ", Toast.LENGTH_SHORT).show();
        }
    }
}
