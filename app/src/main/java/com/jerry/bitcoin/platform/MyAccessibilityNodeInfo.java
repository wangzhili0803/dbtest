package com.jerry.bitcoin.platform;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author Jerry
 * @createDate 5/16/21
 * @copyright www.axiang.com
 * @description
 */
public class MyAccessibilityNodeInfo implements Comparable<MyAccessibilityNodeInfo> {

    private AccessibilityNodeInfo mAccessibilityNodeInfo;
    private int y;

    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return mAccessibilityNodeInfo;
    }

    public void setAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        mAccessibilityNodeInfo = accessibilityNodeInfo;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    @Override
    public int compareTo(final MyAccessibilityNodeInfo other) {
        if (this.y > other.y) {
            return 1;
        } else if (this.y < other.y) {
            return -1;
        }
        return 0;
    }
}
