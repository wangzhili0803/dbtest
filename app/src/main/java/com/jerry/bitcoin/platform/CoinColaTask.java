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
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.common.util.ToastUtil;
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
    private String transferTag;
    private String mValidateCode;

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

    public void checkCoinNeedTransfer(final ListenerService service, final String coinType, final EndCallback endCallback) {
        if (errorCount >= 3 || !AppUtils.playing) {
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
                if (service.exeClickText(coinType)) {
                    taskStep++;
                }
                break;
            case 3:
                taskStep++;
                service.exeClickText("我知道了");
                break;
            case 4:
                Double account = CoinConstant.ACCOUNT_MAP.get(coinType);
                String qtyStr = service.getNodeText(getPackageName() + "tv_total_assets_qty");
                double qty = ParseUtil.parseDouble(qtyStr.replace(Key.COMMA, Key.NIL).replace(coinType, Key.NIL).trim());
                if (account != null && qty > account) {
                    taskStep++;
                } else {
                    taskStep = 0;
                    errorCount = 0;
                    service.back();
                    endCallback.onEnd(false);
                    return;
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
        service.postDelayed(() -> checkCoinNeedTransfer(service, coinType, endCallback));
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
                if (service.input(getPackageName() + "et_address", "rUNoYkus9ZdvKCDXZiNSLd5EMkby31Bmi6")) {
                    taskStep++;
                }
                break;
            case 1:
                if (service.input(getPackageName() + "et_address_tag", "100719")) {
                    taskStep++;
                }
                break;
            case 2:
                String priceStr = service.getNodeText(getPackageName() + "tv_total_assets_qty");
                double dd = ParseUtil.parseDouble(priceStr.replace(CoinConstant.XRP, Key.NIL).trim());
                if (service.input(getPackageName() + "et_quantity", String.valueOf(dd - 70.25))) {
                    taskStep++;
                }
                break;
            case 3:
                service.exeSwipDown(1, result -> {
                    taskStep++;
                    service.postDelayed(() -> transferXrp(service, endCallback));
                });
                return;
            case 4:
                if (service.clickFirst(getPackageName() + "btn_confirm")) {
                    taskStep++;
                }
                break;
            case 5:
                if (service.input(getPackageName() + "et_input", "WZLwzl0705")) {
                    taskStep++;
                }
                break;
            case 6:
                if (service.exeClickText("确定")) {
                    taskStep++;
                }
                break;
            case 7:
                if (service.exeClickId(service.findFirstById(service.getRootInActiveWindow(), getPackageName() + "layout_sms_code"),
                    getPackageName() + "tv_sms_code")) {
                    taskStep++;
                    transferTag = CoinConstant.XRP;
                    smsEndCallback = endCallback;
                }
                break;
            case 8:
                service.mWeakHandler.postDelayed(() -> {
                    if (taskStep == 8) {
                        taskStep = 0;
                        errorCount = 0;
                        endCallback.onEnd(false);
                    }
                }, 20000);
                return;
            case 9:
                AccessibilityNodeInfo validateCodeNode = service
                    .findFirstById(service.findFirstById(service.getRootInActiveWindow(), getPackageName() + "layout_sms_code"),
                        getPackageName() + "et_validate_code");
                if (service.input(validateCodeNode, mValidateCode)) {
                    taskStep++;
                }
                break;
            case 10:
                if (service.clickLast(getPackageName() + "btn_confirm")) {
                    taskStep++;
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
        service.postDelayed(() -> transferXrp(service, endCallback));
    }

    public void transferBch(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3 || !AppUtils.playing) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            smsEndCallback = null;
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.input(getPackageName() + "et_address", "1PSc1ABQfCvAv2qKg9pq2drEj6H6C3b6cs")) {
                    taskStep++;
                }
                break;
            case 1:
                String priceStr = service.getNodeText(getPackageName() + "tv_total_assets_qty");
                double dd = ParseUtil.parseDouble(priceStr.replace(CoinConstant.BCH, Key.NIL).trim());
                if (service.input(getPackageName() + "et_quantity", String.valueOf(MathUtil.halfEven(dd - 0.2005, 8)))) {
                    taskStep++;
                }
                break;
            case 2:
                service.exeSwipDown(1, result -> {
                    taskStep++;
                    service.postDelayed(() -> transferBch(service, endCallback));
                });
                return;
            case 3:
                if (service.clickFirst(getPackageName() + "btn_confirm")) {
                    taskStep++;
                }
                break;
            case 4:
                if (service.input(getPackageName() + "et_input", "WZLwzl0705")) {
                    taskStep++;
                }
                break;
            case 5:
                if (service.exeClickText("确定")) {
                    taskStep++;
                }
                break;
            case 6:
                if (service.exeClickId(service.findFirstById(service.getRootInActiveWindow(), getPackageName() + "layout_sms_code"),
                    getPackageName() + "tv_sms_code")) {
                    taskStep++;
                    transferTag = CoinConstant.BCH;
                    smsEndCallback = endCallback;
                }
                break;
            case 7:
                service.mWeakHandler.postDelayed(() -> {
                    if (taskStep == 8) {
                        taskStep = 0;
                        errorCount = 0;
                        endCallback.onEnd(false);
                        smsEndCallback = null;
                    }
                }, 20000);
                return;
            case 8:
                AccessibilityNodeInfo validateCodeNode = service
                    .findFirstById(service.findFirstById(service.getRootInActiveWindow(), getPackageName() + "layout_sms_code"),
                        getPackageName() + "et_validate_code");
                if (service.input(validateCodeNode, mValidateCode)) {
                    taskStep++;
                }
                break;
            case 9:
                if (service.clickLast(getPackageName() + "btn_confirm")) {
                    taskStep++;
                }
                break;
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(true);
                smsEndCallback = null;
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> transferBch(service, endCallback));
    }

    private void validateCode(final ListenerService service, final String validateCode) {
        AccessibilityNodeInfo smsNode = service.findFirstById(service.getRootInActiveWindow(), getPackageName() + "layout_sms_code");
        AccessibilityNodeInfo validateCodeNode = service.findFirstById(smsNode, getPackageName() + "et_validate_code");
        if (service.input(validateCodeNode, validateCode)) {
            taskStep++;
        }
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

    private double getHighestPrice(final ListenerService service) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo != null) {
            List<AccessibilityNodeInfo> recyclerViews = accessibilityNodeInfo
                .findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view_ad");
            if (!CollectionUtils.isEmpty(recyclerViews)) {
                for (AccessibilityNodeInfo recyclerView : recyclerViews) {
                    if (recyclerView.isVisibleToUser()) {
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

    /**
     * 获取当前选中的tab
     */
    private String getSelectedSymbol(final ListenerService service) {
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

    private boolean isHomePage(final ListenerService service) {
        return service.hasText("首页", "币币", "场外", "钱包", "我的");
    }

    private boolean isListPage(final ListenerService service) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo rgAdType = service.findFirstById(rootNode, getPackageName() + "rg_ad_type");
            return rgAdType != null;
        }
        return false;
    }

    public void exchangeCoin(final ListenerService service, final EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        if (isListPage(service)) {
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
            return;
        }
        if (isHomePage(service)) {
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode != null) {
                AccessibilityNodeInfo mainTab = service.findFirstById(rootNode, getPackageName() + "main_tab_bar");
                if (mainTab != null && service.exeClick(mainTab.getChild(2))) {
                    service.postDelayed(() -> exchangeCoin(service, endCallback));
                    return;
                }
            }
        }
        service.back();
        service.postDelayed(() -> exchangeCoin(service, endCallback));
    }

    public void listenLists(final ListenerService service, final EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        if (!isListPage(service)) {
            if (isHomePage(service)) {
                AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
                AccessibilityNodeInfo mainTab = service.findFirstById(rootNode, getPackageName() + "main_tab_bar");
                if (mainTab != null && service.exeClick(mainTab.getChild(2))) {
                    service.postDelayed(() -> listenLists(service, endCallback));
                    return;
                }
            } else {
                service.back();
                service.postDelayed(() -> listenLists(service, endCallback));
            }
        }
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
            if (recyclerView.isVisibleToUser()) {
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
            double marketPrice = MathUtil.safeGet(ListenerService.priceMap, symbol);
            Double usdtPrice = ListenerService.usdtPrices.get(CoinConstant.HUOBI);
            double marketPrice4Cym = 0;
            if (usdtPrice != null) {
                marketPrice4Cym = MathUtil.halfEven(marketPrice * usdtPrice);
            }
            double finalPrice = Math.min(marketPrice4Cym, priceSecond);
            if (priceMe <= finalPrice || finalPrice + 0.01 < priceMe) {
                if (finalPrice + 0.01 > priceMe && service.exeClickId(firstItem, getPackageName() + "tv_operator")) {
                    service.postDelayed(() -> getPremiumRate(service, symbol, finalPrice, endCallback));
                    return;
                }
                // 原价
                double origin = priceMe / (1 + premiumRate / 100);
                double fdsfs = MathUtil.halfEven(((premiumRate - 0.01) / 100 + 1) * origin);
                if (finalPrice < fdsfs && service.exeClickId(firstItem, getPackageName() + "tv_operator")) {
                    service.postDelayed(() -> getPremiumRate(service, symbol, finalPrice, endCallback));
                    return;
                }
            }
        } else {
            String highestPriceStr = service.getNodeText(firstItem, getPackageName() + "tv_price").replace(Key.COMMA, Key.NIL)
                .replace("CNY", Key.NIL)
                .trim();
            double highestPrice = ParseUtil.parse2Double(highestPriceStr);
            double lowestClose = getLowestClose(symbol, highestPrice);
            double marketPrice = MathUtil.safeGet(ListenerService.priceMap, symbol);
            if (lowestClose < marketPrice) {
                service.swipToClickText("jerrywonder", result -> {
                    if (result) {
                        service.postDelayed(() -> getPremiumRate(service, symbol, highestPrice, endCallback));
                    } else {
                        endCallback.onEnd(false);
                    }
                });
                return;
            }
            ToastUtil.showShortText("marketPrice:" + marketPrice);
        }
        double lowestClose = getLowestClose(symbol, getHighestPrice(service));
        ToastUtil.showShortText("最低价：" + lowestClose);
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
                LogUtils.d("当前最高价：higestPrice:" + higestPrice);
                String title = service.getNodeText(getPackageName() + "tv_title");
                if (title.startsWith("购买") && service.exeClickId(service.getRootInActiveWindow(), getPackageName() + "btn_edit")) {
                    taskStep++;
                }
                break;
            case 1:
                AccessibilityNodeInfo root = service.getRootInActiveWindow();
                String marketPriceStr = service.getNodeText(root, getPackageName() + "tv_market_price").replace(Key.COMMA, Key.NIL)
                    .replace("CNY", Key.NIL).trim();
                double marketPrice = ParseUtil.parseDouble(marketPriceStr);
                if (marketPrice > 0) {
                    double premiumRate = (higestPrice / marketPrice - 1) * 100 + 0.01;
                    while (MathUtil.halfEven(marketPrice * ((1 + premiumRate / 100))) <= higestPrice) {
                        premiumRate = premiumRate + 0.01;
                    }
                    premiumRate = MathUtil.halfEven(premiumRate);
                    if (service.input(getPackageName() + "et_margin", String.valueOf(premiumRate))) {
                        premiumRateMap.put(symbol, premiumRate);
                        taskStep++;
                    }
                }
                break;
            case 2:
                if (ParseUtil.parseDouble(service.getNodeText(getPackageName() + "et_margin")) == 0) {
                    taskStep--;
                } else if (service.exeClickId(service.getRootInActiveWindow(), getPackageName() + "btn_commit")) {
                    taskStep++;
                }
                break;
            case 3:
                service.exeSwipUp(1, result -> {
                    taskStep++;
                    service.postDelayed(() -> getPremiumRate(service, symbol, higestPrice, endCallback));
                });
                return;
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
            if (recyclerView.isVisibleToUser() && recyclerView.getChildCount() == 0) {
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
                            sendMsgToBuyer(service, "OK", result -> service.postDelayed(() -> {
                                service.back();
                                onDataCallback.onDataCallback(finalOrder);
                            }));
                        } else {
                            sendMsgToBuyer(service, "您好，请提供一下您的支付信息：姓名+银行卡号+银行名称", result -> service.postDelayed(() -> {
                                service.back();
                                onDataCallback.onDataCallback(null);
                            }));
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
                                sendMsgToBuyer(service, "OK", result -> service.postDelayed(() -> {
                                    service.back();
                                    onDataCallback.onDataCallback(finalOrder);
                                }));
                            } else {
                                sendMsgToBuyer(service, "您好，请提供一下您的支付信息：姓名+银行卡号+银行名称", result -> service.postDelayed(() -> {
                                    service.back();
                                    onDataCallback.onDataCallback(null);
                                }));
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
            return ParseUtil.parseDouble(text.substring(0, i1).replace(Key.COMMA, ""));
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

    @Override
    public void onReceiveSmsMessage(final ListenerService service, final String address, final String content) {
        taskStep++;
        mValidateCode = StringUtil.getValidateCode4Sms(content);
        if (content.startsWith("【可盈可乐】")) {
            switch (transferTag) {
                case CoinConstant.XRP:
                    transferXrp(service, smsEndCallback);
                    break;
                case CoinConstant.BCH:
                default:
                    transferBch(service, smsEndCallback);
                    break;
            }
        }
    }
}
