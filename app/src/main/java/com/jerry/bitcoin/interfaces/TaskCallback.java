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

    String getPackageName();

    /**
     * 获取购买币的信息
     */
    CoinBean getBuyInfo(ListenerService service);

    /**
     * 获取消息管理器
     */
    void getAvimConversation(OnDataChangedListener<AVIMConversation> onDataChangedListener);
}
