package com.jerry.baselib.common.retrofit;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import android.util.ArrayMap;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.retrofit.retrofit.calladapter.RCallFactory;
import com.jerry.baselib.common.retrofit.retrofit.callback.RetrofitCallBack;
import com.jerry.baselib.common.retrofit.retrofit.interceptor.CommonInterceptor;
import com.jerry.baselib.common.retrofit.retrofit.interceptor.LoggingInterceptor;
import com.jerry.baselib.common.retrofit.retrofit.response.BaseResponse;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.NetworkUtil;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author：zmf Date：2018/6/4 Describtion: 网络工具配置  dzcj接口地址的前缀有种，可以尝试用建造者的方式去构建请求，配置Base_URL和签名管理
 */

public class RetrofitHelper {

    private static String ERROR_MSG = Key.NIL;
    private static String TIMEOUT_MSG = "超时啦，请刷新重试！";
    private static String BROKEN_MSG = "网络不给力，请检查网络连接后再试！";
    private volatile static RetrofitHelper mInstance;
    /**
     * 记录业务的Api类
     */
    private ArrayMap<Class<?>, Object> map = new ArrayMap<>();
    /**
     * 记录正在运行的请求
     */
    private ConcurrentHashMap<Object, Call> callMap = new ConcurrentHashMap<>();

    /**
     * RetrofitHelper 初始化 currentFlag 是否需要再次初始化RetrofitHelper
     */
    public static RetrofitHelper getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitHelper.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 带公共参数参数（旧）
     */
    public <T> T getApi(Class<T> tClass) {
        T api = (T) map.get(tClass);
        if (api == null) {
            List<Interceptor> interceptors = new ArrayList<>();
            interceptors.add(new CommonInterceptor());
            interceptors.add(new LoggingInterceptor());
            api = getApi(tClass, interceptors);
        }
        return api;
    }

    private synchronized <T> T getApi(Class<T> tClass, List<Interceptor> interceptorList) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(15, TimeUnit.SECONDS);
        Retrofit.Builder builder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addCallAdapterFactory(RCallFactory.create())
            .addConverterFactory(GsonConverterFactory.create());
        List<Interceptor> interceptors = okBuilder.interceptors();
        interceptors.addAll(interceptorList);
        OkHttpClient okHttpClient = okBuilder.build();
        builder.client(okHttpClient);
        String apiStr;
        Field fieldd;
        try {
            fieldd = tClass.getDeclaredField("API");
            apiStr = (String) fieldd.get(null);
            T api = builder.baseUrl(apiStr).build().create(tClass);
            map.put(tClass, api);
            return api;
        } catch (NoSuchFieldException e) {
            LogUtils.e("no such static Field API");
        } catch (IllegalAccessException e) {
            LogUtils.e("Field API cannot access");
        }
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public <R, T> void execute(final Object tag, final Call<R> originalCall, final RetrofitCallBack<T> callback) {
        if (tag != null) {
            callMap.put(tag, originalCall);
        }
        new Observable<Response<R>>() {
            @Override
            protected void subscribeActual(final Observer<? super Response<R>> observer) {
                Call<R> call = originalCall.clone();
                CallDisposable disposable = new CallDisposable(call);
                observer.onSubscribe(disposable);
                try {
                    Response<R> response = call.execute();
                    if (!disposable.isDisposed()) {
                        if (response.code() >= 400 && response.code() <= 599) {
                            throw new RuntimeException(ERROR_MSG);
                        } else {
                            observer.onNext(response);
                        }
                    }
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    if (!disposable.isDisposed()) {
                        try {
                            observer.onError(t);
                        } catch (Throwable inner) {
                            Exceptions.throwIfFatal(inner);
                            RxJavaPlugins.onError(new CompositeException(t, inner));
                        }
                    }
                } finally {
                    observer.onComplete();
                }
            }
        }.toFlowable(BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Subscriber<Response<R>>() {
                //一般进行初始化操作后调用request()，立即触发onNext()方法
                // 有坑 404时会先执行onComplete
                @Override
                public void onSubscribe(Subscription s) {
                    //参数为请求的数量，背压相关，一般如果不限制请求数量，可以写成Long.MAX_VALUE
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Response<R> r) {
                    try {
                        R t = r.body();
                        if (t instanceof BaseResponse) {
                            BaseResponse br = (BaseResponse) t;
                            if (br.isOk()) {
                                // callback.chache
                                callback.onResponse(callback.parseNetworkResponse(t));
                            } else {
                                callback.onError(br);
                            }
                        } else {
                            callback.onResponse(callback.parseNetworkResponse(t));
                        }
                        if (tag != null) {
                            callMap.remove(tag);
                        }
                        callback.onAfter();
                    } catch (Exception e) {
                        LogUtils.w(e.toString());
                        onError(e);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (callback != null) {
                        BaseResponse response = new BaseResponse();
                        if (t instanceof SocketTimeoutException) {
                            response.setMsg(TIMEOUT_MSG);
                            callback.onError(response);
                        } else if (!NetworkUtil.isNetworkAvailable(false)) {
                            response.setMsg(BROKEN_MSG);
                            callback.onError(response);
                        } else {
                            response.setMsg(t.getMessage());
                            callback.onError(response);
                        }
                        if (tag != null) {
                            callMap.remove(tag);
                        }
                        callback.onAfter();
                    }
                }

                @Override
                public void onComplete() {
                }
            });
    }

    private static final class CallDisposable implements Disposable {

        private final Call<?> call;
        private volatile boolean disposed;

        CallDisposable(Call<?> call) {
            this.call = call;
        }

        @Override
        public void dispose() {
            disposed = true;
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }

    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        Call call = callMap.get(tag);
        if (call != null) {
            call.cancel();
        }
    }
}
