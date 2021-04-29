package com.example.smart_control.base.scan_broadcast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static final String TIME_FORMAT = "HH:mm:ss";

    public static String formatTime(Long timestamp) {
        if (timestamp == null){
            return "";
        }
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(tz);

        return sdf.format(new Date(timestamp));
    }
}
