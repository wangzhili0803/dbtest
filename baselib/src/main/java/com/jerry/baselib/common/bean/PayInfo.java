package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-06-12
 * @description
 */
public class PayInfo {

    /**
     * 商品名称
     */
    private String tradename;
    /**
     * 商户系统订单号(商户系统内唯一)
     */
    private String outtradeno;
    /**
     * 商品价格（单位：分。如1.5元传150）
     */
    private long amount;
    /**
     * 商户系统回调参数
     */
    private String backparams;
    /**
     * 商户系统回调地址
     */
    private String notifyurl;
    /**
     * 商户系统用户ID(如：trpay@52yszd.com，商户系统内唯一)
     */
    private String userid;

    public String getTradename() {
        return tradename;
    }

    public void setTradename(final String tradename) {
        this.tradename = tradename;
    }

    public String getOuttradeno() {
        return outtradeno;
    }

    public void setOuttradeno(final String outtradeno) {
        this.outtradeno = outtradeno;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(final long amount) {
        this.amount = amount;
    }

    public String getBackparams() {
        return backparams;
    }

    public void setBackparams(final String backparams) {
        this.backparams = backparams;
    }

    public String getNotifyurl() {
        return notifyurl;
    }

    public void setNotifyurl(final String notifyurl) {
        this.notifyurl = notifyurl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(final String userid) {
        this.userid = userid;
    }
}
