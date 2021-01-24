package com.jerry.bitcoin.interfaces;

import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;

import cn.leancloud.im.v2.AVIMConversation;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public interface TaskCallback {

    /**
     * 监控出售
     */
    int TYPE_SELL = 0;
    /**
     * 监控购买
     */
    int TYPE_BUY = 1;

    String getPackageName();

    int getBuyType();

    void setBuyType(final int buyType, OnDataChangedListener<AVIMConversation> onDataChangedListener);

    void setCoinType(final String coinType, OnDataChangedListener<AVIMConversation> onDataChangedListener);

    /**
     * 获取购买币的信息
     */
    CoinBean getBuyInfo(ListenerService service);

    /**
     * 获取消息管理器
     */
    AVIMConversation getAvimConversation();

    /**
     * 断线重连
     */
    void openConversation(OnDataChangedListener<AVIMConversation> onDataChangedListener);
}
