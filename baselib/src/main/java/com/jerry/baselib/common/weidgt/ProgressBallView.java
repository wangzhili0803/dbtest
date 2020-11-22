package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jerry.baselib.R;

/**
 * @author Jerry
 * @createDate 2020/10/19
 * @description
 */
public class ProgressBallView extends FrameLayout {

    private ProgressBall progressBall;
    private TextView tvProgress;

    public ProgressBallView(final Context context) {
        this(context, null);
    }

    public ProgressBallView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBallView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.progress_view, this);
        tvProgress = findViewById(R.id.progress_tv);
        progressBall = findViewById(R.id.progressBall);
    }


    public synchronized void setProgress(String progress) {
        tvProgress.setText(progress);
        progressBall.setProgress(progress);
    }

    public synchronized void setProgress(int progress) {
        tvProgress.setText(String.valueOf(progress));
        progressBall.setProgress(progress);
    }
}
