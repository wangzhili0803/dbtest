package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.OnDataChangedListener;

/**
 * 带清空按钮的EditText
 */
public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Drawable dLeft;
    private Drawable dRight;
    private OnDataChangedListener<CharSequence> mOnDataChangedListener;

    public MyEditText(Context context) {
        super(context);
        initEditText();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText();
    }

    private void initEditText() {
        setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text_color));
        setEditTextDrawable(getText().toString());
        addTextChangedListener(new MyTextWatcher() { // 对文本内容改变进行监听

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setEditTextDrawable(s);
                if (mOnDataChangedListener != null) {
                    mOnDataChangedListener.onDataChanged(s);
                }
            }
        });
    }

    public void setOnTextChangedListener(OnDataChangedListener<CharSequence> onDataChangedListener) {
        mOnDataChangedListener = onDataChangedListener;
    }

    // 控制图片的显示
    private void setEditTextDrawable(CharSequence s) {
        if (s.length() == 0) {
            setCompoundDrawables(dLeft, null, null, null);
        } else {
            setCompoundDrawables(dLeft, null, this.dRight, null);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.dRight = null;
        super.finalize();
    }

    // 添加触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((this.dRight != null) && (event.getAction() == MotionEvent.ACTION_UP) && event.getX() > getWidth() * 0.9) {
            setText("");
        }
        return super.onTouchEvent(event);
    }

    // 设置显示的图片资源
    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        this.dLeft = left;
        if (right != null) {
            this.dRight = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }
}
