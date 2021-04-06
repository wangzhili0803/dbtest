package com.jerry.bitcoin;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.WeakHandler;
import com.jerry.baselib.parsehelper.WebLoader;
import com.jerry.bitcoin.home.MainActivity;
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

    private static final String URL_HUOBI = "https://c2c.huobi.be/zh-cn/trade/buy-usdt/";
    private static final String URL_GWEB = "https://www.gateio.ch/cn/c2c/usdt_cny";
    /**
     * 擦亮
     */
    private static final int MSG_DO_TASK = 101;

    private FloatLogoMenu menu;
    private WebLoader webLoader;
    public static double shouleBuy;

    private final FloatItem startItem = new FloatItem("开始", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem stopItem = new FloatItem("暂停", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.pause), "0");
    private final List<FloatItem> itemList = new ArrayList<>();
    /**
     * task状态，0：买入，1：出售，2，买入下单，3：出售下单
     */
    private int taskState;
    private CoinColaTask mCoinColaTask = CoinColaTask.getInstance();
    private HuobiTask mHuobiTask = HuobiTask.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        mWeakHandler = new WeakHandler(msg -> {
            switch (msg.what) {
                case MSG_DO_TASK:
                    listenOrder();
                    return true;
                default:
                    return false;
            }
        });
        webLoader = new WebLoader(this);
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
    public boolean isHomePage() {
        return hasText("首页", "行情", "交易", "合约", "资产");
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

    private void listenOrder() {
        if (!AppUtils.playing) {
            return;
        }
        mWeakHandler.postDelayed(() -> pullRefresh(t -> mCoinColaTask.listenOrder(this, result -> {
            if (result) {
                // 进入聊天界面
                mCoinColaTask.handleMsg(this, coinOrder -> {
                    if (coinOrder != null) {
                        LogUtils.d(coinOrder.toString());
                        giveNotice();
                    }
                });
            } else {
                ToastUtil.showShortText("暂无消息");
                mWeakHandler.postDelayed(this::listenOrder, TIME_LONGLONG);
            }
        })), TIME_SHORT);
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
        AVIMConversation imConversation = mCoinColaTask.getAvimConversation();
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
        AVIMConversation imConversation = mCoinColaTask.getAvimConversation();
        if (imConversation != null) {
            if (messageEvent != null && imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
                AVIMTypedMessage typedMessage = messageEvent.message;
                if (typedMessage.getMessageType() == AVIMReservedMessageType.TextMessageType.getType()) {
                    ToastUtil.showShortText("收到来自：" + typedMessage.getFrom() + "的消息，内容为：");
                }
            }
        }
    }

}