package com.jerry.baselib.common.weidgt;

import android.content.Context;

import com.jerry.baselib.R;

import java.lang.reflect.Method;
import java.util.Calendar;

public class TimePickerDialog extends BaseDialog {

    private NumberPicker mHourNp;
    private NumberPicker mMinNp;
    private boolean mHalfHourMode;//只显示半小时和整点
    private int maxHour = 23;
    private int hour = -1;
    private int minute = -1;

    public TimePickerDialog(Context context) {
        super(context);
    }

    public void setHalfHourMode(final boolean halfHourMode) {
        mHalfHourMode = halfHourMode;
    }

    public void setMaxHour(final int maxHour) {
        this.maxHour = maxHour;
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_time_picker;
    }

    @Override
    protected void initView() {
        super.initView();
        mHourNp = findViewById(R.id.hour_np);
        mMinNp = findViewById(R.id.min_np);

        mHourNp.setFormatter(value -> mContext.getString(R.string.format_hour_min, value, "时"));

        // 解决首次弹出选择器时，formatter对当前value不起作用
        try {
            Method hourNpMethod = mHourNp.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            hourNpMethod.setAccessible(true);
            hourNpMethod.invoke(mHourNp, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMinNp.setFormatter(value -> mContext.getString(R.string.format_hour_min, mHalfHourMode ? value * 30 : value, "分"));

        try {
            Method minNpMethod = mMinNp.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            minNpMethod.setAccessible(true);
            minNpMethod.invoke(mMinNp, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHourNp.setMaxValue(maxHour);
        mHourNp.setMinValue(0);
        mMinNp.setMaxValue(mHalfHourMode ? 1 : 59);
        mMinNp.setMinValue(0);

        if (hour >= 0 && hour <= maxHour && minute >= 0 && minute <= 59) {
            mHourNp.setValue(hour);
            mMinNp.setValue(minute);
        } else {
            final Calendar c = Calendar.getInstance();
            mHourNp.setValue(c.get(Calendar.HOUR_OF_DAY));
            mMinNp.setValue(c.get(Calendar.MINUTE));
        }
    }

    /**
     * 获取当前时分的字符串
     */
    public String getDate() {
        if (mHalfHourMode) {
            return mContext.getString(R.string.format_time, mHourNp.getValue(), mMinNp.getValue() * 30);
        } else {
            return mContext.getString(R.string.format_time, mHourNp.getValue(), mMinNp.getValue());
        }
    }

    public void setHourMin(int hour, int min) {
        this.hour = hour;
        this.minute = min;
        if (mHourNp != null) {
            mHourNp.setValue(hour);
        }
        if (mMinNp != null) {
            mMinNp.setValue(min);
        }
    }

    public int getHour() {
        return mHourNp.getValue();
    }

    public int getMin() {
        return mHalfHourMode ? mMinNp.getValue() * 30 : mMinNp.getValue();
    }
}
