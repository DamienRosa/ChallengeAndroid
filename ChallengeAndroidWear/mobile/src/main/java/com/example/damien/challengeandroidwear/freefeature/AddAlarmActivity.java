package com.example.damien.challengeandroidwear.freefeature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmManagerBroadcast;

public class AddAlarmActivity extends Activity {

    private static final String TAG = AddAlarmActivity.class.getSimpleName();

    private TimePicker mTimePiker;
    private EditText mDescriptionEditText;
    private Button mAddButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        mTimePiker = (TimePicker) findViewById(R.id.alarm_time_picker);
        mTimePiker.setIs24HourView(true);
        mDescriptionEditText = (EditText) findViewById(R.id.alarm_description_edit_text);
        mAddButton = (Button) findViewById(R.id.add_alarm_button);
        mAddButton.setOnClickListener(new OnClickAdd());

        mCancelButton = (Button) findViewById(R.id.cancel_alarm_button);
        mCancelButton.setOnClickListener(new onClickCancel());
    }

    private class OnClickAdd implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int hour = mTimePiker.getCurrentHour();
            int minute = mTimePiker.getCurrentMinute();
            String description = mDescriptionEditText.getText().toString();

            Intent result = new Intent();
            result.putExtra("hour", hour);
            result.putExtra("minute", minute);
            result.putExtra("description", description);
            setResult(FreeFeatureConstants.REQUEST_ALARM, result);

            finish();
        }
    }

    private class onClickCancel implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setResult(FreeFeatureConstants.REQUEST_CANCEL);
            finish();
        }
    }
}
