package com.jerry.baselib.common.asyctask;

/**
 * Created by wzl on 2017/11/21.
 *
 * @Description 后台线程接口， 通过实现这个接口，添加异步任务。
 */
public interface BackgroundTask<T> {

    /**
     * 异步任务具体实现
     *
     * @return 返回执行结果
     */
    T onBackground();
}
