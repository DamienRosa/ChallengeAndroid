package com.example.damien.challengeandroidwear.freefeature;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmDataBase;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmManagerBroadcast;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmWidgetProvider extends AppWidgetProvider {

    private static final String TAG = AlarmManagerBroadcast.class.getSimpleName();

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcast.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        super.onDisabled(context);
    }

    //enable widget
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcast.class);
        AlarmDataBase alarmDB = new AlarmDataBase(context);
        ArrayList<AlarmObject> alarmsList = alarmDB.getAlarms();

        if (alarmsList.size() >= 2) {
            intent.putExtra("alarm1", alarmsList.get(0).toString());
            intent.putExtra("alarm2", alarmsList.get(1).toString());
        } else if (alarmsList.size() == 1) {
            intent.putExtra("alarm1", alarmsList.get(0).toString());
            intent.putExtra("alarm1", "");
        } else {
            intent.putExtra("alarm1", "");
            intent.putExtra("alarm1", "");
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100 * 3, 1000, pendingIntent);
    }

    //update infor for widget
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context,
                AlarmWidgetProvider.class);

        for (int widgetId : appWidgetManager.getAppWidgetIds(thisWidget)) {
            //Get the remote views
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_widget_layout);
            // Set the text with the current time.
            Format format = new SimpleDateFormat("kk:mm");
            String time = format.format(new Date());
            remoteViews.setTextViewText(R.id.time_alarm_text_view, time);

            AlarmDataBase alarmDB = new AlarmDataBase(context);
            ArrayList<AlarmObject> alarmsList = alarmDB.getAlarms();
            if (alarmsList.size() >= 2) {
                remoteViews.setTextViewText(R.id.alarm1_text_view, alarmsList.get(0).toString());
                remoteViews.setTextViewText(R.id.alarm2_text_view, alarmsList.get(1).toString());
            } else if (alarmsList.size() == 1) {
                remoteViews.setTextViewText(R.id.alarm1_text_view, alarmsList.get(0).toString());
                remoteViews.setTextViewText(R.id.alarm2_text_view, "");
            } else {
                remoteViews.setTextViewText(R.id.alarm1_text_view, "");
                remoteViews.setTextViewText(R.id.alarm2_text_view, "");
            }

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
