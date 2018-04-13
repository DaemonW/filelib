package com.grt.filemanager.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public final static String DATE_FORMAT1 = "yyyy-MM-dd HH:mm";
    public final static String DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT3 = "yyyy-MM-dd";
    public final static String DATE_FORMAT_FOR_ATTACHMENTS = "yyyy-MM-dd-hh-mm-ss";
    public final static String BUSY_BOX_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public final static String DATE_FORMAT_CHINA = "yyyy-MM-dd HH:mm";
    public final static String DATE_FORMAT_FOREIGN = "dd MMM yyyy HH:mm";
    public final static String DATE_FORMAT_UTC = "EEE, dd MMM yyyy HH:mm:ss 'UTC'";
    public final static String DATE_FORMAT_GMT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    public final static String DATE_FORMAT_UTC2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    public static String getDateEnglish(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return format.format(date);
    }

    public static long parserModifiedDateEnglish(String ModifiedDate, String format) {
        Date date = null;

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            date = dateFormat.parse(ModifiedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date != null ? date.getTime() : new Date().getTime();
    }

    /**
     * 计算时长
     * @param num 时长毫秒
     * @return 格式h'h'm'm's's'
     */
    public static String getTimeSpanString(long num) {
        long hours = (num / 3600000) % 24;
        long minutes = (num / 60000) % 60;
        long seconds = (num / 1000) % 60 ;
        String secString, minString;
        if(seconds < 10) {
            secString = "0" + seconds;
        } else {
            secString = String.valueOf(seconds);
        }
        if(hours > 0 && minutes < 10) {
            minString = "0" + minutes;
        } else {
            minString = String.valueOf(minutes);
        }

        return hours > 0 ? hours + "h"+ minString + "m" + secString + "s" : minString + "m" + secString + "s";
    }

    public static long getDateTime(String timeString, String DateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(DateFormat, Locale.getDefault());
        Date date;
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        return date.getTime();
    }

    public static long getCurrentTime() {
        Date date = new Date();
        return date.getTime();
    }

    public static String getCurrentTimeString() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT1, Locale.getDefault());
        return format.format(date);
    }

    public static String getNowStr(String formats) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formats, Locale.getDefault());
        return format.format(date);
    }

    public static long parserModifiedDate(String ModifiedDate, String format) {
        Date date = new Date();
        long time;

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            date = dateFormat.parse(ModifiedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = date.getTime();
        return time;
    }

    public static String secToTime(long time) {
        String timeStr;
        long hour;
        long minute;
        long second;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getCurrentSecondString() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_ATTACHMENTS, Locale.getDefault());
        return dateFormat.format(now);
    }

    public static long parserModifiedDate2(String ModifiedDate, String format) {
        Date date = new Date();
        long time;
        Calendar calendar = Calendar.getInstance();
        long offsetTime = calendar.getTimeZone().getOffset(date.getTime());

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            date = dateFormat.parse(ModifiedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = date.getTime();
        return time + offsetTime;
    }

//    public static long parserTime(String parserTime){
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
//        try {
//            Date date = format.parse(parserTime);
//            date.getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取格式化时间字符串.
     *
     * @param num 以表示自从标准基准时间，即 1970年1月1日00:00:00 GMT 以来的指定毫秒数
     * @return 格式化时间字符串
     */
    public static String getDateString(long num, String DateFormat) {
        Locale locale = LanguageUtils.getLanguageSetting();
        Date date = new Date(num);
        SimpleDateFormat format = new SimpleDateFormat(DateFormat, locale);
        return format.format(date);
    }

}
