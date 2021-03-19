package com.jerry.baselib.common.retrofit.retrofit.callback;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4Data;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.LogUtils;

/**
 * Created by wzl on 2018/11/9.
 *
 * @Description 数据在data里
 */
public class Callback4Data<T> extends RetrofitCallBack<T> {

    public Callback4Data() {}

    public Callback4Data(String cache) {
        this.cache = cache;
    }

    @Override
    public <R> T parseNetworkResponse(final R response) {
        try {
            Response4Data<T> baseResponse = (Response4Data<T>) response;
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
