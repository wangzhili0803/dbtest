package com.jerry.bitcoin.platform;

import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.CoinConstant;

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
    public CoinBean getCoinInfo(final ListenerService service) {
        AccessibilityNodeInfo validNode = getValidNode(service);
        if (validNode != null) {
            String rationed = service.getNodeText(validNode, getPackageName() + "tv_limit");
            String priceStr = service.getNodeText(validNode, getPackageName() + "tv_price");
            if (rationed != null && priceStr != null) {
                rationed = rationed.replace("限额", Key.NIL).replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim();
                String[] minMax = StringUtil.safeSplit(rationed, Key.LINE);
                coinBean.setMin(ParseUtil.parse2Double(minMax[0]));
                coinBean.setMax(ParseUtil.parse2Double(minMax[1]));
                coinBean.setPrice(ParseUtil.parse2Double(priceStr.replace(Key.COMMA, Key.NIL).replace("CNY", Key.NIL).trim()));
                coinBean.setCurrentTimeMs(System.currentTimeMillis());
            }
        }
        return coinBean;
    }

    @Override
    protected AccessibilityNodeInfo getValidNode(final ListenerService service) {
        List<AccessibilityNodeInfo> tvOperators = service.getRootInActiveWindow()
            .findAccessibilityNodeInfosByViewId(getPackageName() + "tv_operator");
        if (!CollectionUtils.isEmpty(tvOperators)) {
            for (int i = 0; i < tvOperators.size(); i++) {
                AccessibilityNodeInfo tvOperator = tvOperators.get(i);
                // 购买类型一致
                if (getBuyTypeStr().equals(tvOperator.getText().toString())) {
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
    public void buyOrder(final ListenerService service, final EndCallback endCallback) {

    }

    @Override
    public void saleOrder(final ListenerService service, final EndCallback endCallback) {

    }
}
