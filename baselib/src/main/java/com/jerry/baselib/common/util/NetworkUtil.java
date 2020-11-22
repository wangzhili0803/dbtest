package com.jerry.baselib.common.util;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by wzl on 2018/8/26.
 *
 * @Description 类说明：网络工具类
 */
public class NetworkUtil {

    public static final int NETWORK_UNACTIVE = -1;
    public static final int NETWORK_UNKOWN = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE_2G = 2;
    public static final int NETWORK_MOBILE_3G = 3;
    public static final int NETWORK_MOBILE_4G = 4;
    /**
     * 联通
     */
    public static final String UNICOM = "中国联通";
    /**
     * 移动
     */
    public static final String MOBILE = "中国移动";
    /**
     * 电信
     */
    public static final String TELECOM = "中国电信";
    public static final String OPERATOR_UNICOM = "46001";
    public static final String OPERATOR_MOBILE = "46000";
    public static final String OPERATOR_TELECOM = "46003";
    public static final String UNKNOW = "未知信息";


    private NetworkUtil() {
    }

    public static boolean isNetworkAvailable() {
        return isNetworkAvailable(false);
    }

    /**
     * 网络是否ok
     *
     * @param alert 决定是否toast
     * @return 默认false
     */
    public static boolean isNetworkAvailable(boolean alert) {
        ConnectivityManager cm = (ConnectivityManager) BaseApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                return true;
            }
        }
        if (alert) {
            ToastUtil.showShortText(R.string.no_network);
        }
        return false;
    }

    @SuppressLint("HardwareIds")
    public static String getMacAddress() {
        WifiManager wifi = (WifiManager) BaseApp.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return Key.NIL;
        }
        WifiInfo info = wifi.getConnectionInfo();
        if (info == null) {
            return Key.NIL;
        }
        return info.getMacAddress();
    }

    /**
     * 区别手机具体网络类型
     */
    private static int getNetSubType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApp.getInstance().getSystemService(Context
            .CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return -1;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        return networkInfo.getSubtype();
    }

    /**
     * 获取运营商名称
     */
    public static String getOperatorName() {
        TelephonyManager telephonyManager = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return UNKNOW;
        }
        String operator = telephonyManager.getSimOperator();
        switch (operator) {
            case OPERATOR_MOBILE:
                return MOBILE;
            case OPERATOR_UNICOM:
                return UNICOM;
            case OPERATOR_TELECOM:
                return TELECOM;
            default:
                break;
        }
        return UNKNOW;
    }

    /**
     * 获取当前网络为2G/3G/4G/WIFI
     */
    public static int getNetType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApp.getInstance().getSystemService(Context
            .CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return NETWORK_UNKOWN;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NETWORK_UNKOWN;
        }
        int netType = networkInfo.getType();
        switch (netType) {
            //网络为wifi
            case ConnectivityManager.TYPE_WIFI:
                return NETWORK_WIFI;
            //移动网络
            case ConnectivityManager.TYPE_MOBILE: {
                switch (getNetSubType()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_MOBILE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS://联通3G
                    case TelephonyManager.NETWORK_TYPE_HSDPA://联通3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_0://电信3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_A://电信3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_B://电信3G
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_MOBILE_3G;
                    case TelephonyManager.NETWORK_TYPE_GPRS://联通2G
                    case TelephonyManager.NETWORK_TYPE_EDGE://移动2G
                    case TelephonyManager.NETWORK_TYPE_CDMA://电信2G
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_MOBILE_4G;
                    default:
                        break;
                }

            }
            //没有网络连接 ConnectivityManager.TYPE_NONE
            case NETWORK_UNACTIVE:
                return NETWORK_UNACTIVE;
            default:
                break;
        }
        return NETWORK_UNKOWN;
    }

    public static String getNetTypeStr() {
        int type = getNetType();
        switch (type) {
            case NETWORK_UNACTIVE:
                return "网络未连接";
            case NETWORK_WIFI:
                return "wifi";
            case NETWORK_MOBILE_2G:
                return "2G";
            case NETWORK_MOBILE_3G:
                return "3G";
            case NETWORK_MOBILE_4G:
                return "4G";
            default:
                return "未知信息";
        }
    }

    /**
     * 获取IP地址
     */
    public static void getNetIp(OnDataChangedListener<String> onDataChangedListener) {
        if (onDataChangedListener == null) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable()) {
            onDataChangedListener.onDataChanged(null);
            return;
        }
        OkHttpUtils.get().url("http://pv.sohu.com/cityjson?ie=utf-8").build().enqueue(new Callback<String>() {
            @Override
            public String parseNetworkResponse(final Response response) throws IOException {
                String content = response.body().string();
                LogUtils.d(content);
                String ip = null;
                int start = content.indexOf("{");
                int end = content.indexOf("}");
                String json = content.substring(start, end + 1);
                try {
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    ip = jsonObject.getString("cip");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String finalIp = ip;
                OkHttpUtils.getInstance().getDelivery().post(() -> onDataChangedListener.onDataChanged(finalIp));
                return ip;
            }
        });
    }

    /**
     * 是否使用了代理
     */
    public static boolean isProxy() {
        String proxyAddress = "";
        int proxyPort = 0;
        proxyAddress = System.getProperty("http.proxyHost");
        String proxyPortString = System.getProperty("http.proxyPort");
        proxyPort = Integer.parseInt((proxyPortString != null ? proxyPortString : "-1"));
        return !TextUtils.isEmpty(proxyAddress) && proxyPort != -1;
    }
}
