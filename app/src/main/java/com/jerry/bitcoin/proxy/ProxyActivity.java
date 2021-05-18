package com.jerry.bitcoin.proxy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewStub;

import com.jerry.baselib.common.base.BaseRecyclerActivity;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.MyEditText;
import com.jerry.bitcoin.R;

public class ProxyActivity extends BaseRecyclerActivity<AxUser> implements BaseRecyclerAdapter.OnItemLongClickListener {

    protected MyEditText etSearch;
    protected String phone;

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_ptrrv;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("我的代理");
        setRight("添加");
        if (UserManager.getInstance().isSuper()) {
            ViewStub vb = findViewById(R.id.vs_head);
            vb.setLayoutResource(R.layout.ll_search);
            vb.inflate();
            etSearch = findViewById(R.id.et_search);
            findViewById(R.id.btn_phone).setOnClickListener(this);
        }
        phone = UserManager.getInstance().getPhone();
    }

    @Override
    protected BaseRecyclerAdapter<AxUser> initAdapter() {
        UserItemAdapter userItemAdapter = new UserItemAdapter(this, mData);
        userItemAdapter.setOnItemLongClickListener(this);
        return userItemAdapter;
    }

    @Override
    protected void getData() {
        AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
        bmobQuery.whereEqualTo("from", phone);
        bmobQuery.orderByAscending("-tradeTime").limit(300);
        bmobQuery.findObjects(data -> {
            if (isFinishing()) {
                return;
            }
            if (data != null && data.getCode() == 0) {
                mData.clear();
                mData.addAll(data.getData());
                mPtrRecyclerView.onRefreshComplete();
                mAdapter.notifyDataSetChanged();
            }
            closeLoadingDialog();
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_right) {
            UserManager.getInstance().requestUser(user -> {
                if (UserManager.getInstance().isproxy()) {
                    AdduserDialog adduserDialog = new AdduserDialog(this);
                    adduserDialog.setOnDataChangedListener(data -> reload());
                    adduserDialog.show();
                } else {
                    ToastUtil.showShortText("您不是代理用户 找客服小哥哥申请哦");
                }
            });
        } else {
            String temp = etSearch.getText().toString().trim();
            if (temp.length() != 11) {
                ToastUtil.showShortText("手机号格式错误");
                return;
            }
            phone = temp;
            reload();
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
        AxUser user = mData.get(position);
        AdduserDialog adduserDialog = new AdduserDialog(this);
        adduserDialog.setPhonePassWd(user.getPhone(), user.getPasswd());
        adduserDialog.setUser(user);
        adduserDialog.setOnDataChangedListener(data -> reload());
        adduserDialog.show();
    }

    @Override
    public void onItemLongClick(View itemView, int position) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null) {
            toast("复制失败");
            return;
        }
        ClipData mClipData = ClipData.newPlainText("Label", mData.get(position).getPasswd());
        //将ClipData内容放到系统剪贴板里
        cm.setPrimaryClip(mClipData);
        toast("激活码已复制");
    }
}
