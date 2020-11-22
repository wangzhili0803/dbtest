package com.jerry.baselib.common.weidgt;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.DisplayUtil;

/**
 * Created by wzl on 2016/10/24.
 *
 * @Description 对话框基类
 */

/**
 * @author Jerry
 * @createDate 2016/9/22
 * @description 基类Activity
 */
public abstract class BaseDialog extends Dialog {

    protected Context mContext;
    protected View.OnClickListener mPositiveButtonListener;
    protected View.OnClickListener mNegativeButtonListener;
    private boolean single;
    /**
     * 默认边距是交互性对话框
     */
    protected int margin = 41;
    @StringRes
    private int positiveId;
    @StringRes
    private int negativeId;

    public BaseDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
    }

    public BaseDialog(Context context, boolean isOp) {
        this(context);
        if (!isOp) {
            margin = 62;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        // 默认的样式@android:style/Theme.Dialog 对应的style 有pading属性
        try {
            Window win = getWindow();
            if (win != null) {
                win.getDecorView().setPadding(DisplayUtil.dip2px(margin), 0, DisplayUtil.dip2px(margin), 0);
                WindowManager.LayoutParams lp = win.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                win.setAttributes(lp);
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        initView();
    }

    protected abstract int getContentId();

    protected void initView() {
        TextView positiveButton = findViewById(R.id.confirm_tv);
        TextView negativeButton = findViewById(R.id.cancel_tv);
        if (positiveButton != null) {
            positiveButton.setText(positiveId == 0 ? R.string.confirm : positiveId);
            positiveButton.setOnClickListener(view -> {
                if (mPositiveButtonListener != null) {
                    mPositiveButtonListener.onClick(view);
                }
            });
        }
        if (single) {
            negativeButton.setVisibility(View.GONE);
            return;
        }
        if (negativeButton != null) {
            negativeButton.setText(negativeId == 0 ? R.string.cancel : negativeId);
            negativeButton.setOnClickListener(view -> {
                dismiss();
                if (mNegativeButtonListener != null) {
                    mNegativeButtonListener.onClick(view);
                }
            });
        }
    }

    /**
     * 只有一个按钮
     */
    public void setSingleListener(View.OnClickListener listener) {
        single = true;
        this.mPositiveButtonListener = listener;
    }

    /**
     * 设置确定按钮点击事件
     */
    public void setPositiveListener(View.OnClickListener listener) {
        this.mPositiveButtonListener = listener;
    }

    /**
     * 设置取消按钮点击事件
     */
    public void setNegativeListener(View.OnClickListener listener) {
        this.mNegativeButtonListener = listener;
    }

    public void setPositiveText(@StringRes int id) {
        positiveId = id;
    }

    public void setNegativeText(@StringRes int id) {
        negativeId = id;
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
