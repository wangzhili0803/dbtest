package com.jerry.dbtest.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import java.lang.reflect.Method;

/**
 * android客户端工具类
 *
 * @author twf
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class AndroidUtil {
    /*
     * 获取手机序列号
     */
    public static String getDeviceId(Context ctx) {
        String deviceID = "";
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            deviceID = tm.getDeviceId();
        } else {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                deviceID = (String) get.invoke(c, "ro.serialno");
            } catch (Exception e) {
                Log.e("TAG", "get the system sn ERROR!", e);
            }
        }
        return deviceID;
    }

    public static String getClientDeviceInfo(Context ctx) {
        String deviceID = "";
        String serial = "";
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            deviceID = tm.getDeviceId();
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            } catch (Exception e) {
                Log.e("TAG", "get the system sn ERROR!", e);
            }
            Log.d("serial", "deviceID:" + deviceID);
        }
        String buildVersion = Build.VERSION.RELEASE;
        return deviceID + "|" + serial + "|android " + buildVersion;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /*
     * 取得操作系统版本号
     */
    public static String getOSVersion(Context ctx) {
        return Build.VERSION.RELEASE;
    }

    /**
     * 取得软件版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (pInfo != null) {
                return pInfo.versionName;
            }
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    /**
     * 获取系统统一的header头
     *
     * @return
     */
    public static String getWebkitHeader(Context ctx) {
        WebView webView = new WebView(ctx);
        return webView.getSettings().getUserAgentString();
    }

    public static Bitmap bigBitmap(Float w, Float h, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.reset();
        int w1 = bitmap.getWidth();
        int h1 = bitmap.getHeight();
        matrix.setScale(w, h);
        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, w1, h1, matrix, true);
        return b;

    }

}
