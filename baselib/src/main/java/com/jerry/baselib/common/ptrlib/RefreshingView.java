package com.jerry.baselib.common.ptrlib;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.DisplayUtil;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:刷新动画View
 */
public class RefreshingView extends View {

    private int w;
    private int h;
    private Paint mPaint1;
    private Paint mPaint2;
    private long time;
    private static final int BLOCK = 800;
    private boolean mAnimating;

    public RefreshingView(Context context) {
        this(context, null);
    }

    public RefreshingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint1 = new Paint(1);
        mPaint2 = new Paint(1);
        mPaint1.setColor(ContextCompat.getColor(context, R.color.red_primary));
        mPaint2.setColor(ContextCompat.getColor(context, R.color.blue_primary));
        w = DisplayUtil.dip2px(10);
        h = DisplayUtil.dip2px(10);
        time = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimating) {
            canvas.translate(w / 2, 0.0F);
            int i = getWidth() - this.w;
            int j = getHeight();
            int k = (int) ((SystemClock.elapsedRealtime() - time) % BLOCK);
            int diff = i * (k % (BLOCK / 2)) / (BLOCK / 2);
            int m = (int) (Math.sin(2 * Math.PI * k / BLOCK) * h / 4.0D) + h / 2;
            int n = h - m;
            if (k > BLOCK / 2) {
                diff = i - diff;
            }
            if (m > n) {
                canvas.drawCircle(i - diff, j / 2, n, mPaint1);
                canvas.drawCircle(diff, j / 2, m, mPaint2);
            } else {
                canvas.drawCircle(diff, j / 2, m, mPaint2);
                canvas.drawCircle(i - diff, j / 2, n, mPaint1);
            }
            postInvalidateDelayed(10L);
        } else {
            canvas.drawCircle(w, h, h / 2, mPaint1);
            canvas.drawCircle(2 * w + 2, h, h / 2, mPaint2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(3 * w, 2 * h);
    }

    public void start() {
        mAnimating = true;
        invalidate();
    }

    public void stop() {
        mAnimating = false;
        invalidate();
    }

}