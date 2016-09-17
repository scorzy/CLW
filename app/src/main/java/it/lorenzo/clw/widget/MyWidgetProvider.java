package it.lorenzo.clw.widget;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import it.lorenzo.clw.R;
import it.lorenzo.clw.chooser.FileSelect;
import it.lorenzo.clw.core.Core;

import static android.app.PendingIntent.getBroadcast;
import static it.lorenzo.clw.R.id.button1;

//import android.util.Log;

public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        //	Log.i("onUpdate", "update");
        for (int n : appWidgetIds) {
            // get path for config file
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference),
                    Context.MODE_PRIVATE);
            String path = sharedPref.getString("" + n, "");
            Boolean notification = sharedPref.getBoolean("use_notification_key", false);
            if (path.equals("")) {
                // path is not set
                RemoteViews remoteViews = new RemoteViews(
                        context.getPackageName(), R.layout.clickme);
                Intent intent = new Intent(context, FileSelect.class);
                intent.putExtra("appWidgetId", n);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendIntent = PendingIntent.getActivity(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(button1, pendIntent);
                appWidgetManager.updateAppWidget(n, remoteViews);
            } else {
                // path is set
                RemoteViews remoteViews;
                try {
                    remoteViews = new RemoteViews(
                            context.getPackageName(), R.layout.widgetlayout);

                    remoteViews.setImageViewBitmap(R.id.widget_image, Core
                            .getInstance().getImageToSet(context, path));
                    Intent intent = new Intent();
                    intent.setAction("it.lorenzo.clw.intent.action.CHANGE_PICTURE");
                    int ids[] = new int[1];
                    ids[0] = n;
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

                    remoteViews.setOnClickPendingIntent(R.id.widget_image,
                            getBroadcast(context, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT));

                    appWidgetManager.updateAppWidget(n, remoteViews);
                    Log.i("n", "" + n);
                } catch (Exception e) {
                    remoteViews = new RemoteViews(
                            context.getPackageName(), R.layout.clickme);
                    if (notification)
                        sendNotification(context, e.getMessage());
                    remoteViews.setTextViewText(button1, "ERROR: " + e.getMessage() + "\n tap to reload");
                    Log.i("error: ", e.getMessage());
                    Intent intent = new Intent();
                    intent.setAction("it.lorenzo.clw.intent.action.CHANGE_PICTURE");
                    int ids[] = new int[1];
                    ids[0] = n;
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    remoteViews.setOnClickPendingIntent(button1,
                            getBroadcast(context, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT));

                    appWidgetManager.updateAppWidget(n, remoteViews);
                }
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        createAlarm(context);
    }


    public void createAlarm(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference),
                Context.MODE_PRIVATE);
        Boolean update = sharedPref.getBoolean("alarm_key", false);
        int intervall = sharedPref.getInt("intervall_key", 30);
        //	Log.i("alarm created", "" + update);
        //	Log.i("alarm created", "" + intervall);
        if (update) {
            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyWidgetProvider.class);
            intent.setAction("it.lorenzo.clw.intent.action.ALARM");
            PendingIntent pi = getBroadcast(context, 0, intent, 0);
            am.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis() + 1000 * intervall, 1000 * intervall, pi);
            //	Log.i("alarm created", "created");
        }
    }

    public void removeAlarm(Context context) {
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction("it.lorenzo.clw.intent.action.ALARM");
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        PendingIntent sender =
                getBroadcast(context, 0, intent, 0);

        alarmManager.cancel(sender);
        //	Log.i("alarm removed", "removed");

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        removeAlarm(context);
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference), Context.MODE_PRIVATE);
        for (int i : appWidgetIds)
            sharedPref.edit().remove("" + i).apply();
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.getSharedPreferences(context.getString(R.string.preference),
                Context.MODE_PRIVATE).edit().clear().apply();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String act = "" + intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(act)
                || act.equals("it.lorenzo.clw.intent.action.CHANGE_PICTURE")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras
                        .getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context,
                            AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(act)
                || act.equals("it.lorenzo.clw.intent.action.SETTINGS_CHANGED")) {
            removeAlarm(context);
            createAlarm(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(act)
                || act.equals("it.lorenzo.clw.intent.action.ALARM")) {


            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(
                    new ComponentName(context, MyWidgetProvider.class));
            onUpdate(context, AppWidgetManager.getInstance(context), ids);


        }
    }

    private void sendNotification(Context context, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.notification)
                .setContentTitle("CLW ERROR").setContentText(text);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(5, mBuilder.build());
    }
}
