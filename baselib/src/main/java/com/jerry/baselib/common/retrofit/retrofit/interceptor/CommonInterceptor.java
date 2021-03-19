package com.jerry.baselib.common.retrofit.retrofit.interceptor;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author：OTMAGIC WeChat：Longalei888 Date：2018/6/11 Signature:每一个Bug修改,每一次充分思考,都会是一种进步. Describtion: 请求前拦截---添加公共参数/签名和验证字段
 */

public class CommonInterceptor implements Interceptor {

    /**
     * 添加签名时的公共参数公共参数
     */
    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldResponse = chain.request();
        HttpUrl.Builder authorizedUrlBuilder = oldResponse.url()
            .newBuilder()
            .scheme(oldResponse.url().scheme())
            .host(oldResponse.url().host());
        Request newRequest = oldResponse.newBuilder()
            .method(oldResponse.method(), oldResponse.body())
            .url(authorizedUrlBuilder.build())
            .build();
        return chain.proceed(newRequest);
    }
}
