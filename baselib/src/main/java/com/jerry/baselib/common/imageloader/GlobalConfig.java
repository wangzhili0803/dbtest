package com.jerry.baselib.common.imageloader;

import android.os.Looper;

import com.jerry.baselib.common.util.WeakHandler;

/**
 * Created by wzl on 2017/11/14.
 *
 * @Description 全局配置
 */
public class GlobalConfig {

    private static ILoader loader;
    private static WeakHandler sHandler;

    private GlobalConfig() {
    }

    public static ILoader getLoader() {

        if (loader == null) {
            loader = new GlideLoader();
        }

        return loader;
    }

    public static WeakHandler getHandler() {
        if (sHandler == null) {
            sHandler = new WeakHandler(Looper.getMainLooper());
        }
        return sHandler;
    }
}
