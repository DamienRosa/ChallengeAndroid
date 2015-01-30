package com.example.damien.challengeandroidwear.freefeature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmDataBase;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmManagerBroadcast;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmObject;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;

public class AlarmScreenActivity extends Activity {

    private static final String TAG = AlarmScreenActivity.class.getSimpleName();
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;

    private AlarmDataBase db = new AlarmDataBase(this);
    private Button mDismissButton;
    private Button mSnoozeButton;
    private TextView mTime;
    private TextView mDescription;
    private WakeLock mWakeLock;
    private MediaPlayer mMediaPlayer;

    private int minuteInitial;
    private long idAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_alarm_screen);

        minuteInitial = getIntent().getIntExtra("minute", 0);
        idAlarm = getIntent().getLongExtra("id", 0);

        mTime = (TextView) findViewById(R.id.time_alarm_text_view);
        mTime.setText(getIntent().getIntExtra("hour", 0) + " : " + getIntent().getIntExtra("minute", 0));

        mDescription = (TextView) findViewById(R.id.description_text_view);
        mDescription.setText(getIntent().getExtras().getString("description"));

        mDismissButton = (Button) findViewById(R.id.dismiss_alarm_button);
        mDismissButton.setOnClickListener(new OnClickDismiss());

        mSnoozeButton = (Button) findViewById(R.id.snooze_alarm_button);
        mSnoozeButton.setOnClickListener(new OnClickSnooze());

        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, Uri.parse("android.resource://com.example.damien.challengeandroidwear/" + R.raw.wake_up));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            e.printStackTrace();
            mMediaPlayer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runnable releaseWakelock = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.PARTIAL_WAKE_LOCK, TAG);
        }
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private class OnClickDismiss implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mMediaPlayer.stop();
            finish();
        }
    }

    private class OnClickSnooze implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            AlarmManagerBroadcast.cancelAlarms(getBaseContext());

            AlarmObject obj = db.getAlarm(idAlarm);
            int minute = minuteInitial + 1;
            obj.setMinute(minute);
            db.updateAlarm(obj);

            AlarmManagerBroadcast.setAlarms(getBaseContext());
            /*
            Intent intent = new TweetComposer.Builder(getApplicationContext())
                    .text("I'm lazy piece of cake!").createIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            mMediaPlayer.stop();
            finish();
        }
    }
}