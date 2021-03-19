package com.jerry.bitcoin.beans;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jerry
 * @createDate 3/1/21
 * @copyright www.aniu.tv
 * @description
 */
public class AnalyzeBean {

    /**
     * base-currency : ar
     * quote-currency : btc
     * price-precision : 8
     * amount-precision : 2
     * symbol-partition : innovation
     * symbol : arbtc
     * state : online
     * value-precision : 8
     * min-order-amt : 0.001
     * max-order-amt : 330000
     * min-order-value : 1.0E-4
     * limit-order-min-order-amt : 0.001
     * limit-order-max-order-amt : 330000
     * sell-market-min-order-amt : 0.001
     * sell-market-max-order-amt : 33000
     * buy-market-max-order-value : 9
     * api-trading : enabled
     * super-margin-leverage-ratio : 3
     * leverage-ratio : 2
     * max-order-value : 1000
     * underlying : ethusdt
     * mgmt-fee-rate : 0.035
     * charge-time : 23:55:00
     * rebal-time : 00:00:00
     * rebal-threshold : -5
     * init-nav : 10
     * funding-leverage-ratio : 3
     */

    @SerializedName("base-currency")
    private String basecurrency;
    @SerializedName("quote-currency")
    private String quotecurrency;

    public String getBasecurrency() {
        return basecurrency;
    }

    public void setBasecurrency(String basecurrency) {
        this.basecurrency = basecurrency;
    }

    public String getQuotecurrency() {
        return quotecurrency;
    }

    public void setQuotecurrency(String quotecurrency) {
        this.quotecurrency = quotecurrency;
    }
}
