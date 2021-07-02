package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.DisplayUtil;

/**
 *
 */
public class MultipleEditText extends FrameLayout implements View.OnClickListener {

    private static final int MAX_MULTIPLE = 9;
    private EditText mEdit;
    /**
     * 倍数 (范围：0-maxMultiple)
     */
    private int multiple;
    private int maxMultiple = MAX_MULTIPLE;

    public int getMultiple() {
        return multiple;
    }

    private int cursor = -1;
    private MyTextWatcher mMyTextWatcher;

    public MultipleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutParams params = new LayoutParams(-1, DisplayUtil.dip2px(35));
        params.gravity = Gravity.CENTER;
        params.leftMargin = context.getResources().getDimensionPixelOffset(R.dimen.four_dp);
        params.rightMargin = params.leftMargin;
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.multiple_edit_view, null);
        addView(view, params);
        findViewById(R.id.minus_layout).setOnClickListener(this);
        findViewById(R.id.add_layout).setOnClickListener(this);
        mEdit = findViewById(R.id.times_et);
        mEdit.setHint("1");
        mEdit.addTextChangedListener(new MyTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > String.valueOf(maxMultiple).length()) {
                    mEdit.setText(String.valueOf(maxMultiple));
                    mEdit.setSelection(String.valueOf(maxMultiple).length());
                    multiple = maxMultiple;
                } else if (s.length() == 0) {
                    multiple = 0;
                } else if ("00".equals(s.toString()) || "000".equals(s.toString()) || "0000".equals(s.toString())) {
                    mEdit.setText("0");
                    mEdit.setSelection(1);
                    multiple = 0;
                } else {
                    if (TextUtils.isEmpty(s.toString())) {
                        multiple = 0;
                    } else {
                        multiple = Integer.parseInt(mEdit.getText().toString());
                        if (multiple > maxMultiple) {
                            mEdit.setText(String.valueOf(maxMultiple));
                            multiple = maxMultiple;
                            mEdit.setSelection(String.valueOf(maxMultiple).length());
                        }
                    }
                }
                if (cursor == -1) {
                    cursor = mEdit.getSelectionStart();
                }
                cursor = -1;
                if (mMyTextWatcher != null) {
                    mMyTextWatcher.afterTextChanged(s);
                }
            }

        });
    }

    public void initData(int multiple, int totalMultiple, MyTextWatcher myTextWatcher) {
        if (mEdit != null) {
            mEdit.setText(String.valueOf(multiple));
        }
        this.multiple = multiple;
        maxMultiple = totalMultiple;
        mMyTextWatcher = myTextWatcher;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.minus_layout) {
            int multiple1;
            if (mEdit.getText().toString().length() == 0) {
                multiple1 = 0;
            } else {
                multiple1 = Integer.parseInt(mEdit.getText().toString());
            }
            if (multiple1 <= 0) {
                return;
            }
            multiple = multiple1 - 1;
            cursor = mEdit.getSelectionStart();
            mEdit.setText(String.valueOf(multiple));
        } else if (id == R.id.add_layout) {
            int multiple2;
            if (mEdit.getText().toString().length() == 0) {
                multiple2 = 0;
            } else {
                multiple2 = Integer.parseInt(mEdit.getText().toString());
            }
            multiple = multiple2 + 1;
            cursor = mEdit.getSelectionStart();
            mEdit.setText(String.valueOf(multiple));
        }
    }

}
