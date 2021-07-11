package com.jerry.baselib;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.multidex.MultiDex;

import com.jerry.baselib.common.util.ForegroundCallbacks;

import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;
import cn.leancloud.core.RequestPaddingInterceptor;
import cn.leancloud.sign.NativeSignHelper;
import cn.leancloud.sign.SecureRequestSignature;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @description
 */
public abstract class BaseApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Application mInstance;
    private ForegroundCallbacks mForegroundCallbacks;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initConfig();
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        AVOSCloud.initialize(getInstance(), Config.AVOS_APP_ID, Config.AVOS_APP_KEY);
        NativeSignHelper.initialize(this);
        RequestPaddingInterceptor.changeRequestSignature(new SecureRequestSignature());
        mForegroundCallbacks = new ForegroundCallbacks();
        MultiDex.install(this);
        registerActivityLifecycleCallbacks(mForegroundCallbacks);
    }


    @Override
    public void onTerminate() {
        if (mForegroundCallbacks != null) {
            unregisterActivityLifecycleCallbacks(mForegroundCallbacks);
        }
        super.onTerminate();
    }

    protected abstract void initConfig();

    public static class Config {

        public static boolean DEBUG;
        public static String APPLICATION_ID;
        public static int VERSION_CODE;
        public static String VERSION_NAME;
        public static String UMENG_APP_KEY;
        public static String BUGLY_APP_ID;
        public static String AVOS_APP_ID;
        public static String AVOS_APP_KEY;
        public static String FILE_PROVIDER;
        public static String SIGN;
        public static Class<?> ACCESS_CLASS;
    }

    public static String getDefultDir() {
        return Config.APPLICATION_ID;
    }
}
