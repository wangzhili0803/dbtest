package com.jerry.baselib.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.retrofit.RetrofitHelper;
import com.jerry.baselib.parsehelper.HttpApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 日期类
 *
 * @author Tina
 */
public class DateUtils {

    private static final String YYYYMMDD = "yyyy-MM-dd";
    private static final String HHMMSS = "HH:mm:ss";
    private static final String YYYYMMDD_HHMM = "yyyy-MM-dd HH:mm";
    public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";

    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat(YYYYMMDD, Locale.CHINA);
    private static final SimpleDateFormat FORMAT_TIME = new SimpleDateFormat(HHMMSS, Locale.CHINA);
    private static final SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat(YYYYMMDD_HHMM, Locale.CHINA);
    private static final SimpleDateFormat FORMAT_DATE_TIME_SS = new SimpleDateFormat(YYYYMMDD_HHMMSS, Locale.CHINA);
    private static final SimpleDateFormat FORMAT_DATETIME = new SimpleDateFormat(YYYYMMDDHHMMSS, Locale.CHINA);
    private static final SimpleDateFormat FORMAT_UTC = new SimpleDateFormat(UTC, Locale.CHINA);

    public static synchronized void main(String[] dfsdfs) {
        Date date = new Date();
        date.setTime(1560664101360L);
        String dsfds = FORMAT_DATE_TIME.format(date);
        System.out.println(dsfds);
    }

    /**
     *
     */
    public static synchronized long getLongByDateTime2(String dateStr) {
        try {
            return FORMAT_DATETIME.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     */
    public static synchronized long getLongByDateTime(String dateTimeStr) {
        try {
            return FORMAT_DATETIME.parse(dateTimeStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取日期
     */
    public static synchronized String getDateByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_DATE.format(date);
    }

    /**
     * 获取时分秒
     */
    public static synchronized String getTimeByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_TIME.format(date);
    }

    /**
     * 获取日期时分秒
     */
    public static synchronized String getDateTimeByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_DATETIME.format(date);
    }

    /**
     * 获取日期时分
     */
    public static synchronized String getDateWTimeByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_DATE_TIME.format(date);
    }

    /**
     * 获取日期时分秒
     */
    public static synchronized String getDateWTimesByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_DATE_TIME_SS.format(date);
    }

    public static String utc2Local(String oldDate) {
        try {
            oldDate = oldDate.replace("Z", " UTC");
            Date date = FORMAT_UTC.parse(oldDate);
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = df1.parse(date.toString());
            return FORMAT_DATE_TIME_SS.format(date1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String utc2LocalDate(String oldDate) {
        String str = utc2Local(oldDate);
        if (str != null && str.length() >= 10) {
            return str.substring(0, 10);
        }
        return Key.NIL;
    }

    public static String utc2LocalTime(String oldDate) {
        String str = utc2Local(oldDate);
        if (str != null && str.length() >= 11) {
            return str.substring(11);
        }
        return Key.NIL;
    }

    public static long utc2Long(String oldDate) {
        try {
            oldDate = oldDate.replace("Z", " UTC");
            Date date = FORMAT_UTC.parse(oldDate);
            if (date != null) {
                Date date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK).parse(date.toString());
                if (date1 != null) {
                    return date1.getTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String toFriendly(String str) {
        if (!TextUtils.isEmpty(str)) {
            str = str.trim();
            if (str.length() == YYYYMMDDHHMMSS.length()) {
                String s1 = str.substring(0, 4);
                String s2 = str.substring(4, 6);
                String s3 = str.substring(6, 8);
                String s4 = str.substring(8, 10);
                String s5 = str.substring(10, 12);
                StringBuilder sb = new StringBuilder();
                sb.append(s1).append(Key.LINE)
                    .append(s2).append(Key.LINE)
                    .append(s3).append(Key.SPACE)
                    .append(s4).append(Key.COLON)
                    .append(s5);
                return sb.toString();
            }
        }
        return Key.NIL;
    }

    public static void getNowTimeLong(OnDataCallback<Long> onDataCallback) {
        RetrofitHelper.getInstance().getApi(HttpApi.class).getTimestamp()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> onDataCallback.onDataCallback(result.getJSONObject(Key.DATA).getLong("t")));
    }
}
