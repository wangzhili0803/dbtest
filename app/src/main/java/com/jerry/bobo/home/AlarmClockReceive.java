package com.jerry.bobo.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jerry.baselib.common.util.LogUtils;

/**
 * @author Jerry
 * @createDate 2020/11/23
 * @description
 */
public class AlarmClockReceive extends BroadcastReceiver {

    public void onReceive(final Context context, final Intent intent) {
        LogUtils.d("onReceive: AlarmClockReceive");
    }
}
