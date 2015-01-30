package com.example.damien.challengeandroidwear.freefeature.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.damien.challengeandroidwear.freefeature.alarm.AlarmTableDb.Alarm;

import java.util.ArrayList;

// Alarm data base
public class AlarmDataBase extends SQLiteOpenHelper {

    public AlarmDataBase(Context context) {
        super(context, AlarmConstant.DATABASE_NAME, null, AlarmConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlarmConstant.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AlarmConstant.SQL_DELETE_TABLE);
        onCreate(db);
    }

    //populate alarm object
    private AlarmObject populate(Cursor c) {
        AlarmObject alarm = new AlarmObject();
        alarm.setID(c.getLong(c.getColumnIndex(Alarm._ID)));
        alarm.setDescription(c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_DESCRIPTION)));
        alarm.setHourOfDay(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR)));
        alarm.setMinute(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE)));
        alarm.setEnable(c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true);
        return alarm;
    }

    // populate content
    private ContentValues populateContent(AlarmObject alarm) {
        ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_NAME_ALARM_DESCRIPTION, alarm.getDescription());
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, alarm.getHourOfDay());
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, alarm.getMinute());
        values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, alarm.isEnable());
        return values;
    }

    //create alarm in data base
    public void createAlarm(AlarmObject alarm) {
        ContentValues value = populateContent(alarm);
        this.getWritableDatabase().insert(Alarm.TABLE_NAME, null, value);
    }

    //delete alarm in data base
    public void deleteAlarm(long id) {
        this.getWritableDatabase().delete(Alarm.TABLE_NAME, Alarm._ID + "= ?", new String[]{String.valueOf(id)});
    }

    //update alarm in data base
    public void updateAlarm(AlarmObject obj) {
        ContentValues value = populateContent(obj);
        this.getWritableDatabase().update(Alarm.TABLE_NAME, value, Alarm._ID + " = ?", new String[]{String.valueOf(obj.getId())});
    }

    //get a specific alarm
    public AlarmObject getAlarm(long l) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm._ID + " = " + l;
        Cursor c = db.rawQuery(select, null);
        if (c.moveToNext()) {
            return populate(c);
        }
        return null;
    }

    //get all alarms in date base
    public ArrayList<AlarmObject> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Alarm.TABLE_NAME;
        Cursor c = db.rawQuery(select, null);
        ArrayList<AlarmObject> alarms = new ArrayList<>();
        while (c.moveToNext()) {
            alarms.add(populate(c));
        }

        if (!alarms.isEmpty()) {
            return alarms;
        }

        return null;
    }

}
