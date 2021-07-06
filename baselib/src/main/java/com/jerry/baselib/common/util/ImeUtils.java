package com.jerry.baselib.common.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Jerry on 05/04/2017
 *
 * @description soft input method utils
 */

public class ImeUtils {

    private ImeUtils() {
    }

    /**
     * 弹出软键盘
     */
    public static void showIme(View view) {
        if (view.getContext() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == inputMethodManager) {
            return;
        }
        view.requestFocus();
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean hideIme(Activity activity) {
        if (activity == null || activity.getWindow() == null || activity.getWindow().getDecorView().getWindowToken() == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * hide ime when click outside only call in {@link Activity#dispatchTouchEvent(MotionEvent)} recently
     */
    public static boolean hideImeOutside(Activity activity, MotionEvent ev) {
        View v = activity.getCurrentFocus();
        if (v instanceof EditText) {
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) {
                int[] locations = new int[2];
                v.getLocationOnScreen(locations);
                float x = ev.getRawX() + v.getLeft() - locations[0];
                float y = ev.getRawY() + v.getTop() - locations[1];

                if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                    boolean b = hideIme(activity);
                    v.clearFocus();
                    return b;
                }
            }
        }
        return false;
    }
}
