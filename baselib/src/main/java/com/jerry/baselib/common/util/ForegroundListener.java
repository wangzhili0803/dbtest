package com.jerry.baselib.common.util;

/**
 * Created by th on 2017/7/18. 类说明:应用切换到前台
 */
public interface ForegroundListener {

    /**
     * 回到前台
     */
    void onForeground();

    /**
     * 退出前台
     */
    void onBackground();


}
