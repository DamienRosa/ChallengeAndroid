package com.example.damien.challengeandroidwear.freefeature;

import android.app.Activity;
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
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmObject;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
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
    private Switch mEnableAlarm;
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
        mTwitterLoginButton.setOnClickListener(new onClickLoginTwitter());

        mAddAlarmButton = (Button) findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(new OnClickNewAlarm());

        mAlarmListView = (ListView) findViewById(R.id.alarms_list_view);
        mAlarmListAdapter = new AlarmListAdapter(this, alarmDataBase.getAlarms());
        mAlarmListView.setAdapter(mAlarmListAdapter);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FreeFeatureConstants.REQUEST_ALARM  && data != null) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            String desc = data.getStringExtra("description");
            AlarmObject alarm = new AlarmObject(hour, minute, desc, false);
            alarmDataBase.createAlarm(alarm);
            mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
            mAlarmListAdapter.notifyDataSetChanged();
        } else if (requestCode == FreeFeatureConstants.REQUEST_CANCEL) {
            //nothing
        } else {
            mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }

    }

    //delete alarm
    public void deleteAlarm(long l) {
        alarmDataBase.deleteAlarm(l);
        mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
        mAlarmListAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_LONG).show();
    }

    //set enable
    public void setAlarmEnabled(long l, boolean isChecked) {
        AlarmObject obj = alarmDataBase.getAlarm(l);
        obj.setEnable(isChecked);
        alarmDataBase.updateAlarm(obj);
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

    private class onClickLoginTwitter implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /*TweetComposer.Builder builder = new TweetComposer.Builder(getApplicationContext())
                    .text("just setting up my Fabric.");
            builder.show();*/
        }
    }

    private class OnClickNewAlarm implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent addAlarmIntent = new Intent(FreeFeatureActivity.this, AddAlarmActivity.class);
            startActivityForResult(addAlarmIntent, FreeFeatureConstants.REQUEST_ALARM);
        }
    }

    private class onCheckAlarm implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
            }
        }
    }
}
