package com.jerry.bitcoin.beans;

import com.jerry.baselib.common.bean.AVBaseObject;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class CoinBean extends AVBaseObject {

    private double min;
    private double max;
    private double price;
    private double shouldTrade;
    private long currentTimeMs;
    private Object tag;

    public double getMin() {
        return min;
    }

    public void setMin(final double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(final double max) {
        this.max = max;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public double getShouldTrade() {
        return shouldTrade;
    }

    public void setShouldTrade(final double shouldTrade) {
        this.shouldTrade = shouldTrade;
    }

    public long getCurrentTimeMs() {
        return currentTimeMs;
    }

    public void setCurrentTimeMs(final long currentTimeMs) {
        this.currentTimeMs = currentTimeMs;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(final Object tag) {
        this.tag = tag;
    }
}
