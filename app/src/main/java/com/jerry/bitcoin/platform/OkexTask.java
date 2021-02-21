package com.jerry.bitcoin.platform;

import java.util.List;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.LogUtils;
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
public class OkexTask extends BaseTask {

    private static volatile OkexTask mInstance;

    private OkexTask() {
        coinType = PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.USDT);
        openConversation(null);
    }

    public static OkexTask getInstance() {
        if (mInstance == null) {
            synchronized (OkexTask.class) {
                if (mInstance == null) {
                    mInstance = new OkexTask();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getPackageName() {
        return "com.okinc.okex:id/";
    }

    @Override
    public CoinBean getBuyCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = this.getValidBuyNode(service);
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "tv_limit");
            String priceStr = service.getNodeText(validNode, getPackageName() + "tv_price");
            if (rationed != null && priceStr != null) {
                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                if (minMax.length == 2) {
                    mBuyCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                    mBuyCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                } else {
                    LogUtils.e("minMax is error :" + rationed);
                }
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
            String rationed = service.getNodeText(validNode, getPackageName() + "tv_limit");
            String priceStr = service.getNodeText(validNode, getPackageName() + "tv_price");
            if (rationed != null && priceStr != null) {
                rationed = rationed.trim().replace(Key.SPACE, Key.NIL).replace(Key.COMMA, Key.NIL).replace("¥", Key.NIL);
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                if (minMax.length == 2) {
                    mSaleCoin.setMin(ParseUtil.parse2Double(minMax[0]));
                    mSaleCoin.setMax(ParseUtil.parse2Double(minMax[1]));
                } else {
                    LogUtils.e("minMax is error :" + rationed);
                }
                mSaleCoin.setPrice(ParseUtil.parse2Double(priceStr));
                mSaleCoin.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return mSaleCoin;
    }

    @Override
    protected AccessibilityNodeInfo getValidBuyNode(ListenerService service) {
        return getValidNode(service, "去购买");
    }

    @Override
    protected AccessibilityNodeInfo getValidSaleNode(final ListenerService service) {
        return getValidNode(service, "去出售");
    }

    private AccessibilityNodeInfo getValidNode(final ListenerService service, @NonNull String nodeStr) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> listViews = rootNode.findAccessibilityNodeInfosByViewId(getPackageName() + "rv_buy_sell");
            if (!CollectionUtils.isEmpty(listViews)) {
                int targetIndex = 0;
                for (int i = 0; i < listViews.size(); i++) {
                    AccessibilityNodeInfo listView = listViews.get(i);
                    String coinType = service.getNodeText(listView, getPackageName() + "tv_num");
                    String buyStr = service.getNodeText(listView, getPackageName() + "tv_to_trade");
                    if (coinType.contains(this.coinType) && nodeStr.equals(buyStr)) {
                        targetIndex = i;
                        break;
                    }
                }
                AccessibilityNodeInfo list = listViews.get(targetIndex);
                return list.getChild(0);
            }
        }
        return null;
    }

    @Override
    public void buyOrder(final ListenerService service, final EndCallback endCallback) {
        if (errorCount >= 3) {
            taskStep = 0;
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = this.getValidBuyNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "tv_to_trade")) {
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
            default:
                taskStep = 0;
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
            taskStep = 0;
            endCallback.onEnd(false);
            return;
        }
        int tempStep = taskStep;
        switch (taskStep) {
            case 0:
                AccessibilityNodeInfo validNode = this.getValidSaleNode(service);
                if (validNode != null) {
                    if (service.exeClickId(validNode, getPackageName() + "tv_to_trade")) {
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
        service.postDelayed(() -> saleOrder(service, endCallback));
    }
}
