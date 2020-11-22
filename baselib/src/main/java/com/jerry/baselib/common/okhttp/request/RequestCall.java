package com.jerry.baselib.common.okhttp.request;

import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class RequestCall {

    private BaseOkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    public RequestCall(BaseOkHttpRequest request) {
        this.okHttpRequest = request;
    }

    private void generateCall(Callback callback) {
        request = okHttpRequest.generateRequest(callback);
        call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
    }

    public void enqueue(Callback callback) {
        generateCall(callback);
        if (callback != null) {
            callback.onBefore(request);
        }
        OkHttpUtils.getInstance().enqueue(this, callback);
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public BaseOkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute(){
        generateCall(null);
        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

}
