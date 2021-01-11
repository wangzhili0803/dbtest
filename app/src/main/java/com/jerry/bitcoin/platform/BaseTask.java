
package com.jerry.bitcoin.platform;

import com.jerry.bitcoin.beans.CoinBean;
import com.jerry.bitcoin.interfaces.TaskCallback;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public abstract class BaseTask implements TaskCallback {

    protected final CoinBean coinBean = new CoinBean();

    protected String coinType;
    protected String buyType;
    protected String payType;

    public CoinBean getCoinBean() {
        return coinBean;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(final String coinType) {
        this.coinType = coinType;
    }

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(final String buyType) {
        this.buyType = buyType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(final String payType) {
        this.payType = payType;
    }
}
