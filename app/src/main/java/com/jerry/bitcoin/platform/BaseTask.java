
package com.jerry.bitcoin.platform;

import java.util.Collections;
import java.util.List;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.baselib.common.util.Patterns;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.TransformInfo;
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

    protected static final String TARGET_ADDRESS = "TRe1xeKfrkTHXAKdJg4nzE6FunJ9k1DWVa";

    public static final float MONEY_POOL_MAX = 30000;

    protected AVIMConversation mAvimConversation;

    protected List<String> blackList;

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
    /**
     * 转账信息
     */
    protected TransformInfo mTransformInfo;

    public String getCoinType() {
        return coinType;
    }

    @Override
    public void setCoinType(final String coinType, OnDataCallback<AVIMConversation> onDataCallback) {
        this.coinType = coinType;
        openConversation(onDataCallback);
    }

    @Override
    public int getBuyType() {
        return buyType;
    }

    @Override
    public void setBuyType(final int buyType, OnDataCallback<AVIMConversation> onDataCallback) {
        this.buyType = buyType;
        openConversation(onDataCallback);
    }

    protected String getBuyTypeStr() {
        return buyType == TYPE_SELL ? "出售" : "购买";
    }

    protected String getAnotherTypeStr() {
        return coinType + (buyType == TYPE_SELL ? "购买" : "出售");
    }

    public void release() {
        taskStep = 0;
        errorCount = 0;
    }

    @Override
    public AVIMConversation getAvimConversation() {
        return mAvimConversation;
    }

    protected void openConversation(OnDataCallback<AVIMConversation> onDataCallback) {
        String connectId = coinType + getBuyTypeStr();
        LCChatKit.getInstance().open(connectId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (null != e) {
                    ToastUtil.showShortText(e.getMessage());
                    if (onDataCallback != null) {
                        onDataCallback.onDataCallback(mAvimConversation);
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
                            if (onDataCallback != null) {
                                onDataCallback.onDataCallback(mAvimConversation);
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

    protected TransformInfo parseTransformInfo(final String infoStr) {
        int preCardIndex = -1;
        int nextCardIndex = -1;
        String name = null;
        String bank = null;
        StringBuilder numberSb = new StringBuilder();
        String[] splits = StringUtil.safeSplit(infoStr, Key.SPACE);
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            if (Patterns.isNumber(split)) {
                if (preCardIndex == -1 && i > 0) {
                    preCardIndex = i - 1;
                }
                nextCardIndex = i + 1;
                numberSb.append(split);
            } else {
                if (split.contains("姓名")) {
                    int index = split.indexOf(Key.COLON);
                    if (index > -1) {
                        name = split.substring(index);
                    } else {
                        index = split.indexOf("：");
                        if (index > -1) {
                            name = split.substring(index);
                        }
                    }
                } else if (split.contains("银行")) {
                    int index = split.indexOf(Key.COLON);
                    if (index > -1) {
                        bank = split.substring(index);
                    } else {
                        index = split.indexOf("：");
                        if (index > -1) {
                            bank = split.substring(index);
                        }
                    }
                }
            }
        }
        if (TextUtils.isEmpty(name)) {
            boolean likePre = false;
            if (preCardIndex >= 0 && preCardIndex < splits.length) {
                if (splits[preCardIndex].length() == 2 || splits[preCardIndex].length() == 3) {
                    if (!splits[preCardIndex].endsWith("行")) {
                        likePre = true;
                    }
                }
            }
            boolean likeNext = false;
            if (nextCardIndex >= 0 && nextCardIndex < splits.length) {
                if (splits[nextCardIndex].length() == 2 || splits[nextCardIndex].length() == 3) {
                    if (!splits[nextCardIndex].endsWith("行")) {
                        likeNext = true;
                    }
                }
            }
            if (likePre && !likeNext) {
                name = splits[preCardIndex];
            } else if (!likePre && likeNext) {
                name = splits[nextCardIndex];
            }
        }
        if (TextUtils.isEmpty(bank)) {
            if (splits[preCardIndex].endsWith("行")) {
                bank = splits[preCardIndex];
            } else if (splits[nextCardIndex].endsWith("行")) {
                bank = splits[nextCardIndex];
            }
        }
        String number = numberSb.toString();
        if (number.startsWith("62") && number.length() == 19) {
            if (name != null && (name.length() == 2 || name.length() == 3)) {
                TransformInfo transformInfo = new TransformInfo();
                transformInfo.setNumber(number);
                transformInfo.setName(name);
                transformInfo.setBank(bank);
                return transformInfo;
            }
        }
        return null;
    }
}
