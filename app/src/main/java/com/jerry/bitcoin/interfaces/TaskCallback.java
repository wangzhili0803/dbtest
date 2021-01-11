package com.jerry.bitcoin.interfaces;

import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;

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

}
