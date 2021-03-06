package com.jerry.bitcoin.platform;

import java.util.List;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4Data;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.beans.TransformInfo;

import androidx.annotation.NonNull;
import cn.leancloud.json.JSON;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class HuobiTask extends BaseTask {

    private static volatile HuobiTask mInstance;
    protected static String BLACK_LIST = "black_list";

    private HuobiTask() {
        blackList = JJSON.parseArray(ListCacheUtil.getValueFromJsonFile(BLACK_LIST));
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
                    Double usdtPrice = ListenerService.usdtPrices.get(CoinConstant.HUOBI);
                    if (usdtPrice != null && price <= usdtPrice) {
                        // 成交量大于1000
                        String merchant = service.getNodeText(validNode, getPackageName() + "merchantName");
                        if (blackList == null || !blackList.contains(merchant)) {
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
                    Double usdtPrice = ListenerService.usdtPrices.get(CoinConstant.HUOBI);
                    if (usdtPrice != null && price > usdtPrice) {
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
                String desc = service.getNodeText(getPackageName() + "id_desc_content_tv");
                if (desc.contains("流水") || desc.contains("明细")) {
                    errorCount = 3;
                    String name = service.getNodeText(getPackageName() + "id_desc_title_tv");
                    int spaceIndex = name.indexOf(Key.SPACE);
                    if (spaceIndex > 0) {
                        blackList.add(name.substring(0, spaceIndex));
                    } else {
                        blackList.add(name);
                    }
                    ListCacheUtil.saveValueToJsonFile(BLACK_LIST, JSON.toJSONString(blackList));
                }
                break;
            case 2:
                if (service.input(getPackageName() + "order_edit_text", String.valueOf(coinBean.getShouldTrade()))) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 3:
                if (service.clickFirst(getPackageName() + "place_order")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 4:
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
    public void sellOrder(final ListenerService service, CoinBean coinBean, final EndCallback endCallback) {
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
        service.postDelayed(() -> sellOrder(service, coinBean, endCallback));
    }

    @Override
    public void charge(final ListenerService service, final EndCallback endCallback) {
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
        service.postDelayed(() -> charge(service, endCallback));
    }

    public void transfer(final ListenerService service, final String coinType, final EndCallback endCallback) {
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
        service.postDelayed(() -> transfer(service, coinType, endCallback));
    }

    @Override
    public void pay(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.exeClickText("去付款")) {
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
        service.postDelayed(() -> pay(service, endCallback));
    }

    @Override
    public void tryBuy(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.hasText("0手续费购买")) {
                    if (service.input(getPackageName() + "otc_fast_trade_input_edit", "7000")) {
                        taskStep++;
                    }
                }
                break;
            case 1:
                AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
                if (rootNode != null) {
                    List<AccessibilityNodeInfo> accessibilityNodeInfos = rootNode
                        .findAccessibilityNodeInfosByViewId(getPackageName() + "otc_fast_trade_coin_unit_txt");
                    AccessibilityNodeInfo gp = null;
                    for (int i = 0; i < accessibilityNodeInfos.size(); i++) {
                        AccessibilityNodeInfo info = accessibilityNodeInfos.get(i);
                        CharSequence coinType = info.getText();
                        if (coinType != null && CoinConstant.USDT.equals(coinType.toString())) {
                            gp = info.getParent().getParent();
                            break;
                        }
                    }
                    if (service.exeClickId(gp, getPackageName() + "otc_fast_trade_submit_txt")) {
                        taskStep++;
                    }
                }
                break;
            case 2:
                String priceStr = service.getNodeText(getPackageName() + "per_price");
                int spaceIndex = priceStr.indexOf(Key.SPACE);
                if (spaceIndex > 0) {
                    double price = ParseUtil.parseDouble(priceStr.substring(0, spaceIndex));
                    Double usdtPrice = ListenerService.usdtPrices.get(CoinConstant.HUOBI);
                    if (price > 0 && usdtPrice != null && price < usdtPrice) {
                        if (service.clickLast(getPackageName() + "buy_rl")) {
                            taskStep++;
                        }
                    } else {
                        service.back();
                        taskStep = 0;
                        errorCount = 0;
                    }
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
        service.postDelayed(() -> tryBuy(service, endCallback));
    }

    @Override
    public void checkContinuePay(final ListenerService service, final OnDataCallback<Response4Data<TransformInfo>> endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onDataCallback(null);
            return;
        }
        String infoStr = service.getNodeText(getPackageName() + "otc_chat_otherside_content");
        if (!TextUtils.isEmpty(infoStr) && (infoStr.contains("明细") || infoStr.contains("流水") || infoStr.contains("截图"))) {
            taskStep = 0;
            errorCount = 0;
            String msg = service.getNodeText(getPackageName() + "otc_chat_dealer_name");
            service.back();
            service.postDelayed(() -> {
                if (service.clickLast(getPackageName() + "id_cancel_refuse_receive_order_tv")) {
                    Response4Data<TransformInfo> response4Data = new Response4Data<>();
                    response4Data.setCode(1);
                    response4Data.setMsg(msg);
                    endCallback.onDataCallback(response4Data);
                }
            });
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                if (service.clickLast(getPackageName() + "id_contact_iv")) {
                    taskStep++;
                }
                break;
            case 1:
                if (service.input(getPackageName() + "otc_chat_input", "您好老板，给下您的支付信息")) {
                    taskStep++;
                }
                break;
            case 2:
            case 5:
                if (service.clickLast(getPackageName() + "send_txt")) {
                    taskStep++;
                }
                break;
            case 3:
                mTransformInfo = parseTransformInfo(service.getNodeText(getPackageName() + "otc_chat_otherside_content"));
                if (mTransformInfo == null) {
                    errorCount--;
                } else {
                    String orderStatus = service.getNodeText(getPackageName() + "otc_chat_order_status");
                    if ("待接单".equals(orderStatus)) {
                        taskStep++;
                    } else {
                        taskStep = taskStep + 3;
                    }
                }
                break;
            case 4:
                if (service.input(getPackageName() + "otc_chat_input", "OK，请接单")) {
                    taskStep++;
                }
                break;
            case 6:
                String orderStatus = service.getNodeText(getPackageName() + "otc_chat_order_status");
                if ("待接单".equals(orderStatus)) {
                    errorCount--;
                } else {
                    taskStep++;
                }
                break;
            case 7:
            default:
                taskStep = 0;
                errorCount = 0;
                Response4Data<TransformInfo> response4Data = new Response4Data<>();
                response4Data.setCode(1);
                response4Data.setData(mTransformInfo);
                endCallback.onDataCallback(response4Data);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> checkContinuePay(service, endCallback));
    }
}
