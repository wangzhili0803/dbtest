package com.jerry.bitcoin.platform;

import java.util.List;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class HuobiTask extends BaseTask {

    @Override
    public String getPackageName() {
        return "pro.huobi:id/";
    }

    @Override
    public CoinBean getBuyInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = getValidNode(service);
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, "rationedExchangeVol");
            String priceStr = service.getNodeText(validNode, "unitPriceValue");
            if (rationed != null && priceStr != null) {
                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                coinBean.setMin(ParseUtil.parse2Double(minMax[0]));
                coinBean.setMax(ParseUtil.parse2Double(minMax[1]));
                coinBean.setPrice(ParseUtil.parse2Double(priceStr));
                coinBean.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return coinBean;
    }

    @Override
    protected AccessibilityNodeInfo getValidNode(ListenerService service) {
        List<AccessibilityNodeInfo> listViews = service.getRootInActiveWindow()
            .findAccessibilityNodeInfosByViewId(getPackageName() + "list_view");
        if (!CollectionUtils.isEmpty(listViews)) {
            int targetIndex = 0;
            for (int i = 0; i < listViews.size(); i++) {
                AccessibilityNodeInfo listView = listViews.get(i);
                String typeStr = service.getNodeText(listView, "coinUnit");
                String buyStr = service.getNodeText(listView, "buy_or_sell_btn");
                if (coinType.equals(typeStr) && getBuyTypeStr().equals(buyStr)) {
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
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = getValidNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input("order_edit_text", "5000")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 2:
                if (service.clickFirst("place_order")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 3:
            default:
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
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = getValidNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, "buy_or_sell_btn")) {
                        errorCount = 0;
                        taskStep++;
                    }
                }
                break;
            case 1:
                if (service.input("order_edit_text", "5000")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 2:
                if (service.clickFirst("place_order")) {
                    errorCount = 0;
                    taskStep++;
                }
                break;
            case 3:
            default:
                endCallback.onEnd(true);
                return;
        }
        if (tempStep == taskStep) {
            errorCount++;
        }
        service.postDelayed(() -> saleOrder(service, endCallback));
    }
}
