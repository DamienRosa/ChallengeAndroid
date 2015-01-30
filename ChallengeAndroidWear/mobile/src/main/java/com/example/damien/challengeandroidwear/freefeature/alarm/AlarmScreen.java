package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.app.Activity;
import android.content.Context;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;

public class AlarmScreen extends Activity {

    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    private static final String TAG = AlarmScreen.class.getSimpleName();

    private Button mDismissButton;
    private Button mSnoozeButton;
    private TextView mTime;
    private TextView mDescription;
    private WakeLock mWakeLock;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_alarm_screen);

        mTime = (TextView) findViewById(R.id.time_alarm_text_view);
        mTime.setText(getIntent().getIntExtra("hour", 0) + " : " + getIntent().getIntExtra("minute", 0));

        mDescription = (TextView) findViewById(R.id.description_text_view);
        mDescription.setText(getIntent().getExtras().getString("description"));

        mDismissButton = (Button) findViewById(R.id.dismiss_alarm_button);
        mDismissButton.setOnClickListener(new OnClickDismiss());

        mSnoozeButton = (Button) findViewById(R.id.snooze_alarm_button);
        mSnoozeButton.setOnClickListener(new OnClickSnooze());

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

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
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
            TweetComposer.Builder builder = new TweetComposer.Builder(getApplicationContext())
                    .text("just setting up my Fabric.");
            builder.show();
        }
    }
}