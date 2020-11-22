package com.jerry.baselib.common.ptrlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jerry.baselib.common.ptrlib.OnRefreshListener;
import com.jerry.baselib.common.ptrlib.PtrDefaultHandler;
import com.jerry.baselib.common.ptrlib.PtrFrameLayout;
import com.jerry.baselib.common.ptrlib.header.PtrSimpleHeader;
import com.jerry.baselib.common.util.WeakHandler;

/**
 * Created by th on 16/9/6. 类说明:PtrSimpleView刷新封装
 */
public class PtrSimpleView extends PtrFrameLayout {

    public static final int REFRESH_LOADING_TIME = 300;

    private WeakHandler mWeakHandler;
    private PtrDefaultHandler mDefaultHandler;
    public boolean canRefresh = true;

    public PtrSimpleView(Context context) {
        this(context, null);
    }

    public PtrSimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        PtrSimpleHeader mPtrSimpleHeader = new PtrSimpleHeader(getContext());
        mDefaultHandler = new PtrDefaultHandler(){

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canRefresh && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        };
        setHeaderView(mPtrSimpleHeader);
        addPtrUIHandler(mPtrSimpleHeader);
        setPtrHandler(mDefaultHandler);
    }

    public void onRefreshComplete() {
        if (mWeakHandler == null) {
            mWeakHandler = new WeakHandler();
        }
        mWeakHandler.postDelayed((Runnable) this::refreshComplete, REFRESH_LOADING_TIME);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mDefaultHandler.setOnRefreshListener(listener);
    }
}
