package com.jerry.bitcoin.home;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.BaseRecyclerFragment;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.EditDialog;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.beans.RoomBean;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseRecyclerFragment<RoomBean> {

    private static final int TO_ROOM = 101;

    @Override
    protected BaseRecyclerAdapter<RoomBean> initAdapter() {
        return new BaseRecyclerAdapter<RoomBean>(mActivity, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final RoomBean bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean.getRoomId());
                textView.setSelected(bean.getRoomId().equals(UserManager.getInstance().getUser().getLiveRoom()));
            }
        };
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        super.initView(view);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.app_name);
        TextView tvRight = view.findViewById(R.id.tv_right);
        tvRight.setText(R.string.new_room);
        tvRight.setOnClickListener(this);
    }

    @Override
    protected void getData() {
        if (UserManager.getInstance().isLogined()) {
            new AVObjQuery<>(RoomBean.class).whereContains("userIds", UserManager.getInstance().getPhone()).findObjects(data -> {
                if (data == null || data.getCode() == 1) {
                    ToastUtil.showShortText("房间查询失败");
                    return;
                }
                mData.clear();
                mData.addAll(data.getData());
                mAdapter.notifyDataSetChanged();
                onAfterRefresh();
            });
        } else {
            onAfterRefresh();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_right) {
            EditDialog editDialog = new EditDialog(mActivity);
            editDialog.setPositiveListener(view -> {
                String roomId = editDialog.getEditText();
                new AVObjQuery<>(RoomBean.class).whereEqualTo("roomId", roomId).findObjects(data -> {
                    if (data == null || data.getCode() == 1) {
                        ToastUtil.showShortText("房间查询失败");
                        return;
                    }
                    List<RoomBean> list = data.getData();
                    if (CollectionUtils.isEmpty(list)) {
                        // 新建房间
                        RoomBean roomBean = new RoomBean();
                        roomBean.setRoomId(roomId);
                        roomBean.setUserIds(Collections.singletonList(UserManager.getInstance().getPhone()));
                        roomBean.save(data1 -> {
                            AxUser user = UserManager.getInstance().getUser();
                            user.setLiveRoom(roomId);
                            user.update(data2 -> {
                                mData.add(0, roomBean);
                                mAdapter.notifyItemRangeInserted(0, 1);
                                editDialog.dismiss();
                                LogUtils.d("添加成功");
                                toast("添加成功");
                            });
                        });
                        return;
                    }
                    // 房间添加用户
                    RoomBean roomBean = list.get(0);
                    List<String> userIds = roomBean.getUserIds();
                    if (userIds.contains(UserManager.getInstance().getPhone())) {
                        LogUtils.d("已经添加过该房间");
                        toast("已经添加过该房间");
                        return;
                    }
                    userIds.add(UserManager.getInstance().getPhone());
                    roomBean.setUserIds(userIds);
                    roomBean.update(data1 -> {
                        mData.add(0, roomBean);
                        mAdapter.notifyItemRangeInserted(0, 1);
                        editDialog.dismiss();
                        LogUtils.d("更新成功");
                        toast("添加成功");
                    });
                });
            });
            editDialog.show();
        }
    }

    @Override
    public void onItemClick(final View itemView, final int position) {
        Intent intent = new Intent(mActivity, RoomActivity.class);
        intent.putExtra(Key.DATA, mData.get(position).getRoomId());
        startActivityForResult(intent, TO_ROOM);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }
}
