package com.jerry.baselib.common.bean;

import java.util.Set;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description 用户表
 */
public class AxUser extends AVBaseObject {

    /**
     * ` 等级 100：总代理，10：代理 1：激活用户
     */
    private int level;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String passwd;
    /**
     * 自己的邀请码
     */
    private String userCode;
    /**
     * 邀请人的邀请码
     */
    private String from;
    /**
     * 设备号
     */
    private Set<String> devices;
    /**
     * 最大任务数
     */
    private int count = 1;
    /**
     * 微信号
     */
    private String wxCode;
    /**
     * 到期时间
     */
    private String expire;
    /**
     * 金额
     */
    private double amount;

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(final String passwd) {
        this.passwd = passwd;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(final String userCode) {
        this.userCode = userCode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public Set<String> getDevices() {
        return devices;
    }

    public void setDevices(final Set<String> devices) {
        this.devices = devices;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public String getWxCode() {
        return wxCode;
    }

    public void setWxCode(final String wxCode) {
        this.wxCode = wxCode;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(final String expire) {
        this.expire = expire;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }
}
