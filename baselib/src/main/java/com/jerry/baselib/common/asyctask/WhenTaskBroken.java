package com.jerry.baselib.common.asyctask;

/**
 * Created by wzl on 2017/11/21.
 *
 * @Description 当异步任务因为异常结束运行接口
 */
public interface WhenTaskBroken {

    /**
     * 异常结束处理方法
     *
     * @param t 异常内容
     */
    void whenBroken(Throwable t);
}
