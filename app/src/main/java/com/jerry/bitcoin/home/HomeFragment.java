package com.jerry.bitcoin.home;

import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.BaseRecyclerFragment;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.DyUser;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.greendao.DyUserDao.Properties;
import com.jerry.bitcoin.R;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseRecyclerFragment<DyUser> {

    @Override
    protected BaseRecyclerAdapter<DyUser> initAdapter() {
        return new BaseRecyclerAdapter<DyUser>(mActivity, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_dyuser;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final DyUser bean) {
                TextView tvName = holder.getView(R.id.tv_name);
                TextView tvDyid = holder.getView(R.id.tv_dyid);
                TextView tvPraise = holder.getView(R.id.tv_praise);
                TextView tvFollow = holder.getView(R.id.tv_follow);
                TextView tvFans = holder.getView(R.id.tv_fans);
                tvName.setText(bean.getName());
                tvDyid.setText(bean.getDyId());
                tvPraise.setText(getString(R.string.praise_text, bean.getPraise()));
                tvFollow.setText(getString(R.string.follow_text, bean.getFollow()));
                tvFans.setText(getString(R.string.fans_text, bean.getFans()));
            }
        };
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        mPtrRecyclerView = view.findViewById(R.id.ptrRecyclerView);
        mPtrRecyclerView.setAdapter(mAdapter);
        mPtrRecyclerView.setOnRefreshListener(() -> reload());
    }

    @Override
    protected void getData() {
        AppTask.withoutContext().assign(() -> ProManager.getInstance().queryAll(DyUser.class, null, Properties.UpdateTime))
            .whenDone((WhenTaskDone<List<DyUser>>) result -> {
                mData.clear();
                mData.addAll(result);
                mAdapter.notifyDataSetChanged();
            }).whenTaskEnd(this::onAfterRefresh).execute();
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
