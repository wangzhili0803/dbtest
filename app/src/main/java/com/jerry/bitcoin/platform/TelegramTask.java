package com.jerry.bitcoin.platform;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.common.util.LogUtils;
import com.jerry.bitcoin.ListenerService;

/**
 * @author Jerry
 * @createDate 7/2/21
 * @copyright www.axiang.com
 * @description
 */
public class TelegramTask {

    private static final String PACKAGE_NAME = "pro.huobi:id/";

    public void doTask(final ListenerService service) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        LogUtils.e("T");

    }
}
