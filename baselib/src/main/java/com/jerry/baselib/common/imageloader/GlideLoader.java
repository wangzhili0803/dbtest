package com.jerry.baselib.common.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jerry.baselib.BaseApp;
import com.jerry.baselib.R;
import com.jerry.baselib.common.asyctask.AppTask;

/**
 * Created by wzl on 2017/11/7.
 *
 * @Description 图片加载具体实现
 */
public class GlideLoader implements ILoader {

    @Override
    public void displayImage(final ImageLoadOption option) {
        RequestManager manager;
        try {
            Context context = option.getContext();
            Activity activity = option.getActivity();
            Fragment fragment = option.getFragment();
            if (context != null) {
                manager = Glide.with(context);
            } else if (activity != null) {
                manager = Glide.with(activity);
            } else if (fragment != null) {
                manager = Glide.with(fragment);
            } else {
                onFail(option);
                return;
            }
        } catch (Exception e) {
            onFail(option);
            return;
        }

        if (option.isAsBitmap()) {
            RequestOptions requestOptions = new RequestOptions().placeholder(option.getPlaceHolderResId()).skipMemoryCache(option
                .skipMemoryCache())
                .diskCacheStrategy(option.getDiskCacheStrategy()).error(option.getErrorResId()).format(DecodeFormat.PREFER_RGB_565);
            manager.asBitmap().apply(requestOptions).load(getLoadModel(option)).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    if (option.getBitmapListener() != null) {
                        option.getBitmapListener().onFail();
                    }
                    if (option.getTarget() != null) {
                        option.getTarget().setImageResource(option.getErrorResId() == 0 ? R.drawable.error : option.getErrorResId());
                    }
                    return true;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean
                    isFirstResource) {
                    if (option.getBitmapListener() != null) {
                        option.getBitmapListener().onSuccess(resource);
                    }
                    return true;
                }
            }).preload();
        } else {
            RequestOptions requestOptions = new RequestOptions().priority(option.getPriority()).diskCacheStrategy(option
                .getDiskCacheStrategy())
                .fitCenter().placeholder(option.getPlaceHolderResId()).error(option.getErrorResId());

            if (URLUtil.isNetworkUrl(option.getUrl()) && !option.getUrl().endsWith("png")) {
                requestOptions = requestOptions.format(DecodeFormat.PREFER_RGB_565);
            }

            if (option.getFixHeight() > 0 && option.getFixWidth() > 0) {
                requestOptions = requestOptions.override(option.getFixWidth(), option.getFixHeight());
            }

            getRequestBuilder(option, manager).apply(requestOptions).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    onFail(option);
                    if (option.getTarget() != null) {
                        option.getTarget().setImageResource(option.getErrorResId() == 0 ? R.drawable.error : option.getErrorResId());
                    }
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource,
                    boolean isFirstResource) {
                    return false;
                }
            }).into(option.getTarget());
        }
    }

    private void onFail(ImageLoadOption option) {
        if (option.getBitmapListener() != null) {
            GlobalConfig.getHandler().post(() -> {
                option.getBitmapListener().onFail();
            });
        }
    }

    private Object getLoadModel(ImageLoadOption option) {
        if (URLUtil.isValidUrl(option.getUrl())) {
            return option.getUrl();
        } else if (option.getResId() > 0) {
            return option.getResId();
        } else if (!TextUtils.isEmpty(option.getAssetsPath())) {
            return option.getAssetsPath();
        } else if (!TextUtils.isEmpty(option.getFilePath())) {
            return option.getFilePath();
        } else if (option.getFile() != null) {
            return option.getFile();
        }

        return option.getErrorResId() == 0 ? null : option.getErrorResId();
    }

    private RequestBuilder<Drawable> getRequestBuilder(ImageLoadOption option, RequestManager manager) {
        RequestBuilder<Drawable> builder;
        if (URLUtil.isValidUrl(option.getUrl())) {
            builder = manager.load(option.getUrl());
        } else if (option.getResId() > 0) {
            builder = manager.load(option.getResId());
        } else if (!TextUtils.isEmpty(option.getAssetsPath())) {
            builder = manager.load(option.getAssetsPath());
        } else if (!TextUtils.isEmpty(option.getFilePath())) {
            builder = manager.load(option.getFilePath());
        } else if (option.getFile() != null) {
            builder = manager.load(option.getFile());
        } else {
            builder = manager.load(option.getErrorResId() == 0 ? R.drawable.error : option.getErrorResId());
        }
        return builder;
    }

    @Override
    public void clearMemCache() {
        //只能在主线程执行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                Glide.get(BaseApp.getInstance()).clearMemory();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearFileCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                AppTask.withoutContext().assign(() -> {
                    Glide.get(BaseApp.getInstance()).clearDiskCache();
                    return null;
                }).execute();
            } else {
                Glide.get(BaseApp.getInstance()).clearDiskCache();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trimMemory(int level) {
        //只能在主线程执行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                Glide.get(BaseApp.getInstance()).trimMemory(level);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
