package com.jerry.bitcoin;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.assibility.BaseListenerService;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.flow.FloatItem;
import com.jerry.baselib.common.flow.FloatLogoMenu;
import com.jerry.baselib.common.flow.FloatMenuView;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.WeakHandler;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.home.MainActivity;
import com.jerry.bitcoin.interfaces.TaskCallback;
import com.jerry.bitcoin.platform.CoinColaTask;
import com.jerry.bitcoin.platform.HuobiTask;

import androidx.core.content.ContextCompat;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessageOption;
import cn.leancloud.im.v2.AVIMReservedMessageType;
import cn.leancloud.im.v2.AVIMTypedMessage;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import cn.leancloud.session.AVConnectionManager;

/**
 * Created by cxk on 2017/2/4. email:471497226@qq.com
 * <p>
 * 获取即时微信聊天记录服务类
 */

public class ListenerService extends BaseListenerService {

    public static final String TYPE_PLATFORMS = "TYPE_PLATFORMS";
    public static final String TYPE_COINS = "TYPE_COINS";
    public static final String TYPE_BUYS = "TYPE_BUYS";
    /**
     * 擦亮
     */
    private static final int MSG_DO_TASK = 101;
    private static final int MSG_BUY_TYPE = 102;
    private static final int MSG_COIN_TYPE = 103;
    private static final int MSG_PLATFORM = 104;
    private static final int UNINSTALL_APP = 1;

    private FloatLogoMenu menu;

    private final FloatItem startItem = new FloatItem("开始", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem stopItem = new FloatItem("暂停", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.pause), "0");
    private final List<FloatItem> itemList = new ArrayList<>();
    private TaskCallback mTasksCallback;
    private HuobiTask mHuobiTask;
    private CoinColaTask mCoinColaTask;
    /**
     * 当前最新刷新信息
     */
    private CoinBean mCoinBean;

    @Override
    public void onCreate() {
        super.onCreate();
        mWeakHandler = new WeakHandler(msg -> {
            switch (msg.what) {
                case MSG_DO_TASK:
                    mWeakHandler.postDelayed(this::ptrRefresh, TIME_LONGLONG);
                    mWeakHandler.postDelayed(this::doTask, TIME_LONGLONG);
                    return true;
                case MSG_PLATFORM:
                    switch (PreferenceHelp.getString(TYPE_PLATFORMS, "huobi")) {
                        case "coincola":
                            if (mCoinColaTask == null) {
                                mCoinColaTask = new CoinColaTask();
                            }
                            mTasksCallback = mCoinColaTask;
                            break;
                        case "huobi":
                        default:
                            if (mHuobiTask == null) {
                                mHuobiTask = new HuobiTask();
                            }
                            mTasksCallback = mHuobiTask;
                            break;
                    }
                    packageName = mTasksCallback.getPackageName();
                    ToastUtil.showShortText("修改成功！");
                    return true;
                case MSG_COIN_TYPE:
                    mTasksCallback.setCoinType(PreferenceHelp.getString(TYPE_COINS), data -> ToastUtil.showShortText("修改成功！"));
                    return true;
                case MSG_BUY_TYPE:
                    mTasksCallback.setBuyType(PreferenceHelp.getInt(TYPE_BUYS), data -> ToastUtil.showShortText("修改成功！"));
                    return true;
                case UNINSTALL_APP:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (getApplicationInfo() != null) {
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            Intent it = new Intent(Intent.ACTION_DELETE, uri);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getInstance().startActivity(it);
                            mWeakHandler.postDelayed(() -> exeClickText(msg.obj.toString()), TIME_MIDDLE);
                        }
                    }
                    return true;
                default:
                    return false;
            }
        });
        setTaskPlatform();
        if (!EventBus.getDefault().isRegistered(ListenerService.this)) {
            EventBus.getDefault().register(ListenerService.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(ListenerService.this)) {
            EventBus.getDefault().unregister(ListenerService.this);
        }
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
                        if (AppUtils.playing) {
                            stopScript();
                            itemList.clear();
                            itemList.add(startItem);
                            menu.updateFloatItemList(itemList);
                            menu.hide();
                            return;
                        }
                        if (AppUtils.isAccessibilitySettingsOff(ListenerService.this)) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            ListenerService.this.startActivity(intent);
                            ToastUtil.showLongText("请先开启辅助哦");
                            return;
                        }
                        AppUtils.playing = true;
                        if (position != 0) {
                            return;
                        }
                        start(MSG_DO_TASK);
                        itemList.clear();
                        itemList.add(stopItem);
                        menu.updateFloatItemList(itemList);
                        menu.hide();
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
        return true;
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
        mWeakHandler.postDelayed(runnable, TIME_MIDDLE);
    }

    private void ptrRefresh() {
        if (!AppUtils.playing) {
            return;
        }
        exeSwip(mWidth >> 1, mHeight >> 1, mWidth >> 1, (int) (mHeight * 0.9));
        mWeakHandler.postDelayed(this::ptrRefresh, TIME_LONG);
    }

    private void doTask() {
        if (!AppUtils.playing) {
            return;
        }
        CoinBean tmp = mTasksCallback.getBuyInfo(this);
        if (tmp != null) {
            if (mTasksCallback.getBuyType() == TaskCallback.TYPE_SELL) {
                if (tmp.getPrice() >= PreferenceHelp.getFloat(CoinConstant.KEY_SALE)) {
                    // 执行卖出操作
                    mTasksCallback.saleOrder(this, result -> {
                        if (result) {
                            LogUtils.d("出售下单成功！");
                        } else {
                            LogUtils.d("出售下单失败！");
                        }
                    });
                    return;
                }
            } else {
                if (tmp.getPrice() <= PreferenceHelp.getFloat(CoinConstant.KEY_BUY)) {
                    // 执买入操作
                    mTasksCallback.buyOrder(this, result -> {
                        if (result) {
                            LogUtils.d("购买下单成功！");
                        } else {
                            LogUtils.d("购买下单失败！");
                        }
                    });
                    return;
                }
            }
            mCoinBean = tmp;
//            // 平台间检测
//            if (mTasksCallback.getBuyType() == TaskCallback.TYPE_SELL && !tmp.equals(mCoinBean)) {
//                String tmpInfo = JSON.toJSONString(tmp);
//                sendMessage(tmpInfo, result -> {
//                    if (result) {
//                        mCoinBean = tmp;
//                        ToastUtil.showShortText("当前信息：" + mCoinBean);
//                    }
//                });
//            } else {
//                mCoinBean = tmp;
//            }
        }
        mWeakHandler.postDelayed(this::doTask, TIME_LONGLONG);
    }

    public static void setTaskPlatform() {
        instance.mWeakHandler.sendEmptyMessage(MSG_PLATFORM);
    }

    public static void setBuyType() {
        instance.mWeakHandler.sendEmptyMessage(MSG_BUY_TYPE);
    }

    public static void setCoinType() {
        instance.mWeakHandler.sendEmptyMessage(MSG_COIN_TYPE);
    }

    /**
     * 发送消息给控制端
     */
    protected void sendMessage(String content, EndCallback callback) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        AVIMTextMessage message = new AVIMTextMessage();
        message.setText(content);
        AVIMMessageOption option = new AVIMMessageOption();
        if (content.startsWith("tr:")) {
            option.setTransient(true);
        } else {
            option.setReceipt(true);
        }
        AVIMConversation imConversation = mTasksCallback.getAvimConversation();
        if (imConversation != null) {
            imConversation.sendMessage(message, option, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (null != e) {
                        ToastUtil.showShortText(e.getMessage());
                        AVConnectionManager.getInstance().startConnection();
                        return;
                    }
                    callback.onEnd(true);
                    LogUtils.d("imConversation send success");
                }
            });
        }
    }


    /**
     * 处理推送过来的消息 同理，避免无效消息，此处加了 conversation id 判断
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LCIMIMTypeMessageEvent messageEvent) {
        AVIMConversation imConversation = mTasksCallback.getAvimConversation();
        if (imConversation != null) {
            if (messageEvent != null && imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
                AVIMTypedMessage typedMessage = messageEvent.message;
                if (typedMessage.getMessageType() == AVIMReservedMessageType.TextMessageType.getType()) {
                    String text = ((AVIMTextMessage) typedMessage).getText();
                    CoinBean sellCoin = JJSON.parseObject(text, CoinBean.class);
                    if (sellCoin.getPrice() > mCoinBean.getPrice()) {
                        LogUtils.w("发现低价：卖价：" + sellCoin.getPrice() + "，买价：" + mCoinBean.getPrice());
                        double maxLimit = findMaxLimit(sellCoin);
                        if (maxLimit > 0) {
                            LogUtils.w("发现执行：卖价：" + sellCoin.getPrice() + "，买价：" + mCoinBean.getPrice());
                            CoinBean coinBean = new CoinBean();
                            coinBean.setPrice(1);
                            coinBean.setMin(2);
                            coinBean.setMax(3);
                            coinBean.save();
                        }
                        giveNotice();
                    }
                    ToastUtil.showShortText("收到来自：" + typedMessage.getFrom() + "的消息，内容为：" + sellCoin.toString());
                }
            }
        }
    }

    private double findMaxLimit(CoinBean sellCoin) {
        double min1 = sellCoin.getMin();
        double max1 = sellCoin.getMax();
        double min2 = mCoinBean.getMin();
        double max2 = mCoinBean.getMax();
        if ((max2 > min1 && min2 < max1) && (max1 > min2 && min1 < max2)) {
            return Math.min(max1, max2);
        }
        return 0;
    }
}