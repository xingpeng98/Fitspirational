package com.testapp.fitspirational.HelperClasses;

import java.util.Calendar;
import java.util.Date;

public class CurrentDate {

    public static String getCurrentDatetime() {
        int day, month, year, hr, min;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hr = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        return String.format("%d/%d/%d %d:%d HRS", day, month, year, hr, min);
    }

    public static String getCurrentDate() {
        int day, month, year;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        return String.format("%d/%d/%d", day, month, year);
    }

    public static int getCurrentYear() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
