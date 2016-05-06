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
    public static String oneWeekDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -7);
        return getFormattedDate(calendar.getTimeInMillis());
    }
    public static String oneMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return getFormattedDate(calendar.getTimeInMillis());
    }

    public static int[] gcd2(int p, int q,int count) {
        int auxP = p;
        int auxQ = q;
        int auxI = 1;
        while (q != 0) {
            int temp = q;
            q = p % q;
            p = temp;
        }
        while(p == 1){
            auxP += auxI;
            p = auxP;
            q = auxQ;
            while (q != 0){
                int temp = q;
                q = p % q;
                p = temp;
            }
        }
        if(count >= 60){
            while(p <= 5){
                auxP += auxI;
                auxQ -= auxI;
                p = auxP;
                q = auxQ;
                while (q != 0){
                    int temp = q;
                    q = p % q;
                    p = temp;
                }
            }
        }
        int stepMax[] = new int[3];
        stepMax[0] = p;
        stepMax[1] = auxP;
        stepMax[2] = auxQ;
        return stepMax;
    }
}


