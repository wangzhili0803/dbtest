package com.jerry.baselib.common.ptrlib.header;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.ptrlib.PtrFrameLayout;
import com.jerry.baselib.common.ptrlib.PtrUIHandler;
import com.jerry.baselib.common.ptrlib.RefreshingView;
import com.jerry.baselib.common.ptrlib.indicator.PtrIndicator;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:header封装
 */
public class PtrSimpleHeader extends LinearLayout implements PtrUIHandler {

    RefreshingView mHeaderImage;
    TextView mHeaderText;
    CharSequence mPullLabel;
    CharSequence mRefreshingLabel;
    CharSequence mReleaseLabel;
    CharSequence mCompleteLabel;

    public PtrSimpleHeader(Context context) {
        this(context, null);
    }

    public PtrSimpleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    protected void initViews(Context context) {
        inflateView(context);
        mHeaderText = findViewById(R.id.pull_to_refresh_text);
        mHeaderImage = findViewById(R.id.loading_image);
        setStageText(context);
        resetView();
    }

    void inflateView(final Context context) {
        View.inflate(context, R.layout.pull_to_refresh_header_vertical, this);
    }

    void setStageText(final Context context) {
        mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
        mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
        mCompleteLabel = context.getString(R.string.pull_to_refresh_complete);
    }

    private void resetView() {
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.stop();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetView();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mHeaderText.setText(mPullLabel);
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.stop();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mHeaderText.setText(mRefreshingLabel);
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mHeaderImage.setVisibility(View.INVISIBLE);
        mHeaderImage.stop();
        mHeaderText.setText(mCompleteLabel);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();
        // System.out.println(mOffsetToRefresh + ",," + currentPos + ",," + lastPos);

        float scaleOfLayout = 1;
        if (currentPos > 0 && currentPos < mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                scaleOfLayout = 1 - currentPos / mOffsetToRefresh;
                scaleOfLayout *= 0.65f;
            }
        }
        mHeaderImage.setAlpha(scaleOfLayout);

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch();
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch();
            }
        }
    }

    private void crossRotateLineFromTopUnderTouch() {
        mHeaderText.setVisibility(VISIBLE);
        mHeaderText.setText(mReleaseLabel);
    }

    private void crossRotateLineFromBottomUnderTouch() {
        mHeaderText.setVisibility(VISIBLE);
        mHeaderText.setText(mPullLabel);
    }
}
