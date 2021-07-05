package com.jerry.bitcoin.home;

import java.io.File;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.jerry.baselib.ActionCode;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.BindingDialog;
import com.jerry.baselib.common.weidgt.LoginDialog;
import com.jerry.baselib.common.weidgt.NoticeDialog;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.interfaces.LoginActionListener;
import com.tencent.bugly.beta.Beta;

public class MainActivity extends BaseActivity implements LoginActionListener {

    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    protected static final int TO_ACCESSIBILITY = 101;
    private static final int REQUEST_PERMISSION = 102;
    public static final int REQUEST_PERMISSION_CONTACT = 103;
    private HomeFragment mHomeFragment;
    private MineFragment mMineFragment;
    private LinearLayout tabBar;

    private int checkId;
    private FragmentManager fragmentManager;

    public static Context getInstance() {
        return mainActivity;
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        switch (bundle.getInt(Key.ACTION)) {
            case ActionCode.USER_BIND:
                String wxCode = (String) bundle.get(Key.WXCODE);
                if (TextUtils.isEmpty(wxCode)) {
                    toast("获取登录信息失败");
                    return;
                }
                LoginDialog loginDialog = new LoginDialog(this);
                loginDialog.setWxCode(wxCode);
                loginDialog.setOnDataCallback(data -> {
                    closeLoadingDialog();
                    if (data == null) {
                        return;
                    }
                    if (data.getCode() == 1) {
                        loginDialog.dismiss();
                        showBinding(data.getData());
                    }
                    if (data.getCode() == 0) {
                        mMineFragment.updateUi();
                    }
                });
                loginDialog.show();
                break;
            case ActionCode.USER_UPDATED:
                mHomeFragment.reload();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mainActivity = this;
        tabBar = findViewById(R.id.tab_bar);
        findViewById(R.id.tv_main).setOnClickListener(this);
        findViewById(R.id.tv_me).setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        setContentFragment(R.id.tv_main);
        initPermission();
    }


    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        PackageManager pkgManager = getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
            pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
            pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !sdCardWritePermission || !phoneSatePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                .READ_PHONE_STATE}, REQUEST_PERMISSION);
            toast("——_——|||\n必要权限丫 您一定要同意哦");
        } else {
            // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
            init();
        }
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        String externalPath = FileUtil.getAppExternalPath();
        if (FileUtil.createOrExistsDir(new File(externalPath))) {
            File file = new File(externalPath + "icon.jpg");
            if (!file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error);
                BitmapUtil.saveBitmap2File(bitmap, Bitmap.CompressFormat.JPEG, 100, "icon.jpg", externalPath);
            }
        }
        Beta.checkUpgrade(false, false);
        if (AppUtils.isAccessibilitySettingsOff(this)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, TO_ACCESSIBILITY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(final View v) {
        setContentFragment(v.getId());
    }


    private void setContentFragment(int viewId) {
        if (isFinishing() || checkId == viewId) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (viewId == R.id.tv_main) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                transaction.add(R.id.content, mHomeFragment);
            } else {
                transaction.show(mHomeFragment);
            }
        } else if (viewId == R.id.tv_me) {
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                transaction.add(R.id.content, mMineFragment);
            } else {
                transaction.show(mMineFragment);
            }
        }
        checkId = viewId;
        transaction.commitAllowingStateLoss();
        for (int i = 0; i < tabBar.getChildCount(); i++) {
            View v = tabBar.getChildAt(i);
            v.setSelected(v.getId() == checkId);
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mMineFragment != null) {
            transaction.hide(mMineFragment);
        }
    }

    @Override
    public void showLogin() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setOnDataCallback(data -> {
            closeLoadingDialog();
            if (mMineFragment != null) {
                mMineFragment.updateUi();
            }
        });
        loginDialog.show();
    }

    @Override
    public void showLogout() {
        NoticeDialog noticeDialog = new NoticeDialog(this);
        noticeDialog.setCancelable(false);
        noticeDialog.setTitleText(getString(R.string.sure_logout, UserManager.getInstance().getPhone()));
        noticeDialog.setPositiveListener(view -> {
            noticeDialog.dismiss();
            UserManager.getInstance().logout(true, data -> {
                if (mMineFragment != null) {
                    mMineFragment.updateUi();
                }
            });
        });
        noticeDialog.show();
    }

    private void showBinding(AxUser user) {
        if (user == null) {
            toast("待绑定的用户为空");
            return;
        }
        BindingDialog bindingDialog = new BindingDialog(MainActivity.this, user, data -> {
            UserManager.getInstance().saveUser(data);
            if (mMineFragment != null) {
                mMineFragment.updateUi();
            }
        });
        bindingDialog.setUser(user);
        bindingDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                init();
            } else {
                toast("请同意权限，否则将无法正常使用");
            }
        } else if (requestCode == REQUEST_PERMISSION_CONTACT) {
            if ((grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            }
        } else {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}