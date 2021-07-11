package com.jerry.bitcoin.home;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jerry.baselib.ActionCode;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.base.BaseRecyclerActivity;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.bean.ScriptWord;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.MyEditText;
import com.jerry.baselib.parsehelper.TxtManager;
import com.jerry.bitcoin.R;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        super.initView();
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(roomId);
        TextView tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.run_link);
        tvRight.setOnClickListener(this);
        etSrict = findViewById(R.id.et_srict);
        findViewById(R.id.tv_txt).setOnClickListener(this);
        findViewById(R.id.tv_send).setOnClickListener(this);
    }

    @Override
    protected BaseRecyclerAdapter<ScriptWord> initAdapter() {
        return new BaseRecyclerAdapter<ScriptWord>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_title;
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
        } else if (v.getId() == R.id.tv_txt) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.setType("text/plain");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, ActionCode.TO_TXT);
        }
        super.onClick(v);
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Uri uri;
        switch (requestCode) {
            case ActionCode.TO_TXT:
            default:
                if (data != null) {
                    uri = data.getData();
                    if (uri != null) {
                        String path = FileUtil.getPath(uri);
                        AppTask.withoutContext().assign(() -> TxtManager.readFile(path)).whenDone(new WhenTaskDone<List<String>>() {
                            @Override
                            public void whenDone(final List<String> result) {

                            }
                        }).execute();

                        List<String> urls = TxtManager.readFile(path);
                        if (!CollectionUtils.isEmpty(urls)) {
                            List<ScriptWord> scriptWords = new ArrayList<>();
                            for (String url : urls) {
                                ScriptWord roomBean = new ScriptWord();
                                roomBean.setRoomId(roomId);
                                roomBean.setUserId(UserManager.getInstance().getPhone());
                                roomBean.setDesc(url);
                                scriptWords.add(roomBean);
                            }
                            AVObjQuery.saveAll(scriptWords, data1 -> reload());
                        }
                        return;
                    }
                }
                toast("数据导入失败！");
                break;
        }
    }
}
