package com.jerry.bitcoin.beans;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class CoinBean {

    private double min;
    private double max;
    private double price;
    private long currentTimeMs;

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

    public long getCurrentTimeMs() {
        return currentTimeMs;
    }

    public void setCurrentTimeMs(final long currentTimeMs) {
        this.currentTimeMs = currentTimeMs;
    }
}
