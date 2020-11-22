package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.ParseUtil;

/**
 * @author Jerry
 * @createDate 2020/10/19
 * @description 进度球
 */
public class ProgressBall extends View {

    private Paint mPaint = new Paint(1);
    private static final int DEFAULT_VIEW_SIZE = 90;
    private int mViewSize;
    private float mRadius;

    private int circleBg;
    private int strokeColor;
    private int strokeWidth;
    private int mProgress = 30;

    public ProgressBall(Context context) {
        super(context);
        init(context);
    }

    public ProgressBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint.setColor(ContextCompat.getColor(context, R.color.unite_366899));
        circleBg = ContextCompat.getColor(context, R.color.unite_e9f0f6);
        strokeColor = ContextCompat.getColor(context, R.color.blue_second);
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.one_dp);
    }

    public void setColor(int fillColor, int borderColor) {
        this.circleBg = ContextCompat.getColor(getContext(), fillColor);
        this.strokeColor = ContextCompat.getColor(getContext(), borderColor);
        invalidate();
    }

    public synchronized void setProgress(String progress) {
        this.mProgress = Math.min(ParseUtil.parseInt(progress), 100);
        invalidate();
    }

    public synchronized void setProgress(int progress) {
        this.mProgress = Math.min(progress, 100);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // 截取部分canvas画实心圆
        mPaint.setColor(circleBg);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.clipRect(0, mViewSize * (1 - (float) mProgress / 100), mViewSize, mViewSize);
        canvas.drawCircle(mRadius, mRadius, mRadius - strokeWidth, mPaint);
        canvas.restore();

        // 画空心圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(strokeColor);
        canvas.drawCircle(mRadius, mRadius, mRadius - strokeWidth, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(DEFAULT_VIEW_SIZE, widthMeasureSpec);
        int height = resolveSize(DEFAULT_VIEW_SIZE, heightMeasureSpec);
        mViewSize = Math.min(width, height);
        mRadius = (float) (mViewSize / 2);
        setMeasuredDimension(width, height);
    }

}
