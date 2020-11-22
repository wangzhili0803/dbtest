package com.jerry.baselib.common.asyctask;

/**
 * Created by wzl on 2017/11/21.
 *
 * @Description 异步任务正常结束运行，且当前上下文环境处于安全的生命周期中
 */
public interface WhenTaskDone<T> {

    /**
     * 异步任务正常结束运行， 方法将会被调用
     *
     * @param result 后台执行结果
     */
    void whenDone(T result);
}
