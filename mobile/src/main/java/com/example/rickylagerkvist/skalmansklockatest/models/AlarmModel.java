package com.example.rickylagerkvist.skalmansklockatest.models;

import com.example.rickylagerkvist.skalmansklockatest.models.enums.AlarmType;

/**
 * Created by rickylagerkvist on 2017-02-12.
 */

public class AlarmModel {

    private String title;
    private int calendarHour;
    private int calendarMin;
    private boolean isAlarmOn;
    private AlarmType alarmType;
    private int intentNr;

    public AlarmModel(String title, int calendarHour, int calendarMin, AlarmType alarmType, int intentNr) {
        this.title = title;
        this.calendarHour = calendarHour;
        this.calendarMin = calendarMin;
        this.alarmType = alarmType;
        this.isAlarmOn = true;
        this.intentNr = intentNr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCalendarHour() {
        return calendarHour;
    }

    public void setCalendarHour(int calendarHour) {
        this.calendarHour = calendarHour;
    }

    public int getCalendarMin() {
        return calendarMin;
    }

    public void setCalendarMin(int calendarMin) {
        this.calendarMin = calendarMin;
    }

    public String getCalenderText(){

        String hour = calendarHour + "";
        if(calendarHour < 10) {
            hour = "0"+ hour;
        }
        String min = calendarMin + "";
        if(calendarMin < 10) {
            min = "0" + min;
        }

        return hour + ":" + min;
    }

    public boolean isAlarmOn() {
        return isAlarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        isAlarmOn = alarmOn;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    public int getIntentNr() {
        return intentNr;
    }

}
