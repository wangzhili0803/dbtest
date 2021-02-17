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

    public static final String TYPE_PLATFORM_BUY = "TYPE_PLATFORM_BUY";
    public static final String TYPE_PLATFORM_SALE = "TYPE_PLATFORM_SALE";
    public static final String TYPE_COINS = "TYPE_COINS";
    /**
     * 擦亮
     */
    private static final int MSG_DO_TASK = 101;
    private static final int MSG_PLATFORM_BUY = 102;
    private static final int MSG_PLATFORM_SALE = 103;
    private static final int MSG_COIN_TYPE = 104;
    private static final int UNINSTALL_APP = 1;

    private FloatLogoMenu menu;

    private final FloatItem startItem = new FloatItem("开始", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem stopItem = new FloatItem("暂停", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.pause), "0");
    private final List<FloatItem> itemList = new ArrayList<>();
    /**
     * task状态，0：买入，1：出售，2，买入下单，3：出售下单
     */
    private int taskState;
    private TaskCallback mBuyTask;
    private TaskCallback mSaleTask;
    /**
     * 当前最新买入刷新信息
     */
    private CoinBean mBuyCoinBean;
    /**
     * 当前最新买入刷新信息
     */
    private CoinBean mSaleCoinBean;

    @Override
    public void onCreate() {
        super.onCreate();
        mWeakHandler = new WeakHandler(msg -> {
            switch (msg.what) {
                case MSG_DO_TASK:
                    mWeakHandler.post(this::doTask);
                    return true;
                case MSG_PLATFORM_BUY:
                    switch (PreferenceHelp.getString(TYPE_PLATFORM_BUY, CoinConstant.HUOBI)) {
                        case CoinConstant.COINCOLA:
                            mBuyTask = CoinColaTask.getInstance();
                            break;
                        case CoinConstant.HUOBI:
                        default:
                            mBuyTask = HuobiTask.getInstance();
                            break;
                    }
                    ToastUtil.showShortText("修改成功！");
                    return true;
                case MSG_PLATFORM_SALE:
                    switch (PreferenceHelp.getString(TYPE_PLATFORM_BUY, CoinConstant.HUOBI)) {
                        case CoinConstant.COINCOLA:
                            mSaleTask = CoinColaTask.getInstance();
                            break;
                        case CoinConstant.HUOBI:
                        default:
                            mSaleTask = HuobiTask.getInstance();
                            break;
                    }
                    ToastUtil.showShortText("修改成功！");
                    return true;
                case MSG_COIN_TYPE:
                    mBuyTask.setCoinType(PreferenceHelp.getString(TYPE_COINS), data -> LogUtils.d("买入币种修改成功！"));
                    mSaleTask.setCoinType(PreferenceHelp.getString(TYPE_COINS), data -> LogUtils.d("出售币种修改成功！"));
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
        setTaskPlatformBuy();
        setTaskPlatformSale();
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

    private void doTask() {
        if (!AppUtils.playing) {
            return;
        }
        switch (taskState) {
            case 0:
                if (exeClickText("我要买")) {
                    mWeakHandler.postDelayed(() -> pullRefresh(t -> {
                        CoinBean buyInfo = mBuyTask.getBuyCoinInfo(ListenerService.this);
                        if (buyInfo != null) {
                            mBuyCoinBean = buyInfo;
                            if (mSaleCoinBean != null) {
                                LogUtils
                                    .d("mBuyCoinBean.getPrice()< mSaleCoinBean.getPrice():" + mBuyCoinBean.getPrice() + " < " + mSaleCoinBean
                                        .getPrice());
                            }
                            if (mSaleCoinBean != null && mBuyCoinBean.getPrice() < mSaleCoinBean.getPrice()) {
                                // 执买入操作
                                mBuyTask.buyOrder(ListenerService.this, result -> {
                                    if (result) {
                                        LogUtils.d("购买下单成功！");
                                        taskState = 2;
                                        back();
                                    }
                                    mWeakHandler.postDelayed(this::doTask, TIME_SHORT);
                                });
                                return;
                            }
                            taskState = 1;
                        }
                        doTask();
                    }), TIME_SHORT);
                    return;
                }
                break;
            case 1:
                if (exeClickText("我要卖")) {
                    mWeakHandler.postDelayed(() -> pullRefresh(t -> {
                        CoinBean saleInfo = mSaleTask.getSaleCoinInfo(this);
                        if (saleInfo != null) {
                            mSaleCoinBean = saleInfo;
                            taskState = 0;
                        }
                        doTask();
                    }), TIME_SHORT);
                    return;
                }
                break;
            case 2:
            default:
                if (exeClickText("我要卖")) {
                    mWeakHandler.postDelayed(() -> {
                        CoinBean saleInfo = mSaleTask.getSaleCoinInfo(this);
                        if (saleInfo != null) {
                            mSaleCoinBean = saleInfo;
                            if (mBuyCoinBean != null && mBuyCoinBean.getPrice() < mSaleCoinBean.getPrice()) {
                                // 执行出售操作
                                mSaleTask.saleOrder(this, result -> {
                                    if (result) {
                                        LogUtils.d("出售下单成功！");
                                        giveNotice();
                                    }
                                });
                                return;
                            }
                        }
                        doTask();
                    }, TIME_SHORT);
                }
                break;
        }
        mWeakHandler.postDelayed(this::doTask, TIME_LONGLONG);
    }

    public static void setTaskPlatformBuy() {
        instance.mWeakHandler.sendEmptyMessage(MSG_PLATFORM_BUY);
    }

    public static void setTaskPlatformSale() {
        instance.mWeakHandler.sendEmptyMessage(MSG_PLATFORM_SALE);
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
        AVIMConversation imConversation = mBuyTask.getAvimConversation();
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
        AVIMConversation imConversation = mBuyTask.getAvimConversation();
        if (imConversation != null) {
            if (messageEvent != null && imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
                AVIMTypedMessage typedMessage = messageEvent.message;
                if (typedMessage.getMessageType() == AVIMReservedMessageType.TextMessageType.getType()) {
                    String text = ((AVIMTextMessage) typedMessage).getText();
                    CoinBean sellCoin = JJSON.parseObject(text, CoinBean.class);
                    if (sellCoin.getPrice() > mBuyCoinBean.getPrice()) {
                        LogUtils.w("发现低价：卖价：" + sellCoin.getPrice() + "，买价：" + mBuyCoinBean.getPrice());
                        double maxLimit = findMaxLimit(sellCoin);
                        if (maxLimit > 0) {
                            LogUtils.w("发现执行：卖价：" + sellCoin.getPrice() + "，买价：" + mBuyCoinBean.getPrice());
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
        double min2 = mBuyCoinBean.getMin();
        double max2 = mBuyCoinBean.getMax();
        if ((max2 > min1 && min2 < max1) && (max1 > min2 && min1 < max2)) {
            return Math.min(max1, max2);
        }
        return 0;
    }
}