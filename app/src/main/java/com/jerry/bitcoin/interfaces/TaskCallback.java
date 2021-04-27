package com.jerry.bitcoin.interfaces;

import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4Data;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.beans.TransformInfo;

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

    void setBuyType(final int buyType, OnDataCallback<AVIMConversation> onDataCallback);

    void setCoinType(final String coinType, OnDataCallback<AVIMConversation> onDataCallback);

    /**
     * 获取购买币的信息
     */
    CoinBean getBuyCoinInfo(ListenerService service);

    /**
     * 获取购买币的信息
     */
    CoinBean getSaleCoinInfo(ListenerService service);

    /**
     * 获取消息管理器
     */
    AVIMConversation getAvimConversation();

    /**
     * 购买下单
     */
    void buyOrder(ListenerService service, CoinBean coinBean, EndCallback endCallback);

    /**
     * 出售下单
     */
    void sellOrder(ListenerService service, CoinBean coinBean, EndCallback endCallback);

    /**
     * 划转
     */
    void charge(ListenerService service, EndCallback endCallback);

    /**
     * 支付
     */
    void pay(ListenerService listenerService, EndCallback endCallback);

    /**
     * 添加并获取弹框
     */
    void tryBuy(ListenerService listenerService, EndCallback endCallback);

    /**
     * 检查是否可以继续交易
     */
    void checkContinuePay(ListenerService listenerService, OnDataCallback<Response4Data<TransformInfo>> endCallback);
}
