package com.jerry.dbtest;

import android.support.multidex.MultiDexApplication;

/**
 * Created by wzl-pc on 2017/2/15.
 */

public class FYApplication extends MultiDexApplication {
    public static final String TAG = FYApplication.class.getCanonicalName();
    @Override
    public void onCreate() {
        super.onCreate();
//        Bugly.init(getApplicationContext(), "202d1fda06", false);
//        //自动初始化开关
//        Beta.autoInit = true;
//        //自动检查更新开关
//        Beta.autoCheckUpgrade = true;
////        升级检查周期设置
//        Beta.upgradeCheckPeriod = 60 * 1000;
////        设置通知栏大图标
//        Beta.largeIconId = R.mipmap.ic_launcher;
////        设置状态栏小图标
//        Beta.smallIconId = R.mipmap.ic_launcher;
////        设置更新弹窗默认展示的banner
//        Beta.defaultBannerId = R.mipmap.ic_launcher;
////        设置sd卡的Download为更新资源存储目录
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
////        设置开启显示打断策略
//        Beta.showInterruptedStrategy = true;
////        添加可显示弹窗的Activity
//        Beta.canShowUpgradeActs.add(MainActivity.class);
////        设置自定义升级对话框UI布局
//        Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;
////        设置自定义tip弹窗UI布局
//        Beta.tipsDialogLayoutId = R.layout.tips_dialog;
////        设置是否显示消息通知
//        Beta.enableNotification = true;
////        设置Wifi下自动下载
//        Beta.autoDownloadOnWifi = false;
////        设置是否显示弹窗中的apk信息
//        Beta.canShowApkInfo = true;
////        关闭热更新能力
//        Beta.enableHotfix = true;
//
//        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
//            @Override
//            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onCreate");
//
//            }
//
//            @Override
//            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onStart");
//            }
//
//            @Override
//            public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onResume");
//                // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:
//
//                // 通过id方式获取控件，并更改imageview图片
//                ImageView imageView = (ImageView) view.findViewById(R.id.icon);
//                imageView.setImageResource(R.mipmap.ic_launcher);
//
//                // 通过tag方式获取控件，并更改布局内容
//                TextView textView = (TextView) view.findViewWithTag("textview");
//                textView.setText("my custom text");
//
//                // 更多的操作：比如设置控件的点击事件
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getApplicationContext(), UpgradeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            @Override
//            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onPause");
//            }
//
//            @Override
//            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onStop");
//            }
//
//            @Override
//            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onDestory");
//            }
//
//        };
//
//
//         /*在application中初始化时设置监听，监听策略的收取*/
//        Beta.upgradeListener = new UpgradeListener() {
//            @Override
//            public void onUpgrade(int ret,UpgradeInfo strategy, boolean isManual, boolean isSilence) {
//                if (strategy != null) {
//                    Intent i = new Intent();
//                    i.setClass(getApplicationContext(), UpgradeActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(FYApplication.this, "没有更新", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//
///* 设置更新状态回调接口 */
//        Beta.upgradeStateListener = new UpgradeStateListener() {
//            @Override
//            public void onUpgradeSuccess(boolean isManual) {
//                Toast.makeText(getApplicationContext(),"UPGRADE_SUCCESS",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUpgradeFailed(boolean isManual) {
//                Toast.makeText(getApplicationContext(),"UPGRADE_FAILED",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUpgrading(boolean isManual) {
//                Toast.makeText(getApplicationContext(),"UPGRADE_CHECKING",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDownloadCompleted(boolean b) {
//                Toast.makeText(getApplicationContext(),"DOWNLOAD_COMPLETED",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUpgradeNoVersion(boolean isManual) {
//                Toast.makeText(getApplicationContext(),"UPGRADE_NO_VERSION",Toast.LENGTH_SHORT).show();
//            }
//        };

    }
}
