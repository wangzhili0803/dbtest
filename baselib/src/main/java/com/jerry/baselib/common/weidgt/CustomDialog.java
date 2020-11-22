package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jerry.baselib.R;

/**
 * @author th 2015-4-16 类说明：自定义对话框基类
 */
public class CustomDialog extends BaseDialog {

    protected TextView mMessageView;

    public CustomDialog(Context context) {
        super(context, false);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_custom;
    }

    @Override
    protected void initView() {
        super.initView();
        mMessageView = findViewById(R.id.msg_tv);
    }

    public void setMessage(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (null != mMessageView) {
            mMessageView.setText(message);
        }
    }

    public void setMessage(int resId) {
        if (null != mMessageView) {
            mMessageView.setText(resId);
        }
    }
}
