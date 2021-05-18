package com.jerry.bitcoin.proxy;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.bean.SelectBean;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.baselib.common.util.MD5;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.BaseDialog;
import com.jerry.bitcoin.R;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jerry
 * @createDate 2019-05-25
 * @copyright www.aniu.tv
 * @description
 */
public class AdduserDialog extends BaseDialog implements BaseRecyclerAdapter.OnItemClickListener {

    private String[] pate = {"一个月", "三个月", "半年", "一年", "永久"};
    private EditText etPhone;
    private CheckBox cbProxy;
    private TagItemAdapter mAdapter;
    private List<SelectBean> mData = new ArrayList<>();
    private OnDataCallback<AxUser> mOnDataChangedListener;
    private String phone;
    private String passwd;
    private AxUser mUser;

    public AdduserDialog(Context context) {
        super(context);
        mAdapter = new TagItemAdapter(mContext, mData);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_adduser;
    }

    @Override
    protected void initView() {
        super.initView();
        etPhone = findViewById(R.id.et_phone);
        etPhone.setText(phone);
        if (TextUtils.isEmpty(passwd)) {
            findViewById(R.id.tv_code).setVisibility(View.GONE);
        } else {
            TextView tvCode = findViewById(R.id.tv_code);
            tvCode.setText(passwd);
            tvCode.setVisibility(View.VISIBLE);
            tvCode.setOnLongClickListener(v -> {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm == null) {
                    ToastUtil.showShortText("复制失败");
                    return true;
                }
                ClipData mClipData = ClipData.newPlainText("Label", tvCode.getText().toString());
                //将ClipData内容放到系统剪贴板里
                cm.setPrimaryClip(mClipData);
                ToastUtil.showShortText("激活码已复制");
                return true;
            });
        }
        cbProxy = findViewById(R.id.cb_proxy);
        if (UserManager.getInstance().isproxy()) {
            cbProxy.setVisibility(UserManager.getInstance().isSuper() ? View.VISIBLE : View.GONE);
        } else {
            cbProxy.setVisibility(View.GONE);
        }
        if (mUser != null) {
            cbProxy.setChecked(mUser.getLevel() >= 10);
        }
        RecyclerView rvTags = findViewById(R.id.rv_tags);
        rvTags.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvTags.setAdapter(mAdapter);
        TextView confirm = findViewById(R.id.confirm_tv);
        confirm.setText("添加");
        confirm.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.length() != 11) {
                ToastUtil.showShortText("手机号格式错误");
                return;
            }
            int selext = -1;
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isSelected()) {
                    selext = i;
                    break;
                }
            }
            long time;
            long daytime = 1000 * 60 * 60 * 24;
            switch (mData.get(selext).getTitle()) {
                case "一天":
                    time = System.currentTimeMillis() + daytime;
                    break;
                case "一个月":
                    time = System.currentTimeMillis() + daytime * 30;
                    break;
                case "三个月":
                    time = System.currentTimeMillis() + daytime * 92;
                    break;
                case "半年":
                    time = System.currentTimeMillis() + daytime * 183;
                    break;
                case "一年":
                    time = System.currentTimeMillis() + daytime * 365;
                    break;
                case "永久":
                default:
                    time = System.currentTimeMillis() + daytime * 99999;
                    break;
            }
            String passwd = MD5.md5(phone);
            AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
            bmobQuery.whereEqualTo("phone", phone);
            bmobQuery.findObjects(data -> {
                if (data == null || data.getCode() == 1) {
                    ToastUtil.showShortText("手机号查询失败");
                    return;
                }
                List<AxUser> list = data.getData();
                String date = DateUtils.getDateTimeByLong(time);
                if (list != null && list.size() == 1) {
                    AxUser user = list.get(0);
                    if (!UserManager.getInstance().isSuper() && !TextUtils.isEmpty(user.getFrom()) && !user.getFrom()
                        .equals(UserManager.getInstance().getPhone())) {
                        ToastUtil.showShortText("该号码被别人代理");
                        return;
                    }
                    user.setFrom(UserManager.getInstance().getPhone());
                    user.setExpire(date);
                    if (UserManager.getInstance().isSuper()) {
                        user.setLevel(cbProxy.isChecked() ? 10 : 1);
                    } else {
                        user.setLevel(1);
                    }
                    user.setTradeTime(System.currentTimeMillis());
                    user.update(data1 -> {
                        if (data1 == null || data1.getCode() == 1) {
                            ToastUtil.showShortText("更新失败");
                            return;
                        }
                        dismiss();
                        if (mOnDataChangedListener != null) {
                            mOnDataChangedListener.onDataCallback(user);
                        }
                    });
                } else {
                    AxUser user = new AxUser();
                    user.setPhone(phone);
                    user.setPasswd(passwd);
                    user.setFrom(UserManager.getInstance().getPhone());
                    user.setExpire(date);
                    if (UserManager.getInstance().isSuper()) {
                        user.setLevel(cbProxy.isChecked() ? 10 : 1);
                    } else {
                        user.setLevel(1);
                    }
                    user.setTradeTime(System.currentTimeMillis());
                    user.save(data1 -> {
                        if (data1 == null || data1.getCode() == 1) {
                            ToastUtil.showShortText("添加失败");
                            return;
                        }
                        dismiss();
                        if (mOnDataChangedListener != null) {
                            mOnDataChangedListener.onDataCallback(user);
                        }
                    });
                }
            });
        });
        if (UserManager.getInstance().isSuper()) {
            SelectBean selectBean = new SelectBean();
            selectBean.setTitle("一天");
            mData.add(selectBean);
        }
        for (String s : pate) {
            SelectBean selectBean = new SelectBean();
            selectBean.setTitle(s);
            mData.add(selectBean);
        }
        mData.get(1).setSelected(true);
        mAdapter.notifyDataSetChanged();
    }

    //    private PayInfo generatePayinfo(String name) {
//        PayInfo payInfo = new PayInfo();
//        payInfo.setTradename(name);
//        payInfo.setOuttradeno(name);
//        payInfo.setAmount(100);
//        payInfo.setBackparams(null);
//        payInfo.setNotifyurl(null);
//        payInfo.setUserid("1339211614@qq.com");
//        return payInfo;
//    }

    @Override
    public void onItemClick(View itemView, int position) {
        for (SelectBean mDatum : mData) {
            mDatum.setSelected(false);
        }
        mData.get(position).setSelected(true);
        mAdapter.notifyDataSetChanged();
    }

    public void setUser(final AxUser user) {
        mUser = user;
    }

    public void setPhonePassWd(String phone, String passwd) {
        this.phone = phone;
        this.passwd = passwd;
    }

    public void setOnDataChangedListener(OnDataCallback<AxUser> onDataChangedListener) {
        mOnDataChangedListener = onDataChangedListener;
    }
}
