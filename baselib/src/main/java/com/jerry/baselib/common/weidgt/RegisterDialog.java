package com.jerry.baselib.common.weidgt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.bean.DataResponse;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.MD5;
import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.Patterns;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description
 */
public class RegisterDialog extends BaseDialog {

    private EditText etPhone;
    private EditText etPassWd;
    private EditText etConPasswd;
    private EditText etInvitation;
    private OnDataChangedListener<DataResponse<AxUser>> mOnDataChangedListener;

    public RegisterDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etPhone = findViewById(R.id.et_phone);
        etPassWd = findViewById(R.id.et_passwd);
        etConPasswd = findViewById(R.id.et_conpasswd);
        etInvitation = findViewById(R.id.et_invitation);
        TextView confirm = findViewById(R.id.confirm_tv);
        confirm.setText(R.string.register);
        etPhone.setVisibility(View.VISIBLE);
        etPassWd.setVisibility(View.VISIBLE);
        //粘贴板
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd2 = clipboard.getPrimaryClip();
        if (cd2 != null && cd2.getItemCount() > 0) {
            CharSequence text1 = cd2.getItemAt(0).getText();
            if (text1 != null) {
                String text = text1.toString().trim();
                if (text.length() == 5 && Patterns.isNumChar(text)) {
                    etInvitation.setText(text);
                }
            }
        }
        confirm.setOnClickListener(v -> rigisterByInvitation());
    }

    /**
     * 通过邀请码注册
     */
    private void rigisterByInvitation() {
        String phone = etPhone.getText().toString().trim();
        String passwd = etPassWd.getText().toString().trim();
        String conpasswd = etConPasswd.getText().toString().trim();
        String invitation = etInvitation.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            ToastUtil.showShortText("手机号格式错误");
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            ToastUtil.showShortText("请输入注册密码");
            return;
        }
        if (passwd.length() < 6) {
            ToastUtil.showShortText("注册密码至少6位");
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
        if (TextUtils.isEmpty(invitation)) {
            ToastUtil.showShortText("请输入邀请码");
            return;
        }
        ((BaseActivity) mContext).loadingDialog();
        new AVObjQuery<>(AxUser.class).whereEqualTo("userCode", invitation).findObjects(data -> {
            if (data.getCode() != 0) {
                ToastUtil.showShortText(data.getMsg());
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            if (CollectionUtils.isEmpty(data.getData())) {
                ToastUtil.showShortText("无法识别该邀请码");
                ((BaseActivity) mContext).closeLoadingDialog();
                return;
            }
            new AVObjQuery<>(AxUser.class).whereEqualTo("phone", phone).findObjects(data1 -> {
                if (data1.getCode() != 0) {
                    ToastUtil.showShortText(data1.getMsg());
                    ((BaseActivity) mContext).closeLoadingDialog();
                    return;
                }
                List<AxUser> list = data1.getData();
                if (!CollectionUtils.isEmpty(list)) {
                    ToastUtil.showShortText("手机号已注册，请登录");
                    ((BaseActivity) mContext).closeLoadingDialog();
                    return;
                }
                AxUser user = new AxUser();
                user.setPhone(phone);
                user.setPasswd(passwd);
                user.setUserCode(MD5.md55(phone));
                user.setFrom(invitation);
                user.setLevel(1);
                Set<String> devices= new HashSet<>();
                devices.add(AppUtils.getDeviceId());
                user.setDevices(devices);
                user.save(data2 -> {
                    ((BaseActivity) mContext).closeLoadingDialog();
                    if (data2 == null || data2.getCode() != 0) {
                        ToastUtil.showShortText("注册失败");
                        return;
                    }
                    user.setObjectId(data2.getData());
                    dismiss();
                    ToastUtil.showShortText("注册成功");
                    UserManager.getInstance().saveUser(user);
                    if (mOnDataChangedListener != null) {
                        DataResponse<AxUser> response = new DataResponse<>();
                        response.setCode(0);
                        response.setData(user);
                        mOnDataChangedListener.onDataChanged(response);
                    }
                });
            });
        });
    }

    public void setOnDataChangedListener(OnDataChangedListener<DataResponse<AxUser>> onDataChangedListener) {
        this.mOnDataChangedListener = onDataChangedListener;
    }
}
