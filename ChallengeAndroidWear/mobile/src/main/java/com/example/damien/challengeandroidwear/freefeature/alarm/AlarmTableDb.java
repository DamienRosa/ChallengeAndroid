package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.provider.BaseColumns;

public final class AlarmTableDb {

    public AlarmTableDb() {
        //nothing
    }

    public static abstract class Alarm implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        public static final String COLUMN_NAME_ALARM_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ALARM_TIME_HOUR = "hour";
        public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "minute";
        public static final String COLUMN_NAME_ALARM_ENABLED = "enabled";
    }
}
