package com.example.damien.challengeandroidwear.freefeature;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class FreeFeatureActivity extends Fragment implements AlarmListAdapter.AdapterInterface {

    private static final String TAG = FreeFeatureActivity.class.getSimpleName();
    TwitterAuthClient authClient;
    private TwitterLoginButton mTwitterLoginButton;
    private Button mAddAlarmButton;
    private ListView mAlarmListView;
    private AlarmListAdapter mAlarmListAdapter;
    private AlarmDataBase alarmDataBase;


    public FreeFeatureActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TwitterAuthConfig authConfig = new TwitterAuthConfig(FreeFeatureConstants.TWITTER_KEY, FreeFeatureConstants.TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));

        View rootView = inflater.inflate(R.layout.activity_free_feature, container, false);
        String text = String.format("Free Feature");
        getActivity().setTitle(text);

        alarmDataBase = new AlarmDataBase(getActivity());
/*
        mTwitterLoginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
        mTwitterLoginButton.setCallback(new CallbackTwitter());
*/
        mAddAlarmButton = (Button) rootView.findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(new OnClickNewAlarm());

        mAlarmListView = (ListView) rootView.findViewById(R.id.alarms_list_view);
        mAlarmListAdapter = new AlarmListAdapter(getActivity(), alarmDataBase.getAlarms(), this);
        mAlarmListView.setAdapter(mAlarmListAdapter);

        //CHANGE
        mAddAlarmButton.setVisibility(View.VISIBLE);
        mAlarmListView.setVisibility(View.VISIBLE);
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FreeFeatureConstants.REQUEST_ALARM && data != null) {
            AlarmManagerBroadcast.cancelAlarms(getActivity());

            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            String desc = data.getStringExtra("description");
            AlarmObject alarm = new AlarmObject(hour, minute, desc, true);
            alarmDataBase.createAlarm(alarm);
            mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
            mAlarmListAdapter.notifyDataSetChanged();

            AlarmManagerBroadcast.setAlarms(getActivity());

        }
    }

    @Override
    public void setAlarmEnabled(long id, boolean isChecked) {
        AlarmManagerBroadcast.cancelAlarms(getActivity());

        AlarmObject obj = alarmDataBase.getAlarm(id);
        obj.setEnable(isChecked);
        alarmDataBase.updateAlarm(obj);

        AlarmManagerBroadcast.setAlarms(getActivity());
    }

    @Override
    public void deleteAlarm(long id) {

        AlarmManagerBroadcast.cancelAlarms(getActivity());

        alarmDataBase.deleteAlarm(id);
        mAlarmListAdapter.setAlarms(alarmDataBase.getAlarms());
        mAlarmListAdapter.notifyDataSetChanged();

        if (mAlarmListAdapter.getCount() != 0)
            AlarmManagerBroadcast.setAlarms(getActivity());

        Toast.makeText(getActivity(), "Alarm Deleted", Toast.LENGTH_LONG).show();
    }


    private class CallbackTwitter extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> twitterSessionResult) {
            TwitterSession session =
                    Twitter.getSessionManager().getActiveSession();
            Log.d(TAG, session.getUserName());
            TwitterAuthToken authToken = session.getAuthToken();
            String token = authToken.token;
            String secret = authToken.secret;

            mTwitterLoginButton.setVisibility(View.INVISIBLE);
            mAddAlarmButton.setVisibility(View.VISIBLE);
            mAlarmListView.setVisibility(View.VISIBLE);
        }

        @Override
        public void failure(TwitterException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class OnClickNewAlarm implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent addAlarmIntent = new Intent(getActivity(), AddAlarmActivity.class);
            startActivityForResult(addAlarmIntent, FreeFeatureConstants.REQUEST_ALARM);
        }
    }
}
