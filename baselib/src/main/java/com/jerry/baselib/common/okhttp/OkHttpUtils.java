package com.jerry.baselib.common.okhttp;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.os.Looper;
import android.text.TextUtils;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.common.okhttp.builder.FileBuilder;
import com.jerry.baselib.common.okhttp.builder.GetBuilder;
import com.jerry.baselib.common.okhttp.callback.Callback;
import com.jerry.baselib.common.okhttp.cookie.SimpleCookieJar;
import com.jerry.baselib.common.okhttp.request.RequestCall;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.NetworkUtil;
import com.jerry.baselib.common.util.WeakHandler;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {

    private static final int TIMEOUT_MILLISECONDS = 15000;// default:10s
    public static final String ERROR_MSG = "ERROR";
    private static final String TIMEOUT_MSG = "超时啦，请刷新重试！";
    private static final String TAG = "OkHttpUtils";
    private static final String BROKEN_MSG = "网络不给力，请检查网络连接后再试！";
    private static final int CACHE_SIZE = 10 * 1024 * 1024;// 10M
    private static volatile OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private WeakHandler mDelivery;
    private boolean debug = BaseApp.Config.DEBUG;
    private String tag;

    private OkHttpUtils() {
        mDelivery = new WeakHandler(Looper.getMainLooper());

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.proxy(Proxy.NO_PROXY);
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());// cookie enabled
        okHttpClientBuilder.connectTimeout(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);

        Cache cache = new Cache(BaseApp.getInstance().getCacheDir(), CACHE_SIZE);
        okHttpClientBuilder.cache(cache);
        okHttpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request()
                .newBuilder()
                .removeHeader("AxUser-Agent")//移除旧的
                .addHeader("AxUser-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")//添加真正的头部
                .build();
            return chain.proceed(request);
        });
        mOkHttpClient = okHttpClientBuilder.build();
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static FileBuilder file() {
        return new FileBuilder();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public WeakHandler getDelivery() {
        return mDelivery;
    }

    public void enqueue(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            LogUtils.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    sendFailResultCallback(TIMEOUT_MSG, finalCallback);
                } else {
                    if (NetworkUtil.isNetworkAvailable(false)) {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } else {
                        sendFailResultCallback(BROKEN_MSG, finalCallback);
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Object o = finalCallback.parseNetworkResponse(response);
                        sendSuccessResultCallback(o, finalCallback);
                    } catch (Throwable e) {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } finally {
                        FileUtil.close(response.body());
                    }
                }
            }
        });
    }

    public void sendFailResultCallback(final String msg, final Callback callback) {
        if (callback == null) {
            return;
        }

        mDelivery.post(() -> {
            callback.onError(msg);
            callback.onAfter();
        });
    }

    public void sendProgressCallback(final float progress, final Callback callback) {
        if (callback == null) {
            return;
        }
        mDelivery.post(() -> callback.inProgress(progress));
    }

    @SuppressWarnings("unchecked")
    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) {
            return;
        }
        mDelivery.post(() -> {
            callback.onResponse(object);
            callback.onAfter();
        });
    }

    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public String getSessionId() {
        SimpleCookieJar mSimpleCookieJar = (SimpleCookieJar) mOkHttpClient.cookieJar();
        List<Cookie> cookies = mSimpleCookieJar.getAllCookies();
        if (CollectionUtils.isEmpty(cookies)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("PHPSESSID".equals(cookie.name())) {
                return cookie.value();
            }
        }
        return null;
    }
}
