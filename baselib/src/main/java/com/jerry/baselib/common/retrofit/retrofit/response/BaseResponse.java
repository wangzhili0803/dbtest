package com.jerry.baselib.common.retrofit.retrofit.response;

import com.jerry.baselib.Key;

public class BaseResponse {

    public boolean isOk() {// 请求成功
        return Key.OK.equals(status);
    }

    private String ch;
    private long ts;
    private String status;
    private String msg;

    public String getCh() {
        return ch;
    }

    public void setCh(final String ch) {
        this.ch = ch;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(final long ts) {
        this.ts = ts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }
}