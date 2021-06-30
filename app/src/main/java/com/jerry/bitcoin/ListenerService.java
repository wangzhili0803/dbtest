package com.jerry.bitcoin;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.assibility.BaseListenerService;
import com.jerry.baselib.common.flow.FloatItem;
import com.jerry.baselib.common.flow.FloatLogoMenu;
import com.jerry.baselib.common.flow.FloatMenuView;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.util.WeakHandler;
import com.jerry.bitcoin.beans.PreferenceKey;
import com.jerry.bitcoin.home.MainActivity;
import com.jerry.bitcoin.platform.DouyinTask;

import androidx.core.content.ContextCompat;

/**
 * Created by cxk on 2017/2/4. email:471497226@qq.com
 * <p>
 * 获取即时微信聊天记录服务类
 */

public class ListenerService extends BaseListenerService {

    public static long TIME_DELAY = 5500 - 500 * PreferenceHelp.getInt(PreferenceKey.RUN_SPEED, 5);
    /**
     * 擦亮
     */
    private static final int MSG_DO_TASK = 101;

    private FloatLogoMenu menu;

    private final FloatItem startItem = new FloatItem("开始", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem stopItem = new FloatItem("暂停", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.pause), "0");
    private final List<FloatItem> itemList = new ArrayList<>();
    /**
     * 记录orderId
     */
    private DouyinTask mDouyinTask = DouyinTask.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        mWeakHandler = new WeakHandler(msg -> {
            switch (msg.what) {
                case MSG_DO_TASK:
                    mDouyinTask.doTask(this);
                    return true;
                default:
                    return false;
            }
        });
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // TODO 初始化Items
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        ListenerService.mWidth = point.x;
        ListenerService.mHeight = point.y - DisplayUtil.getNavigationBarHeightIfRoom(MainActivity.getInstance());
        ToastUtil.showShortText("服务已开启\n屏幕宽：" + ListenerService.mWidth + "\n屏幕高：" + ListenerService.mHeight);
        itemList.clear();
        itemList.add(startItem);
        if (menu == null) {
            menu = new FloatLogoMenu.Builder()
                .withContext(
                    getApplication())//这个在7.0（包括7.0）以上以及大部分7.0以下的国产手机上需要用户授权，需要搭配<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
                .logo(BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.menu))
                .drawCicleMenuBg(true)
                .backMenuColor(0xffe4e3e1)
                .setBgDrawable(ContextCompat.getDrawable(BaseApp.getInstance(), R.drawable.yw_game_float_menu_bg))
                //这个背景色需要和logo的背景色一致
                .addFloatItem(itemList)
                .defaultLocation(FloatLogoMenu.LEFT)
                .drawRedPointNum(false)
                .showWithListener(new FloatMenuView.SimpleMenuClickListener() {
                    @Override
                    public void onItemClick(int position, String title) {
                        if (AppUtils.isAccessibilitySettingsOff(ListenerService.this)) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            ListenerService.this.startActivity(intent);
                            ToastUtil.showLongText("请先开启辅助哦");
                            return;
                        }
                        if (AppUtils.playing) {
                            stopScript();
                            return;
                        }
                        UserManager.getInstance().requestUser(data -> {
                            if (!PreferenceHelp.getBoolean("follow_try") || !PreferenceHelp.getBoolean("search_try")) {
                                AppUtils.playing = true;
                                start(MSG_DO_TASK);
                                return;
                            }
                            if (!UserManager.getInstance().isLogined()) {
                                ToastUtil.showLongText("请先登录哦！");
                                return;
                            }
                            if (!UserManager.getInstance().isActive()) {
                                ToastUtil.showLongText("您的账户尚未激活 找客服小哥哥帮忙吧");
                                return;
                            }
                            UserManager.getInstance().checkDate(data1 -> {
                                if (!data1) {
                                    ToastUtil.showLongText("您的使用时间已用完咯 找客服小哥哥续期吧");
                                    return;
                                }
                                String dayout = UserManager.getInstance().getUser().getExpire();
                                if (dayout != null && dayout.length() == DateUtils.YYYYMMDDHHMMSS.length()) {
                                    ToastUtil.showLongText("到期时间：" + dayout.substring(0, 8));
                                }
                                AppUtils.playing = true;
                                start(MSG_DO_TASK);
                            });
                        });

                    }
                });
            menu.show();
        }
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    protected boolean isHomePage() {
        return false;
    }

    @Override
    protected void start(final int start) {
        super.start(start);
        itemList.clear();
        itemList.add(stopItem);
        menu.updateFloatItemList(itemList);
        menu.hide();
    }

    @Override
    public void stopScript() {
        super.stopScript();
        itemList.clear();
        itemList.add(startItem);
        menu.updateFloatItemList(itemList);
        menu.hide();
    }

    /**
     * 清理msg
     */
    @Override
    protected void removeAllMessages() {
        super.removeAllMessages();
        mWeakHandler.removeMessages(MSG_DO_TASK);
    }

    public void postDelayed(Runnable runnable) {
        postDelayed(runnable, TIME_DELAY);
    }

    public void postDelayed(Runnable runnable, long delay) {
        mWeakHandler.postDelayed(runnable, delay);
    }
}