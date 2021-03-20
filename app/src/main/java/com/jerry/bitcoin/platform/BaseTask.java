
package com.jerry.bitcoin.platform;

import java.util.Collections;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.interfaces.TaskCallback;

import androidx.annotation.NonNull;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public abstract class BaseTask implements TaskCallback {

    public static final float MONEY_POOL_MAX = 30000;

    protected AVIMConversation mAvimConversation;

    /**
     * 币种
     */
    protected String coinType;
    /**
     * 0：出售，1：购买
     */
    protected int buyType;
    /**
     * 任务步骤
     */
    protected int taskStep;
    /**
     * 错误次数
     */
    protected int errorCount;

    public String getCoinType() {
        return coinType;
    }

    @Override
    public void setCoinType(final String coinType, OnDataChangedListener<AVIMConversation> onDataChangedListener) {
        this.coinType = coinType;
        openConversation(onDataChangedListener);
    }

    @Override
    public int getBuyType() {
        return buyType;
    }

    @Override
    public void setBuyType(final int buyType, OnDataChangedListener<AVIMConversation> onDataChangedListener) {
        this.buyType = buyType;
        openConversation(onDataChangedListener);
    }

    protected String getBuyTypeStr() {
        return buyType == TYPE_SELL ? "出售" : "购买";
    }

    protected String getAnotherTypeStr() {
        return coinType + (buyType == TYPE_SELL ? "购买" : "出售");
    }

    @Override
    public AVIMConversation getAvimConversation() {
        return mAvimConversation;
    }

    protected void openConversation(OnDataChangedListener<AVIMConversation> onDataChangedListener) {
        String connectId = coinType + getBuyTypeStr();
        LCChatKit.getInstance().open(connectId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (null != e) {
                    ToastUtil.showShortText(e.getMessage());
                    if (onDataChangedListener != null) {
                        onDataChangedListener.onDataChanged(mAvimConversation);
                    }
                    return;
                }
                ToastUtil.showShortText("设备已连接:" + connectId);
                LCChatKit.getInstance().getClient().createConversation(Collections.singletonList(getAnotherTypeStr()), "", null, false, true,
                    new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation avimConversation, AVIMException e) {
                            if (null != e) {
                                ToastUtil.showShortText(e.getMessage());
                            } else {
                                mAvimConversation = avimConversation;
                                LCIMConversationItemCache.getInstance().insertConversation(avimConversation.getConversationId());
                            }
                            if (onDataChangedListener != null) {
                                onDataChangedListener.onDataChanged(mAvimConversation);
                            }
                        }
                    });
            }
        });
    }

    protected abstract AccessibilityNodeInfo getValidNode(ListenerService service, @NonNull String nodeStr);

    public static double getMoneyPool() {
        return PreferenceHelp.getFloat("moneypool");
    }

    public static void getMoneyPool(float money) {
        PreferenceHelp.putFloat("moneypool", money);
    }
}
