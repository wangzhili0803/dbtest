package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-05-04
 * @description
 */
public class ProLog extends AVBaseObject {

    private String device;
    private String uid;
    private String proInfo;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getProInfo() {
        return proInfo;
    }

    public void setProInfo(String proInfo) {
        this.proInfo = proInfo;
    }
}
