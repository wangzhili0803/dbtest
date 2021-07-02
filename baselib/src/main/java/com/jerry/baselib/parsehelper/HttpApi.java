package com.jerry.baselib.parsehelper;

import com.alibaba.fastjson.JSONObject;

import retrofit2.http.GET;
import rx.Observable;

/**
 *
 */
public interface HttpApi {


    String API = "http://api.m.taobao.com/rest/";// 专家团

    /**
     * 获取所有交易对
     */
    @GET("api3.do?api=mtop.common.getTimestamp")
    Observable<JSONObject> getTimestamp();
}