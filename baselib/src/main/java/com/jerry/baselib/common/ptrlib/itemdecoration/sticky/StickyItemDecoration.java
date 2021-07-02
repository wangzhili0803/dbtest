package com.jerry.baselib.common.ptrlib.itemdecoration.sticky;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.jerry.baselib.common.base.BaseRecyclerAdapter;

/**
 * @author Jerry
 * @createDate 2019/4/11
 * @copyright www.axiang.com
 * @description
 */
public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    public int mFirstVisiblePosition;
    //    private int mFirstCompletelyVisiblePosition;
    protected int mStickyHeadPosition;
    protected int[] mInto;

    protected RecyclerView.Adapter mAdapter;

    protected StickyHeadContainer mStickyHeadContainer;
    protected boolean mEnableStickyHead = true;
    protected DataCallback mDataCallback;

    public StickyItemDecoration(StickyHeadContainer stickyHeadContainer, DataCallback dataCallback) {
        mStickyHeadContainer = stickyHeadContainer;
        mDataCallback = dataCallback;
    }

    // 当我们调用mRecyclerView.addItemDecoration()方法添加decoration的时候，RecyclerView在绘制的时候，去会绘制decorator，即调用该类的onDraw和onDrawOver方法，
    // 1.onDraw方法先于drawChildren
    // 2.onDrawOver在drawChildren之后，一般我们选择复写其中一个即可。
    // 3.getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        checkCache(parent);

        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return;
        }

        calculateStickyHeadPosition(parent);

        if (mEnableStickyHead && mFirstVisiblePosition >= mStickyHeadPosition && mStickyHeadPosition != -1) {
            View belowView = parent.findChildViewUnder(c.getWidth() / 2, mStickyHeadContainer.getChildHeight() + 0.01f);
            mDataCallback.onDataChange(mStickyHeadContainer, mStickyHeadPosition);
            int offset;
            if (isStickyHead(parent, belowView) && belowView.getTop() > 0) {
                offset = belowView.getTop() - mStickyHeadContainer.getChildHeight();
            } else {
                offset = 0;
            }
            mStickyHeadContainer.scrollChild(offset);
        }
    }

    private void calculateStickyHeadPosition(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        // 获取第一个可见的item位置
        mFirstVisiblePosition = findFirstVisiblePosition(layoutManager);

        // 获取标签的位置，
        int stickyHeadPosition = findStickyHeadPosition(mFirstVisiblePosition);
        if (stickyHeadPosition >= 0 && mStickyHeadPosition != stickyHeadPosition) {
            // 标签位置有效并且和缓存的位置不同
            mStickyHeadPosition = stickyHeadPosition;
        }
    }

    /**
     * 从传入位置递减找出标签的位置
     */
    protected int findStickyHeadPosition(int formPosition) {

        for (int position = formPosition; position >= 0; position--) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            int type = mAdapter.getItemViewType(position);
            if (BaseRecyclerAdapter.TYPE_STICKY_HEAD == type) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 找出第一个可见的Item的位置
     */
    protected int findFirstVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int firstVisiblePosition = 0;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            mInto = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(mInto);
            firstVisiblePosition = Integer.MAX_VALUE;
            for (int pos : mInto) {
                firstVisiblePosition = Math.min(pos, firstVisiblePosition);
            }
        }
        return firstVisiblePosition;
    }

    /**
     * 检查缓存
     */
    protected void checkCache(RecyclerView parent) {

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (mAdapter != adapter) {
            mAdapter = adapter;
            // 适配器为null或者不同，清空缓存
            mStickyHeadPosition = -1;
        }
    }

    /**
     * 查找到view对应的位置从而判断出是否标签类型
     */
    private boolean isStickyHead(RecyclerView parent, View view) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        int type = mAdapter.getItemViewType(mFirstVisiblePosition + 1);
        return BaseRecyclerAdapter.TYPE_STICKY_HEAD == type;
    }

    public interface DataCallback {

        void onDataChange(StickyHeadContainer view, int pos);

    }
}
