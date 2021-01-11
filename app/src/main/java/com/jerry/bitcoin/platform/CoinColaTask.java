package com.jerry.bitcoin.platform;

import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
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
public class CoinColaTask extends BaseTask {

    public CoinColaTask() {
        coinType = "XRP";
        buyType = "购买";
        payType = "ali_pay";
    }

    @Override
    public String getPackageName() {
        return "com.newgo.coincola:id/";
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
                coinBean.setMin(ParseUtil.parseDouble(minMax[0]));
                coinBean.setMax(ParseUtil.parseDouble(minMax[1]));
                coinBean.setPrice(ParseUtil.parseDouble(priceStr));
                coinBean.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return coinBean;
    }

    @Override
    protected AccessibilityNodeInfo getValidNode(final ListenerService service) {
        List<AccessibilityNodeInfo> listViews = service.getRootInActiveWindow()
            .findAccessibilityNodeInfosByViewId(getPackageName() + "recycler_view_ad");
        if (!CollectionUtils.isEmpty(listViews)) {
            int targetIndex = 0;
            for (int i = 0; i < listViews.size(); i++) {
                AccessibilityNodeInfo listView = listViews.get(i);
                Rect rect = new Rect();
                listView.getBoundsInScreen(rect);
                String typeStr = service.getNodeText(listView, "coinUnit");
                String buyStr = service.getNodeText(listView, "buy_or_sell_btn");
                if (coinType.equals(typeStr) && buyType.equals(buyStr)) {
                    targetIndex = i;
                    break;
                }
            }
            AccessibilityNodeInfo list = listViews.get(targetIndex);
            for (int i = 0; i < list.getChildCount(); i++) {
                AccessibilityNodeInfo item = list.getChild(i);
                if (!CollectionUtils.isEmpty(item.findAccessibilityNodeInfosByViewId(getPackageName() + payType))) {
                    return item;
                }
            }
        }
        return null;
    }
}
