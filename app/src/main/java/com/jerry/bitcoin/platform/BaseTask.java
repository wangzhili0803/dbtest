
package com.jerry.bitcoin.platform;

import java.util.Collections;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.interfaces.TaskCallback;

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

    protected final CoinBean coinBean = new CoinBean();

    protected AVIMConversation mAvimConversation;

    /**
     * 币种
     */
    protected String coinType;
    /**
     * 0：出售，1：购买
     */
    protected int buyType;
    protected String payType;

    public CoinBean getCoinBean() {
        return coinBean;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(final String coinType) {
        this.coinType = coinType;
        mAvimConversation = null;
    }

    public int getBuyType() {
        return buyType;
    }

    public void setBuyType(final int buyType) {
        this.buyType = buyType;
        mAvimConversation = null;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(final String payType) {
        this.payType = payType;
    }

    protected String getBuyTypeStr() {
        return buyType == 0 ? "出售" : "购买";
    }

    protected String getAnotherTypeStr() {
        return buyType == 0 ? "购买" : "出售";
    }

    @Override
    public void getAvimConversation(OnDataChangedListener<AVIMConversation> onDataChangedListener) {
        if (mAvimConversation == null) {
            LCChatKit.getInstance().open(coinType + getBuyTypeStr(), new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null != e) {
                        ToastUtil.showShortText(e.getMessage());
                        onDataChangedListener.onDataChanged(mAvimConversation);
                        return;
                    }
                    ToastUtil.showShortText("设备已连接");
                    LCChatKit.getInstance().getClient()
                        .createConversation(Collections.singletonList(getAnotherTypeStr()), "", null, false, true,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation avimConversation, AVIMException e) {
                                    if (null != e) {
                                        ToastUtil.showShortText(e.getMessage());
                                    } else {
                                        mAvimConversation = avimConversation;
                                        LCIMConversationItemCache.getInstance().insertConversation(avimConversation.getConversationId());
                                    }
                                    onDataChangedListener.onDataChanged(mAvimConversation);
                                }
                            });
                }
            });
        } else {
            onDataChangedListener.onDataChanged(mAvimConversation);
        }
    }

    protected abstract AccessibilityNodeInfo getValidNode(ListenerService service);
}
