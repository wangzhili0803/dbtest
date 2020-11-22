package com.jerry.bobo.home;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jerry.baselib.common.base.BaseFragment;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.NoticeDialog;
import com.jerry.bobo.ListenerService;
import com.jerry.bobo.R;
import com.jerry.bobo.interfaces.LoginActionListener;
import com.jerry.bobo.setting.SettingActivity;
import com.snail.antifake.jni.EmulatorDetectUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment {

    private LoginActionListener mLoginActionListener;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mLoginActionListener = (LoginActionListener) context;
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(final View view) {
        view.findViewById(R.id.iv_user).setOnClickListener(this);
        TextView tvUser = view.findViewById(R.id.tv_user);
        tvUser.setOnClickListener(this);
        view.findViewById(R.id.tv_setting).setOnClickListener(this);
        View btnDevice = view.findViewById(R.id.btn_device);
        if (EmulatorDetectUtil.isEmulator()) {
            btnDevice.setVisibility(View.GONE);
        } else {
            btnDevice.setOnClickListener(this);
            btnDevice.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.btn_wechat).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        int i = v.getId();
        if (i == R.id.iv_user || i == R.id.tv_user) {
            if (UserManager.getInstance().isLogined()) {
                mLoginActionListener.showLogout();
            } else {
                mLoginActionListener.showLogin();
            }
        } else if (i == R.id.btn_wechat) {
            NoticeDialog noticeDialog = new NoticeDialog(mActivity);
            noticeDialog.setPositiveText(R.string.confirm);
            noticeDialog.setTitleText("阿翔助手将去微信首页\n采集您的微信号");
            noticeDialog.setPositiveListener(view -> {
                if (AppUtils.isAccessibilitySettingsOff(mActivity)) {
                    toast("请先开启辅助服务");
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    return;
                }
                ListenerService.wxLogin();
            });
            noticeDialog.show();
        } else if (i == R.id.btn_device) {
            //TODO 设备登录
//            String code = AppUtils.getDeviceId();
//            Map<String, String> map = new ArrayMap<>();
//            map.put("wxCode", code);
//            RxBus.getInstance().post(map);
        } else if (i == R.id.tv_setting) {
            startActivity(new Intent(mActivity, SettingActivity.class));
        }
    }
}
