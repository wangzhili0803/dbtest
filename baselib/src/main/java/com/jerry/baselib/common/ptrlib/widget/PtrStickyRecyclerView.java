package com.jerry.baselib.common.ptrlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jerry.baselib.R;
import com.jerry.baselib.common.ptrlib.PtrDefaultHandler;
import com.jerry.baselib.common.ptrlib.PtrFrameLayout;
import com.jerry.baselib.common.ptrlib.itemdecoration.sticky.StickyHeadContainer;
import com.jerry.baselib.common.ptrlib.itemdecoration.sticky.StickyItemDecoration;
import com.jerry.baselib.common.ptrlib.itemdecoration.sticky.TimeStickyItemDecoration;

/**
 * @author Jerry
 * @createDate 2019/4/11
 * @copyright www.axiang.com
 * @description RecyclerView list分割线
 */
public class PtrStickyRecyclerView extends PtrRecyclerView {

    private StickyHeadContainer stickyHeadContainer;

    public PtrStickyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public PtrStickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        stickyHeadContainer = findViewById(R.id.stickyHeadContainer);
    }

    @Override
    protected int getPtrLayoutId() {
        return R.layout.refresh_sticky_recyclerview;
    }

    @NonNull
    @Override
    protected PtrDefaultHandler initPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (content.getTop() > 0 || mAdapter == null || mAdapter.getRealItemCount() == 0) {
                    stickyHeadContainer.setVisibility(GONE);
                } else {
                    stickyHeadContainer.setVisibility(VISIBLE);
                }
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        };
    }

    public void addItemDecoration(View view, StickyItemDecoration.DataCallback dataCallback) {
        if (stickyHeadContainer.getChildCount() == 0) {
            stickyHeadContainer.addView(view);
        }
        StickyItemDecoration stickyItemDecoration = new StickyItemDecoration(stickyHeadContainer, dataCallback);
        mRecyclerView.addItemDecoration(stickyItemDecoration);
    }

    public void addTimeItemDecoration(View view, StickyItemDecoration.DataCallback dataCallback) {
        if (stickyHeadContainer.getChildCount() == 0) {
            stickyHeadContainer.addView(view);
        }
        TimeStickyItemDecoration stickyItemDecoration = new TimeStickyItemDecoration(stickyHeadContainer, dataCallback);
        mRecyclerView.addItemDecoration(stickyItemDecoration);
    }

    @Override
    public void onRefreshComplete() {
        super.onRefreshComplete();
        if (mAdapter != null && mAdapter.getRealItemCount() == 0) {
            stickyHeadContainer.setVisibility(GONE);
        }
    }
}
