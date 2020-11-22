package com.jerry.baselib;

/**
 * Created by ssx on 9/2/16. 类说明: Key
 */
public class Key {

    private Key() {}

    public static final long TIME_SECOND = 1000;
    public static final long TIME_MINUTE = TIME_SECOND * 60;
    public static final long TIME_HOUR = TIME_MINUTE * 60;
    public static final long TIME_DAY = TIME_HOUR * 24;
    public static final long TIME_MONTH = TIME_DAY * 30;
    public static final long TIME_YEAR = TIME_DAY * 365;

    public static final String USER_INFO = "user_info";
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String CODE = "code";

    public static final String WXCODE = "wxCode";
    public static final String DEVICEID = "deviceId";
    public static final String DATA = "data";
    public static final String NIL = "";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ACTION = "action";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String LINE = "-";
    public static final String ULINE = "_";
}
