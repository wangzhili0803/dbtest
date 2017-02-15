package com.vince.dbtest;

import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.Bugly;

/**
 * Created by wzl-pc on 2017/2/15.
 */

public class FYApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "202d1fda06", false);
    }
}
