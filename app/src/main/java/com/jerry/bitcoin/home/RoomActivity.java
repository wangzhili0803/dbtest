package com.jerry.bitcoin.home;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseRecyclerActivity;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.MyEditText;
import com.jerry.bitcoin.R;
import com.jerry.baselib.common.bean.ScriptWord;

public class RoomActivity extends BaseRecyclerActivity<ScriptWord> {

    private String roomId;
    private MyEditText etSrict;

    @Override
    protected void beforeViews() {
        roomId = getIntent().getStringExtra(Key.DATA);
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_room;
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(roomId);
        TextView tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.run_link);
        tvRight.setOnClickListener(this);
        etSrict = findViewById(R.id.et_srict);
        findViewById(R.id.tv_send).setOnClickListener(this);
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
        new AVObjQuery<>(ScriptWord.class).whereContains("roomId", roomId)
            .whereContains("userId", UserManager.getInstance().getPhone()).orderByAscending("-createdAt").findObjects(data -> {
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
            if (UserManager.getInstance().isLogined()) {
                AxUser user = UserManager.getInstance().getUser();
                user.setLiveRoom(roomId);
                user.update(data -> toast("激活成功"));
            }
            setResult(Activity.RESULT_OK);
        } else if (v.getId() == R.id.tv_send) {
            // 新建房间
            ScriptWord roomBean = new ScriptWord();
            roomBean.setRoomId(roomId);
            roomBean.setUserId(UserManager.getInstance().getPhone());
            roomBean.setDesc(etSrict.getText().toString());
            roomBean.save(data1 -> {
                etSrict.setText(Key.NIL);
                mData.add(0, roomBean);
                mAdapter.notifyItemRangeInserted(0, 1);
                LogUtils.d("添加成功");
                toast("添加成功");
            });
        }
        super.onClick(v);
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
