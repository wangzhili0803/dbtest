package com.jerry.baselib.common.okhttp.callback;

import com.jerry.baselib.common.okhttp.OkHttpUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class Callback<T> {

    protected String errorMsg = OkHttpUtils.ERROR_MSG;

    /**
     * UI Thread
     */
    public void onBefore(Request request) {}

    /**
     * UI Thread
     */
    public void onAfter() {}

    /**
     * UI Thread
     */
    public void inProgress(float progress) {

    }

    public T parseNetworkResponse(Response response) throws IOException {
        return null;
    }

    public void onError(Request request, Exception e) {}

    public void onResponse(T response) {}

    public void onError(String msg) {}

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response) {
            return null;
        }

        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }

        @Override
        public void onError(String msg) {

        }
    };

}