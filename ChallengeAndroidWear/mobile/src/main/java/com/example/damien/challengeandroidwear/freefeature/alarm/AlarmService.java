package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.damien.challengeandroidwear.freefeature.AlarmScreenActivity;

public class AlarmService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent alarmIntent = new Intent(getBaseContext(), AlarmScreenActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(intent);
        getApplication().startActivity(alarmIntent);

        AlarmManagerBroadcast.setAlarms(this);

        return super.onStartCommand(intent, flags, startId);
    }
}
