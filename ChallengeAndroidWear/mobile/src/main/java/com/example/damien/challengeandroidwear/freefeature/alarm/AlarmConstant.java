package com.example.damien.challengeandroidwear.freefeature.alarm;

public class AlarmConstant {
    public static final String DATABASE_NAME = "alarm.db";
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_TABLE =  "CREATE TABLE " + AlarmTableDb.Alarm.TABLE_NAME + " (" +
            AlarmTableDb.Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AlarmTableDb.Alarm.COLUMN_NAME_ALARM_DESCRIPTION + " TEXT," +
            AlarmTableDb.Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            AlarmTableDb.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            AlarmTableDb.Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" +
            " )";

    public static final String SQL_DELETE_TABLE =  "DROP TABLE IF EXISTS " + AlarmTableDb.Alarm.TABLE_NAME;
}
