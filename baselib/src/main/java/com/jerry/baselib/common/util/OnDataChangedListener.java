package com.jerry.baselib.common.util;

/**
 * Created by wzl on 2018/10/18.
 *
 * @Description 数据变化监听
 */
public interface OnDataChangedListener<T> {

    void onDataChanged(T data);
}
