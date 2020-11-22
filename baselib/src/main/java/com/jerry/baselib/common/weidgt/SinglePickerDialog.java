package com.jerry.baselib.common.weidgt;

import android.content.Context;
import androidx.annotation.StringRes;
import android.util.SparseArray;
import android.widget.TextView;

import com.jerry.baselib.R;

import java.lang.reflect.Method;

public class SinglePickerDialog extends BaseDialog {

    @StringRes
    private int title;
    /**
     * 初始值
     */
    private int mValue;
    private SparseArray<String> mArray;
    private NumberPicker np;

    public SinglePickerDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_single_picker;
    }

    @Override
    protected void initView() {
        super.initView();
        TextView titleTv = findViewById(R.id.title_tv);
        titleTv.setText(title);
        np = findViewById(R.id.np);
        np.setMinValue(0);
        np.setMaxValue(mArray.size() > 0 ? mArray.size() - 1 : 0);
        np.setValue(mValue);
        np.setFormatter(value -> mArray.get(value));
        // 解决首次弹出选择器时，formatter对当前value不起作用
        try {
            Method yearNpMethod = np.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            yearNpMethod.setAccessible(true);
            yearNpMethod.invoke(np, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleText(@StringRes int id) {
        title = id;
    }

    public void setRange(SparseArray<String> array) {
        mArray = array;
    }

    /**
     * 拼接成请求格式："yyyyMMddHHmmss"
     * 我去年买了个表，超耐磨！
     */
    public int getValue() {
        return np.getValue();
    }

    public void setValue(int value) {
        mValue = value;
        if (np != null) {
            np.setValue(value);
        }
    }
}
