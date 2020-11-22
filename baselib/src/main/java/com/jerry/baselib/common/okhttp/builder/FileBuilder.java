package com.jerry.baselib.common.okhttp.builder;

import com.jerry.baselib.common.okhttp.request.FileRequest;
import com.jerry.baselib.common.okhttp.request.RequestCall;


public class FileBuilder extends BaseOkHttpRequestBuilder {


    @Override
    public RequestCall build() {
        return new FileRequest(url).build();
    }

    public FileBuilder url(String url) {
        this.url = url;
        return this;
    }

    public FileBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }
}
