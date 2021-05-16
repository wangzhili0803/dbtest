package com.jerry.baselib.common.ptrlib.itemdecoration.sticky;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.jerry.baselib.common.util.DisplayUtil;

/**
 * @author Jerry
 * @createDate 2019/4/11
 * @copyright www.axiang.com
 * @description
 */
public class StickyHeadContainer extends ViewGroup {

    private int mOffset;
    private int mLastOffset = Integer.MIN_VALUE;
    private SparseArray<View> views = new SparseArray<>();

    public StickyHeadContainer(Context context) {
        this(context, null);
    }

    public StickyHeadContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyHeadContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desireHeight;
        int desireWidth;

        int count = getChildCount();

        if (count == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        } else if (count > 1) {
            throw new IllegalArgumentException("只允许容器添加1个子View！");
        }

        View child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        LayoutParams params = child.getLayoutParams();
        if (params.width == 0 || params.width == LayoutParams.MATCH_PARENT) {
            ViewGroup parent = (ViewGroup) getParent();
            desireWidth = parent == null ? DisplayUtil.getDisplayWidth() : parent.getMeasuredWidth();
        } else {
            desireWidth = params.width;
        }
        // 计算子元素高度
        desireHeight = child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
        // 设置最终测量值
        setMeasuredDimension(resolveSize(desireWidth, widthMeasureSpec), resolveSize(desireHeight, heightMeasureSpec));
        params.width = desireWidth;
        child.setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);

            final int paddingLeft = getPaddingLeft();
            final int paddingTop = getPaddingTop();

            int mRight = child.getMeasuredWidth() + paddingLeft;

            int mTop = paddingTop + mOffset;
            int mBottom = child.getMeasuredHeight() + mTop;

            child.layout(paddingLeft, mTop, mRight, mBottom);
        }
    }

    public void scrollChild(int offset) {
        if (mLastOffset != offset) {
            mOffset = offset;
            if (mLastOffset != Integer.MIN_VALUE && getChildCount() > 0) {
                ViewCompat.offsetTopAndBottom(getChildAt(0), mOffset - mLastOffset);
            }
        }
        mLastOffset = mOffset;
    }

    protected int getChildHeight() {
        return getChildCount() > 0 ? getChildAt(0).getHeight() : 0;
    }
}
