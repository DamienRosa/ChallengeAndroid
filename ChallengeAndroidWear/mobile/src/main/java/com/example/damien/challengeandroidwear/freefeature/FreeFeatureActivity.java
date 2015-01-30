package com.example.damien.challengeandroidwear.freefeature;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmDataBase;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmListAdapter;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmManagerBroadcast;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmObject;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class FreeFeatureActivity extends Activity {

    private static final String TAG = FreeFeatureActivity.class.getSimpleName();

    private TwitterLoginButton mTwitterLoginButton;
    private Button mAddAlarmButton;
    private ListView mAlarmListView;
    private AlarmListAdapter mAlarmListAdapter;
    private AlarmDataBase alarmDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(FreeFeatureConstants.TWITTER_KEY, FreeFeatureConstants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_free_feature);

        alarmDataBase = new AlarmDataBase(this);

        mTwitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        mTwitterLoginButton.setCallback(new CallbackTwitter());

        mAddAlarmButton = (Button) findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(new OnClickNewAlarm());

        mAlarmListView = (ListView) findViewById(R.id.alarms_list_view);
        mAlarmListAdapter = new AlarmListAdapter(this, alarmDataBase.getAlarms());
        mAlarmListView.setAdapter(mAlarmListAdapter);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FreeFeatureConstants.REQUEST_ALARM && data != null) {
            AlarmManagerBroadcast.cancelAlarms(this);

            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            String desc = data.getStringExtra("description");
            AlarmObject alarm = new AlarmObject(hour, minute, desc, true);
            alarmDataBase.createAlarm(alarm);
            mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
            mAlarmListAdapter.notifyDataSetChanged();

            AlarmManagerBroadcast.setAlarms(this);

        } else if (requestCode == FreeFeatureConstants.REQUEST_CANCEL) {
            //nothing
        } else {
            mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }

    }

    //delete alarm
    public void deleteAlarm(long l) {

        AlarmManagerBroadcast.cancelAlarms(this);

        alarmDataBase.deleteAlarm(l);
        mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
        mAlarmListAdapter.notifyDataSetChanged();

        if (mAlarmListAdapter.getCount() != 0)
            AlarmManagerBroadcast.setAlarms(this);

        Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_LONG).show();
    }

    //set enable
    public void setAlarmEnabled(long l, boolean isChecked) {

        AlarmManagerBroadcast.cancelAlarms(this);

        AlarmObject obj = alarmDataBase.getAlarm(l);
        obj.setEnable(isChecked);
        alarmDataBase.updateAlarm(obj);

        AlarmManagerBroadcast.setAlarms(this);
    }

    private class CallbackTwitter extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> twitterSessionResult) {
            Log.d(TAG, twitterSessionResult.data.getUserName());

            mTwitterLoginButton.setVisibility(View.INVISIBLE);
            mAddAlarmButton.setVisibility(View.VISIBLE);
            mAlarmListView.setVisibility(View.VISIBLE);

        }

        @Override
        public void failure(TwitterException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private class OnClickNewAlarm implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent addAlarmIntent = new Intent(FreeFeatureActivity.this, AddAlarmActivity.class);
            startActivityForResult(addAlarmIntent, FreeFeatureConstants.REQUEST_ALARM);
        }
    }
}
