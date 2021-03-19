package com.jerry.baselib.common.retrofit.retrofit.calladapter;

import com.jerry.baselib.common.retrofit.RetrofitHelper;
import com.jerry.baselib.common.retrofit.retrofit.callback.RetrofitCallBack;

import retrofit2.Call;

/**
 * Created by wzl on 2018/11/5.
 *
 * @Description 自定义call-接受列表
 */
public class RCall<R> {

    public Call<R> call;

    public Call<R> getCall() {
        return call;
    }

    public RCall(Call<R> call) {
        this.call = call;
    }

    public <T> void execute(RetrofitCallBack<T> callback) {
        execute(null, callback);
    }

    public <T> void execute(Object tag, RetrofitCallBack<T> callback) {
        RetrofitHelper.getInstance().execute(tag, call, callback);
    }
}