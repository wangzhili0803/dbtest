package com.jerry.baselib.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewConfiguration;

import androidx.core.app.ActivityCompat;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.BaseApp.Config;
import com.jerry.baselib.Key;
import com.jerry.baselib.PackerNg;
import com.jerry.baselib.R;
import com.jerry.baselib.common.weidgt.CustomDialog;

/**
 * 常用方法的工具类
 *
 * @author my
 * @time 2016/9/22 14:41
 */
public class AppUtils {

    public static boolean playing = false;
    private static long lastClickTime;
    private static Pattern chinesePattern = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");

    /**
     * 获取应用程序appdata
     */
    public static String getAppData() {
        String verName = getVersionName();
        int verCode = getVersionCode();
        if (TextUtils.isEmpty(verName) || 0 == verCode) {
            return null;
        }
        return "appVersionName:" + verName + Key.COMMA + "appVersionCode:" + verCode + Key.COMMA + "channel:" + getChannel();
    }

    /**
     * 获取应用程序versionName
     */
    public static String getVersionName() {
        Context context = BaseApp.getInstance().getApplicationContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return Key.NIL;
        }
    }

    /**
     * 是否快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < ViewConfiguration.getJumpTapTimeout()) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取渠道号
     */
    public static String getChannel() {
        return PackerNg.getMarket(BaseApp.getInstance(), "proxy");//proxy
    }

    /**
     * 检测辅助功能是否开启<br> 方 法 名：isAccessibilitySettingsOn <br> 创 建 人 <br> 创建时间：2016-6-22 下午2:29:24 <br> 修 改 人： <br> 修改日期： <br>
     *
     * @return boolean
     */
    public static boolean isAccessibilitySettingsOff(Context context) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = context.getPackageName() + "/" + Config.ACCESS_CLASS.getCanonicalName();
        LogUtils.i("service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtils.d("accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e("Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtils.d("***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtils.d("-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtils.d("We've found the correct setting - accessibility is switched on!");
                        return false;
                    }
                }
            }
        } else {
            LogUtils.d("***ACCESSIBILITY IS DISABLED***");
        }
        return true;
    }


    /**
     * 获取泛型类的type
     *
     * @param raw 泛型类的class, 如BaseResponse4Object.class
     * @param args 泛型实参的class, LotteryBean.class
     * @return 泛型类的type
     */
    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }

            @Override
            public Type getRawType() {
                return raw;
            }
        };
    }


    /**
     * 获取设备ID,如果ID为空,再取Mac地址,都为空最后随机生成
     */
    @SuppressLint("hardwareIds")
    public static String getDeviceId() {
        Context context = BaseApp.getInstance().getApplicationContext();
        String deviceId = PreferenceHelp.getString(Key.DEVICEID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (tm == null) {
                deviceId = Key.NIL;
            } else {
                deviceId = tm.getDeviceId();
            }
            deviceId = deviceId == null ? Key.NIL : deviceId;
        } catch (SecurityException e) {
            e.printStackTrace();
            deviceId = Key.NIL;
        }

        //状态码权限被关闭:获得的DeviceId全部字符相同或者为空则判定为权限被关闭，使用UUID作为唯一标识
        if (TextUtils.isEmpty(deviceId) || Pattern.matches("(.)(\\1)*", deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }

        if (isChinese(deviceId)) {
            deviceId = changeToUrlEncode(deviceId);
        }
        PreferenceHelp.putString(Key.DEVICEID, deviceId);
        return deviceId;
    }

    private static boolean isChinese(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            Matcher m = chinesePattern.matcher(string.charAt(i) + Key.NIL);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static String changeToUrlEncode(String str) {
        if (TextUtils.isEmpty(str)) {
            return Key.NIL;
        }
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception ex) {
            return str;
        }
    }

    /**
     * 获取应用程序版本
     */
    public static int getVersionCode() {
        Context context = BaseApp.getInstance().getApplicationContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 退出app
     */
    public static void exitApp(final Activity activity) {
        final CustomDialog dialog = new CustomDialog(activity);
        dialog.show();
        dialog.setMessage(R.string.sure_exit);
        dialog.setPositiveListener(v -> {
            dialog.dismiss();
            ActivityCompat.finishAffinity(activity);
            try {
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
