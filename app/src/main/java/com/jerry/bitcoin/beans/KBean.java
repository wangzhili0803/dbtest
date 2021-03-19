package com.jerry.bitcoin.beans;

/**
 * @author Jerry
 * @createDate 3/6/21
 * @copyright www.aniu.tv
 * @description
 */
public class KBean {

    /**
     * id : 1499184000
     * amount : 37593.0266
     * count : 0
     * open : 1935.2
     * close : 1879
     * low : 1856
     * high : 1940
     * vol : 7.1031537978665E7
     */

    private long id;
    private double amount;
    private int count;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }
}
