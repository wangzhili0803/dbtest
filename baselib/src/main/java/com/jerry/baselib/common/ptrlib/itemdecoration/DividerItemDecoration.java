package com.jerry.baselib.common.ptrlib.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jerry.baselib.R;

/**
 * @author Jerry
 * @createDate 2019/4/11
 * @copyright www.aniu.tv
 * @description RecyclerView list分割线
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int mOrientation;
    private int mDividerSpace = 1;
    private int mTopSpace;

    public DividerItemDecoration(Context context) {
        this(context, LinearLayoutManager.VERTICAL);
    }

    private DividerItemDecoration(Context context, int orientation) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(ContextCompat.getColor(context, R.color.gray_line_color_primary));
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            orientation = LinearLayoutManager.VERTICAL;
        }
        mOrientation = orientation;
    }

    public void setDividerSpace(int dividerHeight) {
        mDividerSpace = dividerHeight;
    }

    public void setTopSpace(int topSpace) {
        mTopSpace = topSpace;
    }

    public void setDividerColor(int dividerColor) {
        mPaint.setColor(dividerColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + mDividerSpace;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDividerSpace;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (mTopSpace != 0 && parent.getChildAdapterPosition(view) == 0) {// 第一个item顶部距离
                outRect.set(0, mTopSpace, 0, mDividerSpace);
            } else {
                outRect.set(0, 0, 0, mDividerSpace);
            }
        } else {
            outRect.set(0, 0, mDividerSpace, 0);
        }
    }
}
