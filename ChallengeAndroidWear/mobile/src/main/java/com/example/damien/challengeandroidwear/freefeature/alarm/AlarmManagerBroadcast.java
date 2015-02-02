package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.RemoteViews;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.AlarmWidgetProvider;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmManagerBroadcast extends BroadcastReceiver {

    private static final String TAG = AlarmManagerBroadcast.class.getSimpleName();

    public static void setAlarms(Context context) {
        cancelAlarms(context);

        AlarmDataBase db = new AlarmDataBase(context);

        ArrayList<AlarmObject> alarms = db.getAlarms();

        if (alarms == null) {
            return;
        }
        for (AlarmObject alarm : alarms) {
            if (alarm.isEnable()) {
                PendingIntent pendingIntent = createPendingIntent(context, alarm);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
                calendar.set(Calendar.MINUTE, alarm.getMinute());
                calendar.set(Calendar.SECOND, 0);

                //Find next time to set
                final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
                boolean alarmSet = false;


                //First check if it's later in the week
                for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                    if (dayOfWeek >= nowDay && !(dayOfWeek == nowDay && alarm.getHourOfDay() < nowHour) &&
                            !(dayOfWeek == nowDay && alarm.getHourOfDay() == nowHour && alarm.getMinute() <= nowMinute)) {
                        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                        setAlarm(context, calendar, pendingIntent);
                        alarmSet = true;
                        break;
                    }
                }

                //Else check if it's earlier in the week
                if (!alarmSet) {
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                        if (dayOfWeek <= nowDay) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            calendar.add(Calendar.WEEK_OF_YEAR, 1);

                            setAlarm(context, calendar, pendingIntent);
                            alarmSet = true;
                            break;
                        }
                    }
                }

            }
        }
    }

    private static void setAlarm(Context context, Calendar calendar, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarms(Context context) {
        AlarmDataBase alarmDB = new AlarmDataBase(context);

        ArrayList<AlarmObject> alarmsList = alarmDB.getAlarms();
        if (alarmsList != null) {
            for (AlarmObject alarm : alarmsList) {
                if (alarm.isEnable()) {
                    PendingIntent pendingIntent = createPendingIntent(context, alarm);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    private static PendingIntent createPendingIntent(Context context, AlarmObject alarm) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("id", alarm.getId());
        intent.putExtra("description", alarm.getDescription());
        intent.putExtra("hour", alarm.getHourOfDay());
        intent.putExtra("minute", alarm.getMinute());
        return PendingIntent.getService(context, (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_widget_layout);
        Format format = new SimpleDateFormat("kk:mm");
        String time = format.format(new Date());
        remoteViews.setTextViewText(R.id.time_text_view, time);

        AlarmDataBase alarmDB = new AlarmDataBase(context);
        ArrayList<AlarmObject> alarmsList = alarmDB.getAlarms();
        if (alarmsList == null){
            return;
        }
        if (alarmsList.size() >= 2){
            remoteViews.setTextViewText(R.id.alarm1_text_view, alarmsList.get(0).toString());
            remoteViews.setTextViewText(R.id.alarm2_text_view, alarmsList.get(1).toString());
        } else if (alarmsList.size() == 1) {
            remoteViews.setTextViewText(R.id.alarm1_text_view, alarmsList.get(0).toString());
            remoteViews.setTextViewText(R.id.alarm2_text_view, "");
        } else {
            remoteViews.setTextViewText(R.id.alarm1_text_view, "");
            remoteViews.setTextViewText(R.id.alarm2_text_view, "");
        }

        ComponentName widget = new ComponentName(context, AlarmWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(widget, remoteViews);
        //Release the lock
        wakeLock.release();

    }

}
