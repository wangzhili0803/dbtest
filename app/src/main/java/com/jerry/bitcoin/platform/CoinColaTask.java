package com.jerry.bitcoin.platform;

import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.CoinOrder;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4Data;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.greendao.CoinOrderDao.Properties;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.beans.TransformInfo;

import androidx.annotation.NonNull;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class CoinColaTask extends BaseTask {

    private static volatile CoinColaTask mInstance;

    private CoinColaTask() {
        coinType = PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.USDT);
        openConversation(null);
    }

    public static CoinColaTask getInstance() {
        if (mInstance == null) {
            synchronized (CoinColaTask.class) {
                if (mInstance == null) {
                    mInstance = new CoinColaTask();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getPackageName() {
        return "com.newgo.coincola:id/";
    }

    @Override
    public CoinBean getBuyCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = this.getValidNode(service, "购买");
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "tv_limit");
            String priceStr = service.getNodeText(validNode, getPackageName() + "tv_price");
            if (rationed != null && priceStr != null) {
                CoinBean buyCoin = new CoinBean();
                rationed = rationed.replace("限额", Key.NIL).replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim();
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                buyCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                buyCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                buyCoin.setPrice(ParseUtil.parse2Double(priceStr.replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim()));
                buyCoin.setCurrentTimeMs(System.currentTimeMillis());
                return buyCoin;
            }
        }
        return null;
    }

    @Override
    public CoinBean getSaleCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = this.getValidNode(service, "出售");
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "tv_limit");
            String priceStr = service.getNodeText(validNode, getPackageName() + "tv_price");
            if (rationed != null && priceStr != null) {
                rationed = rationed.replace("限额", Key.NIL).replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim();
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                CoinBean saleCoin = new CoinBean();
                saleCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                saleCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                saleCoin.setPrice(ParseUtil.parse2Double(priceStr.replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim()));
                saleCoin.setCurrentTimeMs(System.currentTimeMillis());
                return saleCoin;
            }
        }
        return null;
    }

    @Override
    protected AccessibilityNodeInfo getValidNode(final ListenerService service, @NonNull String nodeStr) {
        List<AccessibilityNodeInfo> tvOperators = service.getRootInActiveWindow()
            .findAccessibilityNodeInfosByViewId(getPackageName() + "tv_operator");
        if (!CollectionUtils.isEmpty(tvOperators)) {
            for (int i = 0; i < tvOperators.size(); i++) {
                AccessibilityNodeInfo tvOperator = tvOperators.get(i);
                // 购买类型一致
                if (nodeStr.equals(tvOperator.getText().toString())) {
                    Rect rect = new Rect();
                    tvOperator.getBoundsInScreen(rect);
                    if (rect.left > 0 && rect.right < DisplayUtil.getDisplayWidth()) {
                        return tvOperator.getParent();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void buyOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {

    }

    @Override
    public void saleOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {

    }

    @Override
    public void charge(final ListenerService service, final EndCallback endCallback) {

    }

    @Override
    public void transfer(final ListenerService service, final EndCallback endCallback) {

    }

    @Override
    public void pay(final ListenerService listenerService, final EndCallback endCallback) {

    }

    @Override
    public void tryBuy(final ListenerService listenerService, final EndCallback endCallback) {

    }

    @Override
    public void checkContinuePay(final ListenerService listenerService, final OnDataChangedListener<Response4Data<TransformInfo>> endCallback) {

    }

    public void listenOrder(final ListenerService service, final EndCallback endCallback) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo != null) {
            List<AccessibilityNodeInfo> recyclerViews = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view");
            if (!CollectionUtils.isEmpty(recyclerViews)) {
                AccessibilityNodeInfo recyclerView = recyclerViews.get(0);
//                for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
//                    AccessibilityNodeInfo child = recyclerView.getChild(i);
//                    String orderId = service.getNodeText(child, getPackageName() + "tv_order_id");
//                    String name = service.getNodeText(child, getPackageName() + "tv_nick_name");
//                    String type = service.getNodeText(child, getPackageName() + "tv_crypto_currency");
//                    String amount = service.getNodeText(child, getPackageName() + "tv_order_amount");
//                    if (TextUtils.isEmpty(orderId)) {
//
//                    } else {
//                        CoinOrder order = ProManager.getInstance().queryObj(CoinOrder.class, Properties.OrderId.like(orderId));
//                        if (order == null) {
//                            order = new CoinOrder();
//                        }
//                    }
//                }
                List<AccessibilityNodeInfo> ivDots = recyclerView.findAccessibilityNodeInfosByViewId(getPackageName() + "iv_dot");
                if (!CollectionUtils.isEmpty(ivDots)) {
                    service.exeClick(ivDots.get(0));
                    endCallback.onEnd(true);
                    return;
                }
            }
        }
        endCallback.onEnd(false);
    }

    /**
     * 处理消息
     */
    public void handleMsg(final ListenerService service, final OnDataChangedListener<CoinOrder> onDataChangedListener) {
        String orderStatus = service.getNodeText(getPackageName() + "tv_order_status");
        switch (orderStatus) {
            case "待下单":
                String name = service.getNodeText(getPackageName() + "tv_opposite_name");
                CoinOrder coinOrder = ProManager.getInstance().queryObj(CoinOrder.class, Properties.Name.eq(name), Properties.Status.eq(1));
                if (coinOrder == null) {
                    coinOrder = new CoinOrder();
                    coinOrder.setName(name);
                    coinOrder.setStatus(1);
                    if (ProManager.getInstance().insertObject(coinOrder)) {
                        AccessibilityNodeInfo root = service.getRootInActiveWindow();
                        List<AccessibilityNodeInfo> chats = root.findAccessibilityNodeInfosByViewId(getPackageName() + "lv_chat_before_order");
                        if (!CollectionUtils.isEmpty(chats)) {
                            if (service.exeClick(chats.get(MathUtil.random(0, 2)))) {
                                service.postDelayed(() -> {
                                    service.back();
                                    onDataChangedListener.onDataChanged(null);
                                });
                                return;
                            }
                        }
                    }
                }
                break;
            case "待付款":
                String nickname = service.getNodeText(getPackageName() + "tv_opposite_name");
                double amount = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_amount"));
                double qty = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_qty"));
                String orderId = service.getNodeText(getPackageName() + "tv_trade_no");
                double price = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_price"));
                double fee = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_fee"));
                CoinOrder cOrder = ProManager.getInstance().queryObj(CoinOrder.class, Properties.Name.eq(nickname));
                if (cOrder == null) {
                    cOrder = new CoinOrder();
                    cOrder.setOrderId(orderId);
                    cOrder.setName(nickname);
                    cOrder.setStatus(2);
                    cOrder.setAmount(amount);
                    cOrder.setQuantity(qty);
                    cOrder.setPrice(price);
                    cOrder.setFee(fee);
                    if (ProManager.getInstance().insertObject(cOrder)) {
                        sendMsgToBuyer(service, "您好，请提供一下您的支付信息：姓名+银行卡号+银行名称", result -> {
                            service.back();
                            onDataChangedListener.onDataChanged(null);
                        });
                        return;
                    }
                } else {
                    String transInfo = service.getAllNodeText(getPackageName() + "tv_message_text");
                    if (StringUtil.numericInStr(transInfo) >= 16) {
                        cOrder.setOrderId(orderId);
                        cOrder.setName(nickname);
                        cOrder.setAmount(amount);
                        cOrder.setQuantity(qty);
                        cOrder.setPrice(price);
                        cOrder.setFee(fee);
                        cOrder.setStatus(2);
                        cOrder.setTransInfo(transInfo);
                        if (ProManager.getInstance().update(cOrder)) {
                            CoinOrder finalOrder = cOrder;
                            sendMsgToBuyer(service, "OK", result -> {
                                service.back();
                                onDataChangedListener.onDataChanged(finalOrder);
                            });
                            return;
                        }
                    }
                }
                break;
            case "已放行":
            default:
                break;
        }
        service.back();
        onDataChangedListener.onDataChanged(null);
    }

    private double getNumberFromStr(final String text) {
        int i1 = text.indexOf(Key.SPACE);
        if (i1 > -1) {
            return ParseUtil.parse2Double(text.substring(0, i1).replace(Key.COMMA, ""));
        }
        return 0;
    }

    private void sendMsgToBuyer(final ListenerService service, final String message, EndCallback callback) {
        if (service.input(getPackageName() + "et_message_input", message)) {
            service.postDelayed(() -> {
                if (service.clickLast(getPackageName() + "tv_send_text")) {
                    callback.onEnd(true);
                }
            });
        }
    }
}
