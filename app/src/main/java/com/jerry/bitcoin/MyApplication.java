package com.jerry.bitcoin;

import android.content.Intent;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.bitcoin.home.MainActivity;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;
import com.umeng.commonsdk.UMConfigure;

public class MyApplication extends BaseApp {

    @Override
    protected void initConfig() {
        Config.DEBUG = BuildConfig.DEBUG;
        Config.SIGN = BuildConfig.SIGN;
        Config.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        Config.VERSION_CODE = BuildConfig.VERSION_CODE;
        Config.VERSION_NAME = BuildConfig.VERSION_NAME;
        Config.UMENG_APP_KEY = BuildConfig.UMENG_APP_KEY;
        Config.AVOS_APP_ID = BuildConfig.AVOS_APP_ID;
        Config.AVOS_APP_KEY = BuildConfig.AVOS_APP_KEY;
        Config.BUGLY_APP_ID = BuildConfig.BUGLY_APP_ID;
        Config.FILE_PROVIDER = BuildConfig.FILE_PROVIDER;
        Config.ACCESS_CLASS = ListenerService.class;
        Beta.canShowUpgradeActs.add(MainActivity.class);
        BuglyStrategy strategy = new BuglyStrategy();
        strategy.setAppVersion(Config.VERSION_NAME + (Config.DEBUG ? "test" : Key.NIL));
        strategy.setAppChannel(AppUtils.getChannel());
        UMConfigure.init(this, Config.UMENG_APP_KEY, AppUtils.getChannel(), 0, null);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        try {
            Bugly.init(getInstance(), Config.BUGLY_APP_ID, Config.DEBUG, strategy);
            getInstance().startService(new Intent(getInstance(), ListenerService.class));
        } catch (Exception ignored) {
        }
    }
}