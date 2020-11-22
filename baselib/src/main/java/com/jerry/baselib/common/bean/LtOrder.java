package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2020-03-03
 * @description
 */
public class LtOrder extends AVBaseObject {

    private String ltid;
    private String expire;
    private int money;

    public String getLtid() {
        return ltid;
    }

    public void setLtid(final String ltid) {
        this.ltid = ltid;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(final String expire) {
        this.expire = expire;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(final int money) {
        this.money = money;
    }
}
