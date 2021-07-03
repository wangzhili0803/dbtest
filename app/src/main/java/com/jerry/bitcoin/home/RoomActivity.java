package com.jerry.bitcoin.home;

import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseRecyclerActivity;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.weidgt.EditDialog;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.beans.ScriptWord;

public class RoomActivity extends BaseRecyclerActivity<ScriptWord> {

    private String roomId;

    @Override
    protected void beforeViews() {
        roomId = getIntent().getStringExtra(Key.DATA);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.room);
        TextView tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.new_script);
        tvRight.setOnClickListener(this);

    }

    @Override
    protected BaseRecyclerAdapter<ScriptWord> initAdapter() {
        return new BaseRecyclerAdapter<ScriptWord>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final ScriptWord bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean.getDesc());
            }
        };
    }

    @Override
    protected void getData() {
        new AVObjQuery<>(ScriptWord.class).whereContains("roomId", roomId).orderByAscending("-createdAt").findObjects(data -> {
            if (data == null || data.getCode() == 1) {
                ToastUtil.showShortText("台词查询失败");
                return;
            }
            mData.clear();
            mData.addAll(data.getData());
            mAdapter.notifyDataSetChanged();
            onAfterRefresh();
        });
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_right) {
            EditDialog editDialog = new EditDialog(this);
            editDialog.setPositiveListener(view -> {
                // 新建房间
                ScriptWord roomBean = new ScriptWord();
                roomBean.setRoomId(roomId);
                roomBean.setDesc(editDialog.getEditText());
                roomBean.save(data1 -> {
                    mData.add(0, roomBean);
                    mAdapter.notifyItemRangeInserted(0, 1);
                    editDialog.dismiss();
                    LogUtils.d("添加成功");
                    toast("添加成功");
                });
            });
            editDialog.show();
        }
        super.onClick(v);
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
