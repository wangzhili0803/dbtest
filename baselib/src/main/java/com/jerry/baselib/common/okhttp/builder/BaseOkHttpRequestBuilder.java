package com.jerry.baselib.common.okhttp.builder;

import java.util.Map;
import java.util.TreeMap;

import com.jerry.baselib.common.okhttp.request.RequestCall;


public abstract class BaseOkHttpRequestBuilder {

    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected TreeMap<String, String> params;

    public BaseOkHttpRequestBuilder headers(TreeMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public BaseOkHttpRequestBuilder params(TreeMap<String, String> params) {
        this.params = params;
        return this;
    }

    public abstract RequestCall build();

}
