package com.jerry.baselib.common.retrofit.retrofit.response;

/**
 * Created by wzl on 16/4/22. 类说明: 返回数据的结构较复杂时的json解析基类
 */
public class Response4Data<T> extends BaseResponse {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
