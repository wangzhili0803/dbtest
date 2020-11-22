package com.jerry.baselib.common.weidgt;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.MD5;
import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description
 */
public class BindingDialog extends BaseDialog {

    private AxUser mUser;
    private EditText etPhone;
    private EditText etPasswd;
    private EditText etConPasswd;
    private OnDataChangedListener<AxUser> mOnDataChangedListener;

    public BindingDialog(Context context, AxUser user, OnDataChangedListener<AxUser> onDataChangedListener) {
        super(context);
        this.mUser = user;
        this.mOnDataChangedListener = onDataChangedListener;
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        etPhone = findViewById(R.id.et_phone);
        etPasswd = findViewById(R.id.et_passwd);
        etConPasswd = findViewById(R.id.et_conpasswd);
        TextView confirm = findViewById(R.id.confirm_tv);
        confirm.setText(R.string.bind);
        confirm.setOnClickListener(v -> loginBuSmsCode());
    }

    private void loginBuSmsCode() {
        String phone = etPhone.getText().toString().trim();
        String passwd = etPasswd.getText().toString().trim();
        String conpasswd = etConPasswd.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            ToastUtil.showShortText("手机号格式错误");
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            ToastUtil.showShortText("请输入注册密码");
            return;
        }
        if (passwd.length() < 6) {
            ToastUtil.showShortText("密码至少6位");
            return;
        }
        if (TextUtils.isEmpty(conpasswd)) {
            ToastUtil.showShortText("请输入确认密码");
            return;
        }
        if (!passwd.equals(conpasswd)) {
            ToastUtil.showShortText("两次输入的密码不一致");
            return;
        }

        ((BaseActivity) mContext).loadingDialog();
        AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
        bmobQuery.whereEqualTo("phone", phone);
        bmobQuery.findObjects(data -> {
            if (data == null || data.getCode() != 0) {
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            List<AxUser> list = data.getData();
            if (!CollectionUtils.isEmpty(list)) {
                AxUser user = list.get(0);
                if (!TextUtils.isEmpty(user.getWxCode())) {
                    ((BaseActivity) mContext).closeLoadingDialog();
                    ToastUtil.showLongText("该手机号已经被绑定");
                    return;
                }
                user.setPhone(phone);
                user.setPasswd(passwd);
                user.setUserCode(MD5.md55(phone));
                user.setWxCode(mUser.getWxCode());
                user.update(data1 -> {
                    ((BaseActivity) mContext).closeLoadingDialog();
                    if (data1 == null || data1.getCode() != 0) {
                        ToastUtil.showShortText("更新失败");
                        return;
                    }
                    dismiss();
                    UserManager.getInstance().saveUser(mUser);
                    if (mOnDataChangedListener != null) {
                        mOnDataChangedListener.onDataChanged(user);
                    }
                });
                return;
            }
            if (mUser == null) {
                AxUser user = new AxUser();
                user.setPhone(phone);
                user.setPasswd(passwd);
                user.setUserCode(MD5.md55(phone));
                user.setWxCode(mUser.getWxCode());
                user.setLevel(1);
                user.save(data1 -> {
                    ((BaseActivity) mContext).closeLoadingDialog();
                    if (data1 == null || data1.getCode() != 0) {
                        ToastUtil.showShortText("添加失败");
                        return;
                    }
                    user.setObjectId(data1.getData());
                    dismiss();
                    UserManager.getInstance().saveUser(mUser);
                    if (mOnDataChangedListener != null) {
                        mOnDataChangedListener.onDataChanged(user);
                    }
                });
            } else {
                mUser.setPhone(phone);
                mUser.setPasswd(passwd);
                mUser.setUserCode(MD5.md55(phone));
                mUser.setWxCode(mUser.getWxCode());
                mUser.setLevel(1);
                mUser.save(data1 -> {
                    ((BaseActivity) mContext).closeLoadingDialog();
                    if (data1 == null || data1.getCode() != 0) {
                        ToastUtil.showShortText("添加失败");
                        return;
                    }
                    mUser.setObjectId(data1.getData());
                    dismiss();
                    UserManager.getInstance().saveUser(mUser);
                    if (mOnDataChangedListener != null) {
                        mOnDataChangedListener.onDataChanged(mUser);
                    }
                });
            }
        });
    }

    public void setUser(final AxUser user) {
        mUser = user;
    }
}
