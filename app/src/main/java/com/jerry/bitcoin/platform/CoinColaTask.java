package com.jerry.bitcoin.platform;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.CoinOrder;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4Data;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.greendao.CoinOrderDao.Properties;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.beans.TransformInfo;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class CoinColaTask extends BaseTask {

    private static volatile CoinColaTask mInstance;
    private ArrayMap<String, Double> premiumRateMap = new ArrayMap<>();

    private List<String> listenCoins = new ArrayList<>();

    private int listenIndex;

    private CoinColaTask() {
        coinType = PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.USDT);
        listenCoins.add(CoinConstant.XRP);
        listenCoins.add(CoinConstant.BCH);
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
    public void sellOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {

    }

    @Override
    public void charge(final ListenerService service, final EndCallback endCallback) {

    }

    public void transferXrp(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3 || !AppUtils.playing) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.exeClickText("钱包")) {
                    taskStep++;
                }
                break;
            case 1:
                if (service.exeClickId(service.getRootInActiveWindow(), getPackageName() + "tv_withdraw")) {
                    taskStep++;
                }
                break;
            case 2:
                if (service.exeClickText(CoinConstant.XRP)) {
                    taskStep++;
                }
                break;
            case 3:
                if (service.exeClickText("我知道了")) {
                    taskStep++;
                }
                break;
            case 4:
                if (service.input(getPackageName() + "et_address", "rUNoYkus9ZdvKCDXZiNSLd5EMkby31Bmi6")) {
                    taskStep++;
                }
                break;
            case 5:
                if (service.input(getPackageName() + "et_address_tag", "100719")) {
                    taskStep++;
                }
                break;
            case 6:
                String priceStr = service.getNodeText(getPackageName() + "tv_total_assets_qty");
                double dd = ParseUtil.parseDouble(priceStr.replace(CoinConstant.XRP, Key.NIL).trim());
                if (service.input(getPackageName() + "et_quantity", String.valueOf(dd - 70.25))) {
                    taskStep++;
                }
                break;
            case 7:
                service.exeSwipDown();
                taskStep++;
                break;
            case 8:
                if (service.clickFirst(getPackageName() + "btn_confirm")) {
                    taskStep++;
                }
                break;
            case 9:
                if (service.input(getPackageName() + "et_input", "WZLwzl0705")) {
                    taskStep++;
                }
                break;
            case 10:
                if (service.exeClickText("确定")) {
                    taskStep++;
                }
                break;
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(false);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> transferXrp(service, endCallback));
    }

    @Override
    public void pay(final ListenerService listenerService, final EndCallback endCallback) {

    }

    @Override
    public void tryBuy(final ListenerService listenerService, final EndCallback endCallback) {

    }

    @Override
    public void checkContinuePay(final ListenerService listenerService, final OnDataCallback<Response4Data<TransformInfo>> endCallback) {

    }

    public double getHighestPrice(final ListenerService service) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo != null) {
            List<AccessibilityNodeInfo> recyclerViews = accessibilityNodeInfo
                .findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view_ad");
            if (!CollectionUtils.isEmpty(recyclerViews)) {
                for (AccessibilityNodeInfo recyclerView : recyclerViews) {
                    if (recyclerView.isFocused()) {
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            AccessibilityNodeInfo subNode = recyclerView.getChild(i);
                            List<AccessibilityNodeInfo> tvPrices = subNode.findAccessibilityNodeInfosByViewId(getPackageName() + "tv_price");
                            if (!CollectionUtils.isEmpty(tvPrices)) {
                                String priceStr = tvPrices.get(0).getText().toString();
                                int spaceIndex = priceStr.indexOf(Key.SPACE);
                                if (spaceIndex > -1) {
                                    return ParseUtil.parseDouble(priceStr.substring(0, spaceIndex).replace(Key.COMMA, Key.NIL));
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public String getSelectedSymbol(final ListenerService service) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        AccessibilityNodeInfo tabLayouts = service.findFirstById(accessibilityNodeInfo, getPackageName() + "tab_layout");
        if (tabLayouts != null) {
            AccessibilityNodeInfo tablayout = tabLayouts.getChild(0);
            if (tablayout != null) {
                for (int i = 0; i < tablayout.getChildCount(); i++) {
                    AccessibilityNodeInfo tab = tablayout.getChild(i);
                    if (tab.isSelected()) {
                        String coin = tab.getChild(0).getText().toString();
                        return coin.toLowerCase() + "usdt";
                    }
                }
            }
        }
        return "";
    }

    public void exchangeCoin(final ListenerService service, final EndCallback endCallback) {
        if (listenIndex < listenCoins.size() - 1) {
            listenIndex++;
        } else {
            listenIndex = 0;
        }
        if (service.exeClickText(listenCoins.get(listenIndex))) {
            service.postDelayed(() -> endCallback.onEnd(true));
        } else {
            endCallback.onEnd(false);
        }
    }

    public void listenLists(final ListenerService service, final EndCallback endCallback) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        AccessibilityNodeInfo floatingOrderMenu = service.findFirstById(accessibilityNodeInfo, getPackageName() + "floating_order_menu");
        if (service.exeClickId(floatingOrderMenu, getPackageName() + "tv_going")) {
            service.postDelayed(() -> endCallback.onEnd(true));
            return;
        }
        AccessibilityNodeInfo targetRecyclerView = null;
        List<AccessibilityNodeInfo> recyclerViews = accessibilityNodeInfo
            .findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view_ad");
        for (AccessibilityNodeInfo recyclerView : recyclerViews) {
            if (recyclerView.isFocused()) {
                targetRecyclerView = recyclerView;
                break;
            }
        }
        if (targetRecyclerView == null || targetRecyclerView.getChildCount() <= 1) {
            endCallback.onEnd(false);
            return;
        }
        AccessibilityNodeInfo headItem = targetRecyclerView.getChild(0);
        AccessibilityNodeInfo headTv = service.findFirstByText(headItem, "0手续费出售");
        if (headTv == null) {
            endCallback.onEnd(false);
            return;
        }
        String symbol = getSelectedSymbol(service);
        double premiumRate = MathUtil.safeGet(premiumRateMap, symbol);
        AccessibilityNodeInfo firstItem = targetRecyclerView.getChild(1);
        String nickname = service.getNodeText(firstItem, getPackageName() + "tv_nickname");
        if ("jerrywonder".equals(nickname)) {
            AccessibilityNodeInfo secondItem = targetRecyclerView.getChild(2);
            String priceSecondStr = service.getNodeText(secondItem, getPackageName() + "tv_price").replace(Key.COMMA, Key.NIL)
                .replace("CNY", Key.NIL)
                .trim();
            double priceSecond = ParseUtil.parse2Double(priceSecondStr);
            if (premiumRate == 0 && service.exeClickId(firstItem, getPackageName() + "tv_operator")) {
                service.postDelayed(() -> getPremiumRate(service, symbol, priceSecond, endCallback));
                return;
            }
            String priceMeStr = service.getNodeText(firstItem, getPackageName() + "tv_price").replace(Key.COMMA, Key.NIL)
                .replace("CNY", Key.NIL)
                .trim();
            double priceMe = ParseUtil.parse2Double(priceMeStr);
            if (priceMe <= priceSecond || priceSecond + 0.01 < priceMe) {
                if (priceSecond + 0.01 > priceMe && service.exeClickId(firstItem, getPackageName() + "tv_operator")) {
                    service.postDelayed(() -> getPremiumRate(service, symbol, priceSecond, endCallback));
                    return;
                }
                // 原价
                double origin = priceMe / (1 + premiumRate / 100);
                double fdsfs = MathUtil.halfEven(((premiumRate - 0.01) / 100 + 1) * origin);
                if (priceSecond < fdsfs && service.exeClickId(firstItem, getPackageName() + "tv_operator")) {
                    service.postDelayed(() -> getPremiumRate(service, symbol, priceSecond, endCallback));
                    return;
                }
            }
        } else {
            String highestPriceStr = service.getNodeText(firstItem, getPackageName() + "tv_price").replace(Key.COMMA, Key.NIL)
                .replace("CNY", Key.NIL)
                .trim();
            double highestPrice = ParseUtil.parse2Double(highestPriceStr);
            double lowestClose = getLowestClose(symbol, highestPrice);
            if (lowestClose > MathUtil.safeGet(ListenerService.priceMap, symbol)) {
                service.swipToClickText("jerrywonder", result -> {
                    if (result) {
                        service.postDelayed(() -> getPremiumRate(service, symbol, highestPrice, endCallback));
                    } else {
                        endCallback.onEnd(false);
                    }
                });
                return;
            }
        }
        endCallback.onEnd(false);
    }


    private double getLowestClose(final String symbol, final double highestPrice) {
        final double amount = 5000;
        Double fee = CoinConstant.FEEMAP.get(symbol);
        if (fee != null) {
            double c1 = amount / highestPrice * 0.993 - fee;
            Double usdtPrice = ListenerService.usdtPrices.get(CoinConstant.HUOBI);
            if (usdtPrice != null) {
                return amount / (c1 * 0.998 * usdtPrice);
            }
        }
        return Integer.MAX_VALUE;
    }

    private void getPremiumRate(final ListenerService service, final String symbol, final double higestPrice, final EndCallback endCallback) {
        if (errorCount >= 3 || !AppUtils.playing) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.exeClickId(service.getRootInActiveWindow(), getPackageName() + "btn_edit")) {
                    taskStep++;
                }
                break;
            case 1:
                AccessibilityNodeInfo root = service.getRootInActiveWindow();
                String marketPriceStr = service.getNodeText(root, getPackageName() + "tv_market_price").replace(Key.COMMA, Key.NIL)
                    .replace("CNY", Key.NIL).trim();
                double marketPrice = ParseUtil.parseDouble(marketPriceStr);
                if (marketPrice > 0) {
                    double premiumRate = MathUtil.halfEven((higestPrice / marketPrice - 1) * 100) + 0.01;
                    while (MathUtil.halfEven(marketPrice * ((1 + premiumRate / 100))) <= higestPrice) {
                        premiumRate = premiumRate + 0.01;
                    }
                    if (service.input(getPackageName() + "et_margin", String.valueOf(MathUtil.halfEven(premiumRate)))) {
                        premiumRateMap.put(symbol, premiumRate);
                        taskStep++;
                    }
                }
                break;
            case 2:
                if (ParseUtil.parseDouble(service.getNodeText(getPackageName() + "et_margin")) == 0) {
                    taskStep--;
                } else {
                    taskStep++;
                }
                break;
            case 3:
                if (service.exeClickId(service.getRootInActiveWindow(), getPackageName() + "btn_commit")) {
                    taskStep++;
                }
                break;
            case 4:
                service.exeSwipUp();
                taskStep++;
                break;
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(false);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> getPremiumRate(service, symbol, higestPrice, endCallback));
    }

    public void sellByCurrentPage(final ListenerService service, final OnDataCallback<CoinOrder> endCallback) {
        String title = service.getNodeText(getPackageName() + "tv_title");
        String nickname = service.getNodeText(getPackageName() + "tv_opposite_name");
        double amount = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_amount"));
        double qty = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_qty"));
        String orderId = service.getNodeText(getPackageName() + "tv_trade_no");
        double price = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_price"));
        double fee = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_fee"));
        CoinOrder cOrder = new CoinOrder();
        cOrder.setOrderId(orderId);
        cOrder.setCoinType(title.replace("购买", "").trim().toLowerCase() + "usdt");
        cOrder.setName(nickname);
        cOrder.setAmount(amount);
        cOrder.setQuantity(qty);
        cOrder.setPrice(price);
        cOrder.setFee(fee);
        endCallback.onDataCallback(cOrder);
    }

    public void listenOrder(final ListenerService service, final OnDataCallback<Integer> endCallback) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        List<AccessibilityNodeInfo> recyclerViews = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view");
        for (AccessibilityNodeInfo recyclerView : recyclerViews) {
            if (recyclerView.isFocused() && recyclerView.getChildCount() == 0) {
                endCallback.onDataCallback(2);
                return;
            }
        }
        AccessibilityNodeInfo tabLayout = service.findFirstById(accessibilityNodeInfo, getPackageName() + "tab_layout");
        AccessibilityNodeInfo complete = service.findFirstByText(tabLayout, "已完成");
        if (complete != null) {
            // 已完成中的dot
            AccessibilityNodeInfo dot = service.findFirstById(complete.getParent(), getPackageName() + "iv_dot");
            if (dot != null && service.exeClick(dot)) {
                service.postDelayed(() -> {
                    if (service.clickFirst(getPackageName() + "iv_dot")) {
                        //有待评价的订单
                        handleEvaluation(service, result -> endCallback.onDataCallback(1));
                    } else {
                        endCallback.onDataCallback(1);
                    }
                });
                return;
            }
        }
        deleteUnusedMsg(service, result -> {
            AccessibilityNodeInfo recyclerView = service.findFirstById(accessibilityNodeInfo, getPackageName() + "recycler_view");
            AccessibilityNodeInfo ivDot = service.findFirstById(recyclerView, getPackageName() + "iv_dot");
            if (ivDot != null && service.exeClick(ivDot)) {
                service.postDelayed(() -> endCallback.onDataCallback(0));
                return;
            }
            endCallback.onDataCallback(1);
        });
    }

    /**
     * 处理消息
     */
    public void handleMsg(final ListenerService service, final OnDataCallback<CoinOrder> onDataCallback) {
        String orderStatus = service.getNodeText(getPackageName() + "tv_order_status");
        switch (orderStatus) {
            case "待付款":
            case "已完成":
                String title = service.getNodeText(getPackageName() + "tv_title");
                String nickname = service.getNodeText(getPackageName() + "tv_opposite_name");
                double amount = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_amount"));
                double qty = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_qty"));
                String orderId = service.getNodeText(getPackageName() + "tv_trade_no");
                double price = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_price"));
                double fee = getNumberFromStr(service.getNodeText(getPackageName() + "tv_trade_fee"));
                String transInfo = service.getAllNodeText(getPackageName() + "tv_message_text");
                CoinOrder cOrder = ProManager.getInstance().queryObj(CoinOrder.class, Properties.Name.eq(nickname));
                if (cOrder == null) {
                    cOrder = new CoinOrder();
                    cOrder.setOrderId(orderId);
                    cOrder.setCoinType(title.replace("购买", "").trim().toLowerCase() + "usdt");
                    cOrder.setName(nickname);
                    cOrder.setStatus(2);
                    cOrder.setAmount(amount);
                    cOrder.setQuantity(qty);
                    cOrder.setPrice(price);
                    cOrder.setFee(fee);
                    if (ProManager.getInstance().insertObject(cOrder)) {
                        if (StringUtil.numericInStr(transInfo) >= 16) {
                            cOrder.setTransInfo(transInfo);
                            CoinOrder finalOrder = cOrder;
                            sendMsgToBuyer(service, "OK", result -> {
                                service.postDelayed(() -> {
                                    service.back();
                                    onDataCallback.onDataCallback(finalOrder);
                                });
                            });
                        } else {
                            sendMsgToBuyer(service, "您好，请提供一下您的支付信息：姓名+银行卡号+银行名称", result -> {
                                service.postDelayed(() -> {
                                    service.back();
                                    onDataCallback.onDataCallback(null);
                                });
                            });
                        }
                        return;
                    }
                } else {
                    if (StringUtil.numericInStr(transInfo) >= 16) {
                        cOrder.setOrderId(orderId);
                        cOrder.setCoinType(title.replace("购买", "").trim().toLowerCase() + "usdt");
                        cOrder.setName(nickname);
                        cOrder.setAmount(amount);
                        cOrder.setQuantity(qty);
                        cOrder.setPrice(price);
                        cOrder.setFee(fee);
                        cOrder.setStatus(2);
                        cOrder.setTransInfo(transInfo);
                        if (ProManager.getInstance().update(cOrder)) {
                            if (StringUtil.numericInStr(transInfo) >= 16) {
                                cOrder.setTransInfo(transInfo);
                                CoinOrder finalOrder = cOrder;
                                sendMsgToBuyer(service, "OK", result -> {
                                    service.postDelayed(() -> {
                                        service.back();
                                        onDataCallback.onDataCallback(finalOrder);
                                    });
                                });
                            } else {
                                sendMsgToBuyer(service, "您好，请提供一下您的支付信息：姓名+银行卡号+银行名称", result -> {
                                    service.postDelayed(() -> {
                                        service.back();
                                        onDataCallback.onDataCallback(null);
                                    });
                                });
                            }
                            return;
                        }
                    }
                }
                break;
            case "已放行":
                break;
            case "待下单":
            default:
                AccessibilityNodeInfo root = service.getRootInActiveWindow();
                AccessibilityNodeInfo chat = service.findFirstById(root, getPackageName() + "lv_chat_before_order");
                if (chat != null && service.exeClick(chat.getChild(MathUtil.random(0, 2)))) {
                    service.postDelayed(() -> {
                        service.back();
                        onDataCallback.onDataCallback(null);
                    });
                    return;
                }
                break;
        }
        service.back();
        onDataCallback.onDataCallback(null);
    }

    private void deleteUnusedMsg(final ListenerService service, EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.exeLongClick("待下单")) {
                    taskStep++;
                } else {
                    // 没有了待付款就返回
                    taskStep = 2;
                }
                break;
            case 1:
                if (service.exeClickText("删除")) {
                    // 继续删除
                    taskStep = 0;
                }
                break;
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> deleteUnusedMsg(service, endCallback));
    }

    /**
     * 去评价
     */
    private void handleEvaluation(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.exeClickText("待评价")) {
                    taskStep++;
                } else if (service.clickFirst(getPackageName() + "title_right_text")) {
                    // 没有了待评价就点击已读返回
                    taskStep = 5;
                }
                break;
            case 1:
                if (service.clickFirst(getPackageName() + "tv_trade_finish_operator")) {
                    taskStep++;
                }
                break;
            case 2:
                if (service.exeClickText("确定")) {
                    taskStep++;
                }
                break;
            case 3:
                sendMsgToBuyer(service, "谢谢，期待下次合作哈！", result -> {
                    if (result) {
                        taskStep++;
                    } else {
                        errorCount++;
                    }
                    service.postDelayed(() -> handleEvaluation(service, endCallback));
                });
                return;
            case 4:
                service.back();
                taskStep = 0;
                break;
            case 5:
                if (service.exeClickText("进行中")) {
                    taskStep++;
                }
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> handleEvaluation(service, endCallback));
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
                } else {
                    callback.onEnd(false);
                }
            });
        } else {
            callback.onEnd(false);
        }
    }
}
