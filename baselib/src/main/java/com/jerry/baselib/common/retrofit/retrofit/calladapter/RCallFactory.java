package com.jerry.baselib.common.retrofit.retrofit.calladapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by wzl on 2018/11/5.
 *
 * @Description 项目中的自定义call工厂类
 */
public class RCallFactory extends CallAdapter.Factory {

    public static RCallFactory create() {
        return new RCallFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        //获取原始类型
        Class<?> rawType = getRawType(returnType);
        //返回值必须是Custom并且带有泛型（参数类型），根据APIService接口中的方法返回值，确定returnType
        //如 CustomCall<String> getCategories();，那确定returnType就是CustomCall<String>
        if (rawType == RCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new CallAdapter<Object, RCall<Object>>() {
                @Override
                public Type responseType() {
                    return callReturnType;
                }

                @Override
                public RCall<Object> adapt(final Call<Object> call) {
                    return new RCall<>(call);
                }
            };
        }
        return null;
    }
}