package com.sam_chordas.android.stockhawk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joao on 04/05/2016.
 */
    public class Utility {

    public static String DATE_FORMAT = "yyyy-MM-dd";

    public static String getFormattedDate(long dateInMillis ) {
        Locale localeUS = new Locale("en", "US");
        SimpleDateFormat queryDayFormat = new SimpleDateFormat(DATE_FORMAT,localeUS);
        return queryDayFormat.format(dateInMillis);
    }

    public static String oneWeekDate(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -7);
        return getFormattedDate(calendar.getTimeInMillis());
    }
}


