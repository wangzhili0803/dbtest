package com.jerry.bitcoin.beans;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoinBean)) {
            return false;
        }
        CoinBean coinBean = (CoinBean) o;
        return Double.compare(coinBean.getMin(), getMin()) == 0 &&
            Double.compare(coinBean.getMax(), getMax()) == 0 &&
            Double.compare(coinBean.getPrice(), getPrice()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMin(), getMax(), getPrice());
    }

    @Override
    public String toString() {
        return "CoinBean{" +
            "min=" + min +
            ", max=" + max +
            ", price=" + price +
            ", currentTimeMs=" + currentTimeMs +
            '}';
    }
}
