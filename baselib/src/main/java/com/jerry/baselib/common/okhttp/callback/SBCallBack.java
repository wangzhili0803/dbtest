package com.jerry.baselib.common.okhttp.callback;

import java.io.IOException;

import android.text.TextUtils;

import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.LogUtils;

import okhttp3.Response;

/**
 * Created by wzl on 2018/9/8.
 *
 * @Description 直接返回一个对象，毫无规范可言
 * 大单，月大单
 */
public class SBCallBack<T> extends Callback<T> {

    private Class<T> mClass;
    private String cache;

    public SBCallBack(Class<T> tClass) {
        mClass = tClass;
    }

    public SBCallBack(Class<T> tClass, String cache) {
        mClass = tClass;
        this.cache = cache;
    }

    @Override
    public T parseNetworkResponse(Response response) throws IOException {
        try {
            String content = response.body().string();
            LogUtils.d(content);
            T parseObject = JJSON.parseObject(content, AppUtils.type(mClass));
            if (!TextUtils.isEmpty(cache) && !TextUtils.isEmpty(content)) {
                ListCacheUtil.saveValueToJsonFile(cache, content);
            }
            return parseObject;
        } catch (Exception e) {
            LogUtils.w(e.toString());
            e.printStackTrace();
            OkHttpUtils.getInstance().sendFailResultCallback(errorMsg, this);
        }
        return super.parseNetworkResponse(response);
    }
}
