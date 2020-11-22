package com.jerry.baselib.common.imageloader;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;

/**
 * Created by wzl on 2017/11/14.
 *
 * @Description 图片加载入口类
 */
public class ImageLoader {

    public static ImageLoadOption.OptionBuilder with(Context context) {
        return new ImageLoadOption.OptionBuilder(context.getApplicationContext());
    }

    public static ImageLoadOption.OptionBuilder with(Activity activity) {
        return new ImageLoadOption.OptionBuilder(activity);
    }

    public static ImageLoadOption.OptionBuilder with(Fragment fragment) {
        return new ImageLoadOption.OptionBuilder(fragment);
    }

    public static void clearMemCache() {
        GlobalConfig.getLoader().clearMemCache();
    }

    public static void clearFileCache() {
        GlobalConfig.getLoader().clearFileCache();
    }

    public static void trimMemory(int level) {
        GlobalConfig.getLoader().trimMemory(level);
    }
}
