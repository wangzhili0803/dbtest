package com.jerry.baselib.common.bean;

public class DataResponse<T> {

    public boolean isOk() {// 请求成功
        return code == 0;
    }

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}