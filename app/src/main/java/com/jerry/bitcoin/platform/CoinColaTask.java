package com.jerry.bitcoin.platform;

import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.CoinBean;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
class CoinColaTask extends BaseTask {

    @Override
    public String getPackageName() {
        return "com.newgo.coincola";
    }

    @Override
    public CoinBean getBuyInfo(final ListenerService service) {
        return null;
    }
}
