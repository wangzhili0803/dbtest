package com.jerry.bobo.home;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.LoginDialog;
import com.jerry.baselib.common.weidgt.NoticeDialog;
import com.jerry.bobo.R;
import com.jerry.bobo.interfaces.LoginActionListener;
import com.tencent.bugly.beta.Beta;

public class MainActivity extends BaseActivity implements LoginActionListener {

    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;
    protected static final int TO_ACCESSIBILITY = 101;
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

    @Override
    protected void initView() {
        mainActivity = this;
        tabBar = findViewById(R.id.tab_bar);
        findViewById(R.id.tv_main).setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        setContentFragment(R.id.tv_main);
        init();
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
        } else if (viewId == R.id.tab_me) {
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
        loginDialog.setOnDataChangedListener(data -> {
            closeLoadingDialog();
            // TODO 登录成功
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
                // TODO 登出成功
            });
        });
        noticeDialog.show();
    }
}