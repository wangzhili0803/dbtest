package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-11-27
 * @description
 */
public class user extends AVBaseObject {

    private String ltid;
    private String xyid;
    private String expire;
    private int zdxdAuth;
    private int jgAuth;
    private int wantAuth;
    private int maxDevice;

    public String getLtid() {
        return ltid;
    }

    public void setLtid(final String ltid) {
        this.ltid = ltid;
    }

    public String getXyid() {
        return xyid;
    }

    public void setXyid(final String xyid) {
        this.xyid = xyid;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(final String expire) {
        this.expire = expire;
    }

    public int getZdxdAuth() {
        return zdxdAuth;
    }

    public void setZdxdAuth(final int zdxdAuth) {
        this.zdxdAuth = zdxdAuth;
    }

    public int getJgAuth() {
        return jgAuth;
    }

    public void setJgAuth(final int jgAuth) {
        this.jgAuth = jgAuth;
    }

    public int getWantAuth() {
        return wantAuth;
    }

    public void setWantAuth(final int wantAuth) {
        this.wantAuth = wantAuth;
    }

    public int getMaxDevice() {
        return maxDevice;
    }

    public void setMaxDevice(final int maxDevice) {
        this.maxDevice = maxDevice;
    }
}
