package com.jerry.baselib.common.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jerry.baselib.R;
import com.jerry.baselib.common.ptrlib.OnLoadMoreListener;
import com.jerry.baselib.common.ptrlib.widget.PtrRecyclerView;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.NetworkUtil;

public abstract class BaseRecyclerFragment<T> extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    protected int pageSize = 10;
    protected boolean canLoadMore = true;
    protected int page;
    protected PtrRecyclerView mPtrRecyclerView;
    protected BaseRecyclerAdapter<T> mAdapter;
    protected List<T> mData = new ArrayList<>();

    protected OnLoadMoreListener mLoadMoreListener = () -> {
        if (canLoadMore) {
            page++;
            getData();
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mAdapter = initAdapter();
        mAdapter.setOnItemClickListener(this);
    }

    protected abstract BaseRecyclerAdapter<T> initAdapter();

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fg_recycler_with_viewstub;
    }

    @Override
    protected void initView(View view) {
        mPtrRecyclerView = view.findViewById(R.id.ptrRecyclerView);
        mPtrRecyclerView.setAdapterOnLoadMore(mAdapter, mLoadMoreListener);
        mPtrRecyclerView.setOnRefreshListener(() -> {
            onPreRefresh();
            reload();
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
    }

    @Override
    public void onHiddenChanged(final boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isCreateView) {
            lazyLoad();
        }
    }

    protected void lazyLoad() {
        if (mAdapter != null && !CollectionUtils.isEmpty(mData)) {
            mAdapter.notifyDataSetChanged();
        } else if (canLoadMore) {
            if (NetworkUtil.isNetworkAvailable() && mData.size() == 0) {
                loadingDialog();
            }
            getData();
        }
    }

    /**
     * 刷新前还原排序
     */
    protected void onPreRefresh() {
    }

    /**
     * 从网络端获取数据
     */
    protected abstract void getData();

    @Override
    public void reload() {
        canLoadMore = true;
        page = 0;
        getData();
    }

    @Override
    public void onClick(final View v) {

    }

    protected void onAfterRefresh() {
        mAdapter.notifyDataSetChanged();
        closeLoadingDialog();
        mPtrRecyclerView.onRefreshComplete();
    }
}
