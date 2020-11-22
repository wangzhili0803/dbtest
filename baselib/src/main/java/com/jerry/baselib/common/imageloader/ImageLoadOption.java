package com.jerry.baselib.common.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by wzl on 2017/11/14.
 *
 * @Description 加载图片配置
 */
public class ImageLoadOption {

    private Context mContext;
    private Activity mActivity;
    private Fragment mFragment;
    /**
     * 资源id
     */
    @DrawableRes
    private int resId;
    private ImageView target;
    private int scaleType;
    private boolean asBitmap;

    private Priority priority;
    private DiskCacheStrategy mDiskCacheStrategy;
    @DrawableRes
    private int placeHolderResId;
    @DrawableRes
    private int errorResId;
    private int fixWidth;
    private int fixHeight;

    private String url;
    private String assetsPath;
    private String filePath;
    private File file;
    private boolean skip;

    private OnBitmapListener bitmapListener;

    public ImageLoadOption(OptionBuilder optionBuilder) {
        this.mContext = optionBuilder.context;
        this.mActivity = optionBuilder.mActivity;
        this.mFragment = optionBuilder.mFragment;
        this.url = optionBuilder.url;
        this.assetsPath = optionBuilder.assetPath;
        this.resId = optionBuilder.resId;
        this.target = optionBuilder.target;
        this.file = optionBuilder.file;
        this.filePath = optionBuilder.filePath;

        this.priority = optionBuilder.priority;
        this.mDiskCacheStrategy = optionBuilder.mDiskCacheStrategy;
        this.placeHolderResId = optionBuilder.placeHolderResId;

        this.bitmapListener = optionBuilder.bitmapListener;
        this.errorResId = optionBuilder.errorResId;
        this.scaleType = optionBuilder.scaleType;

        this.fixWidth = optionBuilder.fixWidth;
        this.fixHeight = optionBuilder.fixHeight;
        this.asBitmap = optionBuilder.asBitmap;
    }

    public Context getContext() {
        return mContext;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public int getErrorResId() {
        return errorResId;
    }

    public int getPlaceHolderResId() {
        return placeHolderResId;
    }

    public int getResId() {
        return resId;
    }

    public int getScaleType() {
        return scaleType;
    }

    public ImageView getTarget() {
        return target;
    }

    public String getUrl() {
        return url;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    public Priority getPriority() {
        return priority;
    }

    public DiskCacheStrategy getDiskCacheStrategy() {
        return mDiskCacheStrategy;
    }

    public int getFixWidth() {
        return fixWidth;
    }

    public int getFixHeight() {
        return fixHeight;
    }

    public boolean isAsBitmap() {
        return asBitmap;
    }

    public boolean skipMemoryCache() {
        return skip;
    }

    public OnBitmapListener getBitmapListener() {
        return bitmapListener;
    }

    private void show() {
        GlobalConfig.getLoader().displayImage(this);
    }

    public interface OnBitmapListener {

        void onSuccess(Bitmap bitmap);

        void onFail();
    }

    public static class SimpleBitmapListener implements OnBitmapListener {

        @Override
        public void onSuccess(Bitmap bitmap) {
        }

        @Override
        public void onFail() {
        }
    }

    public static class OptionBuilder {

        private Context context;
        private Activity mActivity;
        private Fragment mFragment;
        @DrawableRes
        private int resId;
        @DrawableRes
        private int placeHolderResId;
        @DrawableRes
        private int errorResId;

        private int fixWidth;
        private int fixHeight;
        private int scaleType;

        private String url;
        private String assetPath;
        private File file;
        private String filePath;

        private ImageView target;

        private boolean asBitmap;

        private OnBitmapListener bitmapListener;

        /**
         * 请求优先级
         */
        private Priority priority = Priority.HIGH;
        /**
         * 缓存策略
         */
        private DiskCacheStrategy mDiskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
        /**
         * skip memory cache
         */
        private boolean skip;

        OptionBuilder(Context context) {
            this.context = context;
        }

        OptionBuilder(Activity activity) {
            mActivity = activity;
        }

        OptionBuilder(Fragment fragment) {
            mFragment = fragment;
        }

        /**
         * error图
         */
        public OptionBuilder error(int errorResId) {
            this.errorResId = errorResId;
            return this;
        }

        /**
         * 设置网络路径
         */
        public OptionBuilder url(String url) {
            this.url = url;
            return this;
        }

        public OptionBuilder file(File file) {
            this.file = file;
            return this;
        }

        public OptionBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public OptionBuilder assetPath(String assetPath) {
            this.assetPath = assetPath;
            return this;
        }

        public OptionBuilder res(int resId) {
            this.resId = resId;
            return this;
        }

        public OptionBuilder scaleType(int scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public OptionBuilder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public OptionBuilder diskCacheStrategy(DiskCacheStrategy diskCacheStrategy) {
            this.mDiskCacheStrategy = diskCacheStrategy;
            return this;
        }

        public OptionBuilder override(int fixHeight, int fixWidth) {
            this.fixHeight = fixHeight;
            this.fixWidth = fixWidth;
            return this;
        }

        public OptionBuilder placeHolder(int placeHolderResId) {
            this.placeHolderResId = placeHolderResId;
            return this;
        }

        public OptionBuilder listener(OnBitmapListener bitmapListener) {
            this.bitmapListener = bitmapListener;
            return this;
        }

        public void asBitmap(OnBitmapListener bitmapListener) {
            this.bitmapListener = bitmapListener;
            this.asBitmap = true;
            new ImageLoadOption(this).show();
        }

        public void into(ImageView targetView) {
            this.target = targetView;
            new ImageLoadOption(this).show();
        }

        public OptionBuilder skipMemoryCache(boolean skip) {
            this.skip = skip;
            return this;
        }
    }
}
