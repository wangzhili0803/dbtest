package com.jerry.baselib.common.okhttp.request;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Jerry
 * @createDate 2019-07-18
 * @description
 */
public class FileRequest extends BaseOkHttpRequest {

    public FileRequest(final String url) {
        super(url, null, null, null);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return new FormBody.Builder().build();
    }
}
