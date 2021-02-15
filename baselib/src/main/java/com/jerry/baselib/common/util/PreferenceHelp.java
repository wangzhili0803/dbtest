package com.jerry.baselib.common.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;

/**
 * SharedPreference操作类
 */
public class PreferenceHelp {

    public static final String ISENTITY = "isentity";
    public static final String ISNEW = "isnew";
    public static final String AUTO_REPLAY = "auto_replay";
    public static final String PUBLISH_COUNT = "publish_count";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String RANDOM_ADDRESS = "random_address";
    public static final String PUBLISH_TRANS_FEE = "publish_trans_fee";
    public static final String PUBLISH_ADDRESS = "publish_address";
    public static final String PDD_COOKIE = "pddCookie";
    public static final String DIRNAME = "dirname";
    public static final String RANDOM_FISH = "random_fish";
    public static final String FISH_POND = "fish_pond";
    public static final String HOUSE_RENT = "house_rent";
    public static final String HOUSE_RENOVATION = "house_renovation";
    public static final String HOUSE_ROOM_TYPE = "house_room_type";
    public static final String TASK_PLATFORM = "task_platform";
    /**
     * 0：商品，1：免费送
     */
    public static final String FREESEND = "freesend";

    private PreferenceHelp() {
    }

    private static final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseApp.getInstance());

    public static String getString(String strKey) {
        return sp.getString(strKey, Key.NIL);
    }

    public static String getString(String strKey, String strDefault) {
        return sp.getString(strKey, strDefault);
    }

    public static void putString(String strKey, String strData) {
        sp.edit().putString(strKey, strData).apply();
    }

    public static int getInt(String strKey) {
        return sp.getInt(strKey, 0);
    }

    public static int getInt(String strKey, int strDefault) {
        return sp.getInt(strKey, strDefault);
    }

    public static void putInt(String strKey, int strData) {
        sp.edit().putInt(strKey, strData).apply();
    }

    public static float getFloat(String strKey) {
        return sp.getFloat(strKey, 0);
    }

    public static float getFloat(String strKey, int strDefault) {
        return sp.getFloat(strKey, strDefault);
    }

    public static void putFloat(String strKey, float strData) {
        sp.edit().putFloat(strKey, strData).apply();
    }

    public static long getLong(String strKey) {
        return sp.getLong(strKey, 0);
    }

    public static long getLong(String strKey, long strDefault) {
        return sp.getLong(strKey, strDefault);
    }

    public static void putLong(String strKey, long l) {
        sp.edit().putLong(strKey, l).apply();
    }

    public static boolean getBoolean(String strKey) {
        return sp.getBoolean(strKey, false);
    }

    public static boolean getBoolean(String strKey, boolean bDefault) {
        return sp.getBoolean(strKey, bDefault);
    }

    public static void putBoolean(String strKey, boolean bValue) {
        sp.edit().putBoolean(strKey, bValue).apply();
    }
}