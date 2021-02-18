package com.jerry.bitcoin.platform;

import java.util.List;

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
        AccessibilityNodeInfo validNode = this.getValidBuyNode(service);
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "rationedExchangeVol");
            String priceStr = service.getNodeText(validNode, getPackageName() + "unitPriceValue");
            if (rationed != null && priceStr != null) {
                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                mBuyCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                mBuyCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                mBuyCoin.setPrice(ParseUtil.parse2Double(priceStr));
                mBuyCoin.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return mBuyCoin;
    }

    @Override
    public CoinBean getSaleCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = this.getValidSaleNode(service);
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "rationedExchangeVol");
            String priceStr = service.getNodeText(validNode, getPackageName() + "unitPriceValue");
            if (rationed != null && priceStr != null) {
                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                mSaleCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                mSaleCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                mSaleCoin.setPrice(ParseUtil.parse2Double(priceStr));
                mSaleCoin.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return mSaleCoin;
    }

    @Override
    protected AccessibilityNodeInfo getValidBuyNode(ListenerService service) {
        return getValidNode(service, "购买");
    }

    @Override
    protected AccessibilityNodeInfo getValidSaleNode(final ListenerService service) {
        return getValidNode(service, "出售");
    }

    private AccessibilityNodeInfo getValidNode(final ListenerService service, @NonNull String nodeStr) {
        List<AccessibilityNodeInfo> listViews = service.getRootInActiveWindow()
            .findAccessibilityNodeInfosByViewId(getPackageName() + "list_view");
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
            return list.getChild(0);
        }
        return null;
    }

    @Override
    public void buyOrder(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep= 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = this.getValidBuyNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input(getPackageName() + "order_edit_text", "5000")) {
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
                taskStep= 0;
                errorCount = 0;
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> buyOrder(service, endCallback));
    }

    @Override
    public void saleOrder(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            errorCount = 0;
            taskStep= 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = this.getValidSaleNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input(getPackageName() + "order_edit_text", "5000")) {
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
                taskStep= 0;
                errorCount = 0;
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> saleOrder(service, endCallback));
    }
}
