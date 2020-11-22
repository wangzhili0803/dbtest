/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jerry.baselib.common.ptrlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.MyAnimationListener;
import com.jerry.baselib.common.util.WeakHandler;

/**
 * @author th 2015-9-23 类说明：网页加载进度条
 */
public class PageProgressView extends ImageView {

    public static final int MAX_PROGRESS = 100;
    private static final int MSG_UPDATE = 42;
    private static final int STEPS = 10;
    private static final int DELAY = 40;

    private int mCurrentProgress;
    private int mTargetProgress;
    private int mIncrement;
    private Rect mBounds;
    private WeakHandler mHandler;
    private Animation alphaAnim;

    public PageProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PageProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageProgressView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctx) {
        alphaAnim = AnimationUtils.loadAnimation(ctx, R.anim.page_progress_dismiss);
        alphaAnim.setAnimationListener(new MyAnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                setProgress(0);
            }
        });
        mBounds = new Rect(0, 0, 0, 0);
        mCurrentProgress = 0;
        mTargetProgress = 0;
        mHandler = new WeakHandler(msg -> {
            if (msg.what == MSG_UPDATE) {
                mCurrentProgress = Math.min(mTargetProgress, mCurrentProgress + mIncrement);
                mBounds.right = getWidth() * mCurrentProgress / MAX_PROGRESS;
                invalidate();
                if (mCurrentProgress < mTargetProgress) {
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE), DELAY);
                }
            }
            return false;
        });
    }

    @Override
    public void onLayout(boolean f, int l, int t, int r, int b) {
        mBounds.left = 0;
        mBounds.right = (r - l) * mCurrentProgress / MAX_PROGRESS;
        mBounds.top = 0;
        mBounds.bottom = b - t;
    }

    public void setProgress(int progress) {
        mCurrentProgress = mTargetProgress;
        mTargetProgress = progress;
        mIncrement = (mTargetProgress - mCurrentProgress) / STEPS;
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    public void dismissWithAnim() {
        clearAnimation();
        startAnimation(alphaAnim);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        Drawable d = getDrawable();
        d.setBounds(mBounds);
        d.draw(canvas);
    }

}
