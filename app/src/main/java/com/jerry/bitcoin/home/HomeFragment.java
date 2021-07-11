package com.jerry.bitcoin.home;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.BaseRecyclerFragment;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.EditDialog;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.bean.RoomBean;
import com.jerry.bitcoin.interfaces.LoginActionListener;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseRecyclerFragment<RoomBean> {

    private static final int TO_ROOM = 101;
    private LoginActionListener mLoginActionListener;

    @Override
    public void onAttach(@NotNull final Context context) {
        super.onAttach(context);
        mLoginActionListener = (LoginActionListener) context;
    }

    @Override
    protected BaseRecyclerAdapter<RoomBean> initAdapter() {
        return new BaseRecyclerAdapter<RoomBean>(mActivity, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_title;
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
        tvRight.setText(R.string.new_link);
        tvRight.setOnClickListener(this);
    }

    @Override
    protected void getData() {
        if (UserManager.getInstance().isLogined()) {
            new AVObjQuery<>(RoomBean.class).whereContains("userIds", UserManager.getInstance().getPhone()).orderByAscending("-createdAt")
                .findObjects(data -> {
                    if (data == null || data.getCode() == 1) {
                        LogUtils.e(data.getMsg());
                        ToastUtil.showShortText(data.getMsg());
                        return;
                    }
                    mData.clear();
                    mData.addAll(data.getData());
                    mAdapter.notifyDataSetChanged();
                    onAfterRefresh();
                });
        } else {
            mData.clear();
            mAdapter.notifyDataSetChanged();
            onAfterRefresh();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_right) {
            if (!UserManager.getInstance().isLogined()) {
                if (mLoginActionListener != null) {
                    mLoginActionListener.showLogin();
                }
                return;
            }
            UserManager.getInstance().requestUser(data -> {
                int count = UserManager.getInstance().getUser().getRoomCount();
                if (mData.size() >= count) {
                    toast("您的账户只能建" + count + "个链接，想增加请联系客服小哥哥");
                    return;
                }
                showNewLinkDialog();
            });
        }
    }

    private void showNewLinkDialog() {
        EditDialog editDialog = new EditDialog(mActivity);
        editDialog.setDialogTitle(getString(R.string.new_link));
        editDialog.setPositiveListener(view -> {
            String roomId = editDialog.getEditText();
            new AVObjQuery<>(RoomBean.class).whereEqualTo("roomId", roomId).findObjects(data -> {
                if (data == null || data.getCode() == 1) {
                    LogUtils.e(data.getMsg());
                    ToastUtil.showShortText(data.getMsg());
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
                        if (TextUtils.isEmpty(user.getLiveRoom())) {
                            user.setLiveRoom(roomId);
                            user.update(data2 -> {
                                mData.add(0, roomBean);
                                mAdapter.notifyItemRangeInserted(0, 1);
                                editDialog.dismiss();
                                LogUtils.d("添加成功");
                                toast("添加成功");
                            });
                        } else {
                            mData.add(0, roomBean);
                            mAdapter.notifyItemRangeInserted(0, 1);
                            editDialog.dismiss();
                            LogUtils.d("添加成功");
                            toast("添加成功");
                        }
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
                    AxUser user = UserManager.getInstance().getUser();
                    if (TextUtils.isEmpty(user.getLiveRoom())) {
                        user.setLiveRoom(roomId);
                        user.update(data2 -> {
                            mData.add(0, roomBean);
                            mAdapter.notifyItemRangeInserted(0, 1);
                            editDialog.dismiss();
                            LogUtils.d("添加成功");
                            toast("添加成功");
                        });
                    } else {
                        mData.add(0, roomBean);
                        mAdapter.notifyItemRangeInserted(0, 1);
                        editDialog.dismiss();
                        LogUtils.d("添加成功");
                        toast("添加成功");
                    }
                });
            });
        });
        editDialog.show();
    }

    @Override
    public void onItemClick(final View itemView, final int position) {
        RoomBean roomBean = mData.get(position);
        DateUtils.getNowTimeLong(data -> {
            if (data < DateUtils.getLongByDateTime(roomBean.getExpire())) {
                toast("改链接已过使用期");
                return;
            }
            Intent intent = new Intent(mActivity, RoomActivity.class);
            intent.putExtra(Key.DATA, mData.get(position).getRoomId());
            startActivityForResult(intent, TO_ROOM);
        });
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
