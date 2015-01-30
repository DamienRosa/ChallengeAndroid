package com.example.damien.challengeandroidwear.freefeature.alarm;

public class AlarmObject {

    private long id;
    private int hourOfDay;
    private int minute;
    private String description;
    private boolean enable;

    public AlarmObject(int hour, int minute, String description, boolean b) {
        this.hourOfDay = hour;
        this.minute = minute;
        this.description = description;
        this.enable = b;
    }

    public AlarmObject() {

    }

    public long getId() {
        return id;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setID(long id) {
        this.id = id;
    }
}
