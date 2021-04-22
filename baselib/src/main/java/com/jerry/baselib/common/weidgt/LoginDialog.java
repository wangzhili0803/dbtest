package com.jerry.baselib.common.weidgt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.bean.DataResponse;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.MD5;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description
 */
public class LoginDialog extends BaseDialog {

    private EditText etPhone;
    private EditText etLivecode;
    private String mWxCode;
    private OnDataCallback<DataResponse<AxUser>> mOnDataCallback;

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView titleTv = findViewById(R.id.title_tv);
        etPhone = findViewById(R.id.et_phone);
        etLivecode = findViewById(R.id.et_livecode);
        TextView confirm = findViewById(R.id.confirm_tv);
        confirm.setText(R.string.login);
        if (TextUtils.isEmpty(mWxCode)) {
            titleTv.setText(R.string.login);
            etPhone.setVisibility(View.VISIBLE);
            etLivecode.setVisibility(View.VISIBLE);
            confirm.setOnClickListener(v -> loginBySmsCode());
        } else {
            StringBuilder sb = new StringBuilder();
            if (mWxCode != null && mWxCode.length() > 8) {
                sb.append(mContext.getString(R.string.login_device));
                sb.append(Key.COLON);
                sb.append(mWxCode.substring(0, 4));
                sb.append("****");
                sb.append(mWxCode.substring(mWxCode.length() - 4));
            }
            titleTv.setText(sb.toString());
            etPhone.setVisibility(View.GONE);
            etLivecode.setVisibility(View.GONE);
            confirm.setOnClickListener(v -> loginByWxCode());
        }
    }

    private void loginByWxCode() {
        ((BaseActivity) mContext).loadingDialog();
        AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
        bmobQuery.whereEqualTo(Key.WXCODE, mWxCode);
        bmobQuery.findObjects(data -> {
            if (data.getCode() != 0) {
                ((BaseActivity) mContext).closeLoadingDialog();
                ToastUtil.showShortText(data.getMsg());
                return;
            }
            List<AxUser> list = data.getData();
            if (CollectionUtils.isEmpty(list)) {
                if (mOnDataCallback != null) {
                    DataResponse<AxUser> response = new DataResponse<>();
                    response.setCode(1);
                    AxUser user = new AxUser();
                    user.setWxCode(mWxCode);
                    response.setData(user);
                    mOnDataCallback.onDataCallback(response);
                }
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }

            AxUser user = list.get(0);
            if (TextUtils.isEmpty(user.getPhone())) {
                if (mOnDataCallback != null) {
                    DataResponse<AxUser> response = new DataResponse<>();
                    response.setCode(1);
                    response.setData(user);
                    mOnDataCallback.onDataCallback(response);
                }
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            String deviceId = AppUtils.getDeviceId();
            Set<String> devices = user.getDevices();
            if (devices == null) {
                devices = new HashSet<>();
            }
            if (devices.size() > 0 && !devices.contains(deviceId)) {
                ToastUtil.showShortText(R.string.out_of_devices);
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            devices.add(deviceId);
            user.setDevices(devices);
            user.setUserCode(MD5.md55(user.getPhone()));
            user.update(data1 -> {
                ((BaseActivity) mContext).closeLoadingDialog();
                if (data1.getCode() != 0) {
                    ToastUtil.showShortText(data1.getMsg());
                    return;
                }
                dismiss();
                ToastUtil.showShortText("登录成功");
                UserManager.getInstance().saveUser(user);
                if (mOnDataCallback != null) {
                    DataResponse<AxUser> response = new DataResponse<>();
                    response.setCode(0);
                    response.setData(user);
                    mOnDataCallback.onDataCallback(response);
                }
            });
        });
    }

    private void loginBySmsCode() {
        String phone = etPhone.getText().toString().trim();
        String passwd = etLivecode.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            ToastUtil.showShortText("手机号格式错误");
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            ToastUtil.showShortText("请输入密码");
        }
        ((BaseActivity) mContext).loadingDialog();
        AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
        bmobQuery.whereEqualTo("phone", phone).whereEqualTo("passwd", passwd);
        bmobQuery.findObjects(data -> {
            if (data == null || data.getCode() == 1) {
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            List<AxUser> list = data.getData();
            if (CollectionUtils.isEmpty(list)) {
                ((BaseActivity) mContext).closeLoadingDialog();
                ToastUtil.showShortText("账号或密码错误！");
                return;
            }
            AxUser user = list.get(0);
            String deviceId = AppUtils.getDeviceId();
            Set<String> devices = user.getDevices();
            if (devices == null) {
                devices = new HashSet<>();
            }
            if (devices.size() > 0 && !devices.contains(deviceId)) {
                ToastUtil.showShortText(R.string.out_of_devices);
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            devices.add(deviceId);
            user.setDevices(devices);
            user.update(data1 -> {
                ((BaseActivity) mContext).closeLoadingDialog();
                if (data1.getCode() != 0) {
                    ToastUtil.showShortText(data1.getMsg());
                    return;
                }
                dismiss();
                UserManager.getInstance().saveUser(user);
                if (mOnDataCallback != null) {
                    DataResponse<AxUser> response = new DataResponse<>();
                    response.setCode(0);
                    response.setData(user);
                    mOnDataCallback.onDataCallback(response);
                }
            });
        });
    }

    public void setOnDataCallback(OnDataCallback<DataResponse<AxUser>> onDataCallback) {
        this.mOnDataCallback = onDataCallback;
    }

    public void setWxCode(final String wxCode) {
        mWxCode = wxCode;
    }
}
