package com.jerry.dbtest.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jerry.dbtest.R;
import com.jerry.dbtest.interfaces.RequestServes;
import com.jerry.dbtest.utils.DiskLruCacheUtil;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BitmapActivity extends Activity {
    private final static String TAG = BitmapActivity.class.getSimpleName();
    private Button btn_bitmap;
    private ImageView iv_bitmap;
    private DiskLruCacheUtil util;
    private static final String DISK_CACHE_SUBDIR = "temp";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    private static final String url = "http://img5.duitang.com/uploads/item/201207/18/20120718215150_QyFQZ.thumb.700_0.jpeg";
    RequestServes requestServes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        iv_bitmap = (ImageView) findViewById(R.id.iv_bitmap);
        btn_bitmap = (Button) findViewById(R.id.btn_bitmap);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.36wu.com/")
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        Log.d(TAG, "stringConverter" + "type:" + type);
                        Log.d(TAG, "stringConverter" + "annotations:" + annotations);
                        Log.d(TAG, "stringConverter" + "retrofit:" + retrofit);
                        return super.stringConverter(type, annotations, retrofit);
                    }

                    @Override
                    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
                        Log.d(TAG, "requestBodyConverter" + "type:" + type);
                        Log.d(TAG, "requestBodyConverter" + "parameterAnnotations:" + parameterAnnotations);
                        Log.d(TAG, "requestBodyConverter" + "methodAnnotations:" + methodAnnotations);
                        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
                    }

                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        Log.d(TAG, "responseBodyConverter" + "type:" + type);
                        Log.d(TAG, "responseBodyConverter" + "annotations:" + annotations);
                        Log.d(TAG, "responseBodyConverter" + "retrofit:" + retrofit);
                        return super.responseBodyConverter(type, annotations, retrofit);
                    }
                })
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        requestServes = retrofit.create(RequestServes.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        util = DiskLruCacheUtil.getInstance();
        util.open(this, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        util.flush();
    }

    @Override
    protected void onStop() {
        super.onStop();
        util.close();
    }

    public void getBitmapClick(View view) {
        Bitmap bitmap = util.getBitmapFromCache(url);
        if (bitmap == null) {
            InputStream in = util.getFromDiskCache(url);
            if (in == null) {
                util.putToDiskCache(url, new DiskLruCacheUtil.Callback<Bitmap>() {
                    @Override
                    public void response(Bitmap entity) {
                        System.out.println("http load");
                        iv_bitmap.setImageBitmap(entity);
                    }
                });
            } else {
                System.out.println("disk cache");
                bitmap = BitmapFactory.decodeStream(in);
                iv_bitmap.setImageBitmap(bitmap);
                util.addBitmapToCache(url, bitmap);
            }
        } else {
            System.out.println("memory cache");
            iv_bitmap.setImageBitmap(bitmap);
        }

        Call<String> call = requestServes.getString("13006140713", "71d7599f6d8f4843aba0ca551f142e7a");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("===", "call:" + call);
                Log.e("===", "response:" + response.errorBody());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("===", "return:" + t.toString());
            }
        });
    }

}
