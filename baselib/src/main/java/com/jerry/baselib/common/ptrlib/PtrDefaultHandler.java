package com.jerry.baselib.common.ptrlib;

import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

public class PtrDefaultHandler implements PtrHandler {

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(final OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public PtrDefaultHandler() {
    }

    public PtrDefaultHandler(final OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    @Override
    public void onRefreshBegin(final PtrFrameLayout frame) {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    private static boolean canChildScrollUp(View view) {
        if (view instanceof ListView) {// add
            final ListView absListView = (ListView) view;
            if (absListView.getChildCount() == absListView.getHeaderViewsCount()) {
                return true;
            }
        } else if (view instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) view;
            if (recyclerView.getChildCount() == 0) {
                return true;
            }
        }

        return view.canScrollVertically(-1);
    }

    /**
     * Default implement for check can perform pull to refresh
     */
    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
}