package com.jerry.baselib.common.util;

import android.text.TextUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 基本数据类型解析工具类
 */
public class ParseUtil {

    private ParseUtil() {
    }

    /**
     * 解析以字符串表示的整数类型
     */
    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    /**
     * 解析以字符串表示的整数类型，如果发生异常则返回默认值
     */
    public static int parseInt(String s, int defaultInt) {
        if (TextUtils.isEmpty(s)) {
            return defaultInt;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
            return defaultInt;
        }
    }

    /**
     * 解析以字符串表示的长整数类型
     */
    public static long parseLong(String s) {
        return parseLong(s, 0L);
    }

    /**
     * 解析以字符串表示的长整数类型，如果发生异常则返回默认值
     */
    public static long parseLong(String s, long defaultLong) {
        if (TextUtils.isEmpty(s)) {
            return defaultLong;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultLong;
    }

    /**
     * 解析以字符串表示的单精度浮点类型
     */
    public static float parseFloat(String s) {
        return parseFloat(s, 0.0F);
    }

    /**
     * 解析以字符串表示的单精度浮点类型，如果发生异常则返回默认值
     */
    public static float parseFloat(String s, float defaultFloat) {
        if (TextUtils.isEmpty(s)) {
            return defaultFloat;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultFloat;
    }

    /**
     * 解析以字符串表示的双精度浮点类型
     */
    public static double parseDouble(String s) {
        return parseDouble(s, 0.0);
    }

    /**
     * 解析以字符串表示的双精度浮点类型，如果发生异常则返回默认值
     */
    public static double parseDouble(String s, double defaultDouble) {
        if (TextUtils.isEmpty(s)) {
            return defaultDouble;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultDouble;
    }

    /**
     * 转换小数为百分比
     *
     * @param fraction 精度
     */
    public static String parse2Percent(double d, int fraction) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumIntegerDigits(3);
        numberFormat.setMinimumFractionDigits(fraction);
        return numberFormat.format(d);
    }

    /**
     * 转换小数为百分比, 默认保留一位小数
     */
    public static String parse2Percent(String s) {
        return parse2Percent(parseDouble(s), 0);
    }

    /**
     * 转换小数为百分比
     *
     * @param fraction 精度
     */
    public static String parse2Percent(String s, int fraction) {
        return parse2Percent(parseDouble(s), fraction);
    }

    /**
     * 数字格式化为#,###类型
     */
    public static String parseNum2USFormat(int num) {
        return NumberFormat.getInstance(Locale.US).format(num);
    }
}
