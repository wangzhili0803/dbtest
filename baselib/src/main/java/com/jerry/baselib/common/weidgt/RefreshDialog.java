package com.jerry.baselib.common.weidgt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.ptrlib.RefreshingView;

public class RefreshDialog extends Dialog {

    private Context mContext;
    private RefreshingView mImageView;
    private TextView mLoadingTv;
    private String mLoadingText;

    public RefreshDialog(Context context) {
        super(context, R.style.refresh_dialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_refresh);
        setCanceledOnTouchOutside(false);
        mImageView = findViewById(R.id.loading_image);
        mLoadingTv = findViewById(R.id.tv_loading);
    }

    public void setLoadingText(String loadingText) {
        mLoadingText = loadingText;
    }

    @Override
    public void show() {
        if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        dismiss();
        super.show();
        mImageView.post(() -> mImageView.start());
        mLoadingTv.setText(TextUtils.isEmpty(mLoadingText) ? mContext.getString(R.string.loading) : mLoadingText);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            if (mImageView != null) {
                mImageView.stop();
            }
            super.dismiss();
        }
    }
}
