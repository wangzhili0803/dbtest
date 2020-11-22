package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.view.View;

import com.jerry.baselib.R;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 通知类型对话框, (有title)
 */
public class ProgressDialog extends BaseDialog {

    private CirclePgBar circlePgBar;

    public ProgressDialog(Context context) {
        super(context, false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_progress;
    }

    @Override
    protected void initView() {
        super.initView();
        circlePgBar = findViewById(R.id.circlePgBar);
        findViewById(R.id.confirm_tv).setVisibility(View.GONE);
    }

    public void setProgress(int progress) {
        if (circlePgBar != null) {
            circlePgBar.setProgress(progress);
        }
    }
}
