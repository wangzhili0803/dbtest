package com.jerry.baselib.common.imageloader;

/**
 * Created by wzl on 2017/11/14.
 *
 * @Description
 */
public interface ILoader {

    void displayImage(ImageLoadOption option);

    void clearMemCache();

    void clearFileCache();

    void trimMemory(int level);
}
