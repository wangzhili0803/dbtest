package com.jerry.bitcoin.platform;

import java.util.List;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;

import androidx.annotation.NonNull;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class HuobiTask extends BaseTask {

    private static volatile HuobiTask mInstance;

    private HuobiTask() {
        coinType = PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.USDT);
        openConversation(null);
    }

    public static HuobiTask getInstance() {
        if (mInstance == null) {
            synchronized (HuobiTask.class) {
                if (mInstance == null) {
                    mInstance = new HuobiTask();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getPackageName() {
        return "pro.huobi:id/";
    }

    @Override
    public CoinBean getBuyCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> listViews = rootNode.findAccessibilityNodeInfosByViewId(getPackageName() + "list_view");
            if (!CollectionUtils.isEmpty(listViews)) {
                int targetIndex = 0;
                for (int i = 0; i < listViews.size(); i++) {
                    AccessibilityNodeInfo listView = listViews.get(i);
                    String typeStr = service.getNodeText(listView, getPackageName() + "coinUnit");
                    String buyStr = service.getNodeText(listView, getPackageName() + "buy_or_sell_btn");
                    if (coinType.equals(typeStr) && "购买".equals(buyStr)) {
                        targetIndex = i;
                        break;
                    }
                }
                AccessibilityNodeInfo list = listViews.get(targetIndex);
                for (int i = 0; i < list.getChildCount(); i++) {
                    AccessibilityNodeInfo validNode = list.getChild(i);
                    String priceStr = service.getNodeText(validNode, getPackageName() + "unitPriceValue");
                    double price = ParseUtil.parse2Double(priceStr);
                    if (price <= 6.54) {
                        // 成交量大于1000
                        String dealNum = service.getNodeText(validNode, getPackageName() + "merchantDealNum");
                        if (ParseUtil.parseInt(dealNum) > 1000) {
                            String rationed = service.getNodeText(validNode, getPackageName() + "rationedExchangeVol");
                            if (rationed != null && priceStr != null) {
                                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                                double min = ParseUtil.parse2Double(minMax[0]);
                                double max = ParseUtil.parse2Double(minMax[1]);
                                if (MONEY_POOL_MAX - getMoneyPool() > min) {
                                    CoinBean buyCoin = new CoinBean();
                                    buyCoin.setMin(min);
                                    buyCoin.setMax(max);
                                    buyCoin.setPrice(price);
                                    buyCoin.setCurrentTimeMs(System.currentTimeMillis());
                                    buyCoin.setShouldTrade(Math.min(MONEY_POOL_MAX - getMoneyPool(), max));
                                    buyCoin.setTag(validNode);
                                    return buyCoin;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public CoinBean getSaleCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> listViews = rootNode.findAccessibilityNodeInfosByViewId(getPackageName() + "list_view");
            if (!CollectionUtils.isEmpty(listViews)) {
                int targetIndex = 0;
                for (int i = 0; i < listViews.size(); i++) {
                    AccessibilityNodeInfo listView = listViews.get(i);
                    String typeStr = service.getNodeText(listView, getPackageName() + "coinUnit");
                    String buyStr = service.getNodeText(listView, getPackageName() + "buy_or_sell_btn");
                    if (coinType.equals(typeStr) && "出售".equals(buyStr)) {
                        targetIndex = i;
                        break;
                    }
                }
                AccessibilityNodeInfo list = listViews.get(targetIndex);
                for (int i = 0; i < list.getChildCount(); i++) {
                    AccessibilityNodeInfo validNode = list.getChild(i);
                    String priceStr = service.getNodeText(validNode, getPackageName() + "unitPriceValue");
                    double price = ParseUtil.parse2Double(priceStr);
                    if (price > ListenerService.shouleBuy) {
                        // 成交量大于1000
                        String dealNum = service.getNodeText(validNode, getPackageName() + "merchantDealNum");
                        if (ParseUtil.parseInt(dealNum) > 1000) {
                            String rationed = service.getNodeText(validNode, getPackageName() + "rationedExchangeVol");
                            if (rationed != null && priceStr != null) {
                                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                                double min = ParseUtil.parse2Double(minMax[0]);
                                double max = ParseUtil.parse2Double(minMax[1]);
                                if (MONEY_POOL_MAX - getMoneyPool() > min) {
                                    CoinBean saleCoin = new CoinBean();
                                    saleCoin.setMin(min);
                                    saleCoin.setMax(max);
                                    saleCoin.setPrice(price);
                                    saleCoin.setCurrentTimeMs(System.currentTimeMillis());
                                    return saleCoin;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected AccessibilityNodeInfo getValidNode(final ListenerService service, @NonNull String nodeStr) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> listViews = rootNode.findAccessibilityNodeInfosByViewId(getPackageName() + "list_view");
            if (!CollectionUtils.isEmpty(listViews)) {
                int targetIndex = 0;
                for (int i = 0; i < listViews.size(); i++) {
                    AccessibilityNodeInfo listView = listViews.get(i);
                    String typeStr = service.getNodeText(listView, getPackageName() + "coinUnit");
                    String buyStr = service.getNodeText(listView, getPackageName() + "buy_or_sell_btn");
                    if (coinType.equals(typeStr) && nodeStr.equals(buyStr)) {
                        targetIndex = i;
                        break;
                    }
                }
                AccessibilityNodeInfo list = listViews.get(targetIndex);
                for (int i = 0; i < list.getChildCount(); i++) {
                    AccessibilityNodeInfo accessibilityNodeInfo = list.getChild(i);
                    // 成交量大于1000
                    String dealNum = service.getNodeText(accessibilityNodeInfo, getPackageName() + "merchantDealNum");
                    if (ParseUtil.parseInt(dealNum) > 1000) {

                    }
                }
            }
        }
        return null;
    }

    @Override
    public void buyOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = (AccessibilityNodeInfo) coinBean.getTag();
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input(getPackageName() + "order_edit_text", String.valueOf(coinBean.getShouldTrade()))) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 2:
                if (service.clickFirst(getPackageName() + "place_order")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 3:
            default:
                taskStep = 0;
                errorCount = 0;
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> buyOrder(service, coinBean, endCallback));
    }

    @Override
    public void saleOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {
        if (errorCount >= 3) {
            errorCount = 0;
            taskStep = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = getValidNode(service, "出售");
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input(getPackageName() + "order_edit_text", String.valueOf(PreferenceHelp.getInt(Key.MONEY, 20000)))) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 2:
                if (service.clickFirst(getPackageName() + "place_order")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 3:
                if (service.input(getPackageName() + "pwd_edit_text", PreferenceHelp.getString(Key.PASSWORD, "123456"))) {
                    errorCount = 0;
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
        service.postDelayed(() -> saleOrder(service, coinBean, endCallback));
    }

    @Override
    public void toHuazhuan(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.isHomePage()) {
                    if (service.exeClickText("资产")) {
                        taskStep++;
                    }
                } else {
                    service.back();
                }
                break;
            case 1:
                if (service.exeClickText("划转")) {
                    taskStep++;
                }
                break;
            case 2:
                if (service.clickFirst(getPackageName() + "currency_title")) {
                    taskStep++;
                }
                break;
            case 3:
                if (service.exeClickText("USDT")) {
                    taskStep++;
                }
                break;
            case 4:
                if (service.clickFirst(getPackageName() + "tv_all")) {
                    taskStep++;
                }
                break;
            case 5:
                if (service.clickFirst(getPackageName() + "btn_action")) {
                    taskStep++;
                }
                break;
            case 6:
                if (service.clickFirst(getPackageName() + "dialog_confirm_btn")) {
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
        service.postDelayed(() -> toHuazhuan(service, endCallback));
    }

    @Override
    public void zhuanzhang(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.isHomePage()) {
                    if (service.exeClickText("资产")) {
                        taskStep++;
                    }
                } else {
                    service.back();
                }
                break;
            case 1:
                if (service.exeClickText("USDT")) {
                    taskStep++;
                }
                break;
            case 2:
                if (service.exeClickText("提币")) {
                    taskStep++;
                }
                break;
            case 3:
                if (service.exeClickText("TRC20")) {
                    taskStep++;
                }
                break;
            case 4:
                if (service.input(getPackageName() + "withdraw_address_edit_amount", TARGET_ADDRESS)) {
                    taskStep++;
                }
                break;
            case 5:
                if (service.clickFirst(getPackageName() + "tv_all")) {
                    taskStep++;
                }
                break;
            case 6:
                String withdraw = service.getNodeText(getPackageName() + "withdraw_edit_amount");
                String receive = service.getNodeText(getPackageName() + "tv_receive_amount");
                if (!TextUtils.isEmpty(withdraw) && withdraw.equals(receive)) {
                    taskStep++;
                } else {
                    errorCount = 3;
                }
                break;
            case 7:
                if (service.clickFirst(getPackageName() + "btn_action")) {
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
        service.postDelayed(() -> zhuanzhang(service, endCallback));
    }
}
