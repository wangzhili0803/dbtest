package com.jerry.baselib.common.retrofit.retrofit.callback;

import java.util.List;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4List;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.LogUtils;

/**
 * Created by wzl on 2018/11/9.
 *
 * @Description 数据在data里
 */
public class Callback4List<T> extends RetrofitCallBack<List<T>> {

    public Callback4List() {
    }

    public Callback4List(String cache) {
        this.cache = cache;
    }

    @Override
    public <R> List<T> parseNetworkResponse(final R response) {
        try {
            Response4List<T> baseResponse = (Response4List<T>) response;
            if (baseResponse.isOk()) {
                if (!TextUtils.isEmpty(cache)) {
                    ListCacheUtil.saveValueToJsonFile(cache, JSON.toJSONString(response));
                }
                return baseResponse.getData();
            }
        } catch (Exception e) {
            LogUtils.w(e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
