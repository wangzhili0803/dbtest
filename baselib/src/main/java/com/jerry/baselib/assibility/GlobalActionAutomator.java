package com.jerry.baselib.assibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import android.view.ViewConfiguration;

import com.jerry.baselib.common.util.LogUtils;

/**
 * Created by Stardust on 2017/5/16.
 */

public class GlobalActionAutomator {

    private AccessibilityService mService;

    public GlobalActionAutomator(AccessibilityService mService) {
        this.mService = mService;
    }

    public boolean back() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public boolean home() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean powerDialog() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
    }

    private boolean performGlobalAction(int globalAction) {
        if (mService == null) {
            return false;
        }
        return mService.performGlobalAction(globalAction);
    }

    public boolean notifications() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    public boolean quickSettings() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
    }

    public boolean recents() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean splitScreen() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gesture(long start, long duration, int[]... points) {
        Path path = pointsToPath(points);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return gestures(new GestureDescription.StrokeDescription(path, start, duration, false));
        }
        return gestures(new GestureDescription.StrokeDescription(path, start, duration));
    }

    private Path pointsToPath(int[][] points) {
        Path path = new Path();
        path.moveTo(points[0][0], points[0][1]);
        for (int i = 1; i < points.length; i++) {
            int[] point = points[i];
            path.lineTo(point[0], point[1]);
        }
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gestureAsync(long start, long duration, int[]... points) {
        Path path = pointsToPath(points);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gesturesAsync(new GestureDescription.StrokeDescription(path, start, duration, false));
        }
        gesturesAsync(new GestureDescription.StrokeDescription(path, start, duration));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gestures(GestureDescription.StrokeDescription... strokes) {
        if (mService == null) {
            return false;
        }
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        return gesturesWithoutHandler(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean gesturesWithoutHandler(GestureDescription description) {
        prepareLooperIfNeeded();
        boolean isDispatched = mService.dispatchGesture(description, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
        LogUtils.d("Was it dispatched? " + isDispatched);
        return isDispatched;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gesturesAsync(GestureDescription.StrokeDescription... strokes) {
        if (mService == null) {
            return;
        }
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        mService.dispatchGesture(builder.build(), null, null);
    }

    private void prepareLooperIfNeeded() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean click(int x, int y) {
        return press(x, y, ViewConfiguration.getTapTimeout() + 50);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean press(int x, int y, int delay) {
        return gesture(0, delay, new int[]{x, y});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean longClick(int x, int y) {
        return gesture(0, ViewConfiguration.getLongPressTimeout() + 200, new int[]{x, y});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean swipe(int x1, int y1, int x2, int y2, int delay) {
        return gesture(0, delay, new int[]{x1, y1}, new int[]{x2, y2});
    }

}
