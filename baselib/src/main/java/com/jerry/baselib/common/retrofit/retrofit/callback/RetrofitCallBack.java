package com.jerry.baselib.common.retrofit.retrofit.callback;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.common.retrofit.retrofit.response.BaseResponse;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.LogUtils;

import androidx.annotation.NonNull;

/**
 * Created by wzl on 2018/11/9.
 *
 * @Description T为传入类型：对象Data，列表List，content列表List，R为解析类，可以理解为适配器
 */
public class RetrofitCallBack<T> {

    public String cache;

    public RetrofitCallBack() {}

    public RetrofitCallBack(String cache) {
        this.cache = cache;
    }

    /**
     * UI Thread
     */
    public void onAfter() {}

    public void onResponse(T response) {}

    public void onError(@NonNull BaseResponse response) {}

    public void onProgress(float progress) {}

    public <R> T parseNetworkResponse(R response) {
        try {
            T baseResponse = (T) response;
            if (!TextUtils.isEmpty(cache)) {
                ListCacheUtil.saveValueToJsonFile(cache, JSON.toJSONString(baseResponse));
            }
            return baseResponse;
        } catch (Exception e) {
            LogUtils.w(e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
