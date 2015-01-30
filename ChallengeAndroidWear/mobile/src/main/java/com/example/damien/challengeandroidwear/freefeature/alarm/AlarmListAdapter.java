package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.freefeature.FreeFeatureActivity;

import java.util.ArrayList;

public class AlarmListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AlarmObject> listAlarmObj;

    public AlarmListAdapter(Context context, ArrayList<AlarmObject> list) {
        this.context = context;
        this.listAlarmObj = list;
    }

    @Override
    public int getCount() {
        if (listAlarmObj != null) {
            return listAlarmObj.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return listAlarmObj.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.alarm_obj_item, parent, false);
        }

        AlarmObject obj = (AlarmObject) getItem(position);

        TextView time = (TextView) convertView.findViewById(R.id.time_text_view);
        time.setText(obj.getHourOfDay() + ":" + obj.getMinute());
        TextView description = (TextView) convertView.findViewById(R.id.description_text_view);
        description.setText(obj.getDescription());

        Switch switchAlarm = (Switch) convertView.findViewById(R.id.alarm_enable_switch);
        switchAlarm.setChecked(obj.isEnable());
        switchAlarm.setTag(obj.getId());
        switchAlarm.setOnCheckedChangeListener(new onCheckSwitch());

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete_alarm);
        delete.setTag(obj.getId());
        delete.setOnClickListener(new OnDeleteClick());

        return convertView;
    }

    public void setAlarms(ArrayList<AlarmObject> alarms) {
        this.listAlarmObj = alarms;
    }

    private class onCheckSwitch implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ((FreeFeatureActivity) context).setAlarmEnabled((Long) buttonView.getTag(), isChecked);
        }
    }

    private class OnDeleteClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((FreeFeatureActivity) context).deleteAlarm((Long) v.getTag());
        }
    }
}
