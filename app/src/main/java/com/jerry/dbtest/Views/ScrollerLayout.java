package com.jerry.dbtest.Views;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by wzl-pc on 2017/2/22.
 */

public class ScrollerLayout extends ViewGroup {

    private final int mTouchSlop;
    private Scroller scroller;

    //左边界和右边界
    private int leftBorder;
    private int rightBorder;
    private float xLastMove;
    private float xMove;

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //创建scroller的实例
        scroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                child.layout(i * child.getMeasuredWidth(), 0, (i + 1) * child.getMeasuredWidth(), child.getMeasuredHeight());
            }

            //获取边界值
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(count - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xLastMove = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = ev.getRawX();
                float diff = Math.abs(xMove - xLastMove);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float scrollX = xLastMove - xMove;
                if (getScrollX() + scrollX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + getWidth() + scrollX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy((int) scrollX, 0);
                xLastMove = xMove;
                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
                int dx = targetIndex * getWidth() - getScrollX();
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                scroller.startScroll(getScrollX(), 0, dx, 0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
