package com.jerry.baselib.common.weidgt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jerry.baselib.common.util.DisplayUtil;

/**
 * Created by Jerry on 2015/8/5 0005.
 */
public class CirclePgBar extends View {

    private Paint mFrontPaint;
    private Paint mTextPaint;
    private float mRadius;
    private RectF mRect;
    private int mProgress;
    private int mWidth;
    private int mHeight;


    public CirclePgBar(Context context) {
        super(context);
        init();
    }

    public CirclePgBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePgBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //完成相关参数初始化
    private void init() {
        mFrontPaint = new Paint();
        mFrontPaint.setColor(Color.BLUE);
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.STROKE);
        mFrontPaint.setStrokeWidth(2);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DisplayUtil.getDisplayDensity() * 12);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }


    //重写测量大小的onMeasure方法和绘制View的核心方法onDraw()
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getRealSize(widthMeasureSpec);
        mHeight = getRealSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        mRadius = (Math.min(mWidth, mHeight) >> 1) - 5;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        initRect();
        int max = 100;
        float angle = mProgress / (float) max * 360;
        canvas.drawArc(mRect, -90, angle, false, mFrontPaint);
        String text = mProgress + "%";
        canvas.drawText(text, mWidth >> 1, mHeight >> 1, mTextPaint);
    }

    public int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            //自己计算
            result = (int) (mRadius * 2 + 50);
        } else {
            result = size;
        }
        return result;
    }

    public void setProgress(final int progress) {
        mProgress = progress;
        invalidate();
    }

    private void initRect() {
        if (mRect == null) {
            mRect = new RectF();
            int viewSize = (int) (mRadius * 2);
            int left = (mWidth - viewSize) / 2;
            int top = (mHeight - viewSize) / 2;
            int right = left + viewSize;
            int bottom = top + viewSize;
            mRect.set(left, top, right, bottom);
        }
    }
}