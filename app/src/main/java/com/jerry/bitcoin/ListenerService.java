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
import android.util.ArrayMap;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.constant.enums.ConnectionStateEnum;
import com.huobi.model.market.Candlestick;
import com.huobi.service.huobi.connection.HuobiWebSocketConnection;
import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.BaseListenerService;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.flow.FloatItem;
import com.jerry.baselib.common.flow.FloatLogoMenu;
import com.jerry.baselib.common.flow.FloatMenuView;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.WeakHandler;
import com.jerry.baselib.parsehelper.WebLoader;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.home.MainActivity;
import com.jerry.bitcoin.platform.CoinColaTask;
import com.jerry.bitcoin.platform.HuobiTask;
import com.jerry.bitcoin.trade.HuobiTradeHelper;

import androidx.core.content.ContextCompat;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessageOption;
import cn.leancloud.im.v2.AVIMReservedMessageType;
import cn.leancloud.im.v2.AVIMTypedMessage;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import cn.leancloud.json.JSON;
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
    private static final String URL_GWEB = "https://history.btc126.com/usdt/";
    /**
     * 擦亮
     */
    private static final int MSG_DO_TASK = 101;
    private static final int MSG_TRANS = 102;
    private static final int MSG_ORDER = 103;

    private static final SubCandlestickRequest CANDLESTICK_RESQUEST = new SubCandlestickRequest();
    private HuobiWebSocketConnection mHuobiWebSocketConnection;

    static {
        CANDLESTICK_RESQUEST.setSymbol(CoinConstant.SUB_CANDLE);
        CANDLESTICK_RESQUEST.setInterval(CandlestickIntervalEnum.MIN1);
    }

    private FloatLogoMenu menu;
    private WebLoader webLoader;
    public static ArrayMap<String, Double> usdtPrices = new ArrayMap<>();
    public static ArrayMap<String, Double> priceMap = new ArrayMap<>();

    private final FloatItem startItem = new FloatItem("开始", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem transItem = new FloatItem("搬运", 0x99000000, 0x99000000,
        BitmapFactory.decodeResource(BaseApp.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem moneyItem = new FloatItem("下单", 0x99000000, 0x99000000,
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
                    getUsdtData(null);
                    getHuobiMarket(Key.NIL, null);
                    listenLists();
                    return true;
                case MSG_TRANS:
                    mCoinColaTask.transferXrp(this, new EndCallback() {
                        @Override
                        public void onEnd(final boolean result) {

                        }
                    });
                    return true;
                case MSG_ORDER:
                    mCoinColaTask.sellByCurrentPage(this, coinOrder -> {
                        if (coinOrder != null) {
                            LogUtils.d(coinOrder.toString());

                            String symbol = coinOrder.getCoinType();
                            // 接受的最低价
                            getUsdtData(data1 -> getHuobiMarket(symbol, result -> {
                                // 当前市场价
                                double lowestPrice = getLowestClose(symbol, coinOrder.getAmount(), coinOrder.getPrice());
                                double currentPrice = MathUtil.safeGet(ListenerService.priceMap, symbol);
                                double finalPrice = Math.max(lowestPrice, currentPrice);
                                LogUtils.d("lowestPrice:" + lowestPrice + ",currentPrice:" + currentPrice);
                                HuobiTradeHelper.getInstance().sell(symbol, finalPrice, coinOrder.getQuantity() - coinOrder.getFee(),
                                    data -> {
                                        if (data == null) {
                                            LogUtils.e("下单失败");
                                            ToastUtil.showShortText("下单失败");
                                            return;
                                        }
                                        List<Long> orderList = JJSON.parseArray(ListCacheUtil.getValueFromJsonFile(Key.ORDER), Long.class);
                                        orderList.add(data);
                                        ListCacheUtil.saveValueToJsonFile(Key.ORDER, JSON.toJSONString(orderList));
                                    });
                            }));
                        }
                    });
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
        itemList.add(transItem);
        itemList.add(moneyItem);
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
                            itemList.add(transItem);
                            itemList.add(moneyItem);
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
                        switch (position) {
                            case 0:
                                start(MSG_DO_TASK);
                                itemList.clear();
                                itemList.add(stopItem);
                                menu.updateFloatItemList(itemList);
                                menu.hide();
                                break;
                            case 1:
                                start(MSG_TRANS);
                                itemList.clear();
                                itemList.add(stopItem);
                                menu.updateFloatItemList(itemList);
                                menu.hide();
                                break;
                            case 2:
                                start(MSG_ORDER);
                                itemList.clear();
                                itemList.add(stopItem);
                                menu.updateFloatItemList(itemList);
                                menu.hide();
                                break;
                            default:
                                break;
                        }

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
        return hasText("首页", "币币", "场外", "钱包", "我的");
    }

    @Override
    protected void stopScript() {
        super.stopScript();
        if (mHuobiWebSocketConnection != null) {
            mHuobiWebSocketConnection.close();
        }
    }

    /**
     * 清理msg
     */
    @Override
    protected void removeAllMessages() {
        super.removeAllMessages();
        mWeakHandler.removeMessages(MSG_DO_TASK);
        mWeakHandler.removeMessages(MSG_TRANS);
        mWeakHandler.removeMessages(MSG_ORDER);
    }

    public void postDelayed(Runnable runnable) {
        mWeakHandler.postDelayed(runnable, TIME_LONG);
    }

    public void getUsdtData(EndCallback endCallback) {
        if (!AppUtils.playing && endCallback == null) {
            return;
        }
        webLoader.load(URL_GWEB, data -> {
            LogUtils.d(data);
            Document doc = Jsoup.parse(data);
            Elements allElements = doc.getAllElements();Element sellBody = allElements.select(".layui-card-body").get(0);
            String tmp = "";
            for (int i = 0; i < sellBody.childNodeSize(); i++) {
                Node element = sellBody.childNode(i);
                if (element instanceof Element) {
                    tmp = ((Element) element).text().replace("：", "");
                } else if (element instanceof TextNode) {
                    if (!TextUtils.isEmpty(tmp)) {
                        usdtPrices.put(tmp.toLowerCase(), ParseUtil.parseDouble(((TextNode) element).text(), 6.6));
                    }
                } else if (element instanceof Comment) {
                    LogUtils.d(element.baseUri());
                }
            }
            if (endCallback != null) {
                endCallback.onEnd(true);
            }
            mWeakHandler.postDelayed(() -> getUsdtData(null), 60000);
        });
    }

    private void getHuobiMarket(final String symbol, final EndCallback endCallback) {
        if (mHuobiWebSocketConnection == null) {
            MarketClient marketClient = MarketClient.create(new HuobiOptions());
            mHuobiWebSocketConnection = marketClient.subCandlestick(CANDLESTICK_RESQUEST, response -> {
                Candlestick candlestick = response.getCandlestick();
                if (response.getCh().contains(CoinConstant.BCH_USDT)) {
                    priceMap.put(CoinConstant.BCH_USDT, candlestick.getClose().doubleValue());
                } else if (response.getCh().contains(CoinConstant.XRP_USDT)) {
                    priceMap.put(CoinConstant.XRP_USDT, candlestick.getClose().doubleValue());
                }
                if (endCallback != null && response.getCh().contains(symbol)) {
                    mHuobiWebSocketConnection.close();
                    endCallback.onEnd(true);
                }
            });
        } else if (mHuobiWebSocketConnection.getState() != ConnectionStateEnum.CONNECTED) {
            mHuobiWebSocketConnection.reConnect();
        } else if (endCallback != null) {
            endCallback.onEnd(true);
        }
    }

    private double getLowestClose(final String symbol, final double amount, final double highestPrice) {
        Double fee = CoinConstant.FEEMAP.get(symbol);
        if (fee != null) {
            double c1 = amount / highestPrice * 0.993 - fee;
            Double usdtPrice = usdtPrices.get(CoinConstant.HUOBI);
            if (usdtPrice != null) {
                return amount / (c1 * 0.998 * usdtPrice);
            }
        }
        return Integer.MAX_VALUE;
    }

    private void listenLists() {
        if (!AppUtils.playing) {
            return;
        }
        mCoinColaTask.exchangeCoin(this, result -> {
            if (AppUtils.playing) {
                pullRefresh(t -> mCoinColaTask.listenLists(this, result1 -> {
                    if (AppUtils.playing) {
                        if (result1) {
                            mWeakHandler.postDelayed(this::listenOrder, TIME_LONG);
                        } else {
                            mWeakHandler.postDelayed(this::listenLists, TIME_LONG);
                        }
                    }
                }));
            }
        });
    }

    private void listenOrder() {
        if (!AppUtils.playing) {
            return;
        }
        pullRefresh(t -> mCoinColaTask.listenOrder(this, result -> {
            if (result == 0) {
                // 进入聊天界面
                mCoinColaTask.handleMsg(this, coinOrder -> {
                    if (coinOrder != null) {
                        LogUtils.d(coinOrder.toString());
                        giveNotice();
                        String symbol = coinOrder.getCoinType();
                        // 接受的最低价
                        double lowestPrice = getLowestClose(symbol, coinOrder.getAmount(), coinOrder.getPrice());
                        // 当前市场价
                        double currentPrice = MathUtil.safeGet(ListenerService.priceMap, symbol);
                        double finalPrice = Math.max(lowestPrice, currentPrice);
                        LogUtils.d("lowestPrice:" + lowestPrice + ",currentPrice:" + currentPrice);
                        HuobiTradeHelper.getInstance().sell(symbol, finalPrice, coinOrder.getQuantity() - coinOrder.getFee(), data -> {
                            if (data == null) {
                                LogUtils.e("下单失败");
                                ToastUtil.showShortText("下单失败");
                                return;
                            }
                            List<Long> orderList = JJSON.parseArray(ListCacheUtil.getValueFromJsonFile(Key.ORDER), Long.class);
                            orderList.add(data);
                            ListCacheUtil.saveValueToJsonFile(Key.ORDER, JSON.toJSONString(orderList));
                        });
                        // TODO发消息去控制端
                    }
                });
            } else if (result == 1) {
                ToastUtil.showShortText("暂无消息");
                mWeakHandler.postDelayed(this::listenOrder, TIME_LONGLONG);
            } else {
                back();
                mWeakHandler.postDelayed(this::listenLists, TIME_LONGLONG);
            }
        }));
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