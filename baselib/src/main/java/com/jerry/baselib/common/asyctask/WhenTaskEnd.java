package com.jerry.baselib.common.asyctask;

/**
 * Created by wzl on 2017/11/21.
 *
 * @Description 当异步任务因为异常结束运行接口
 */
public interface WhenTaskEnd {

    /**
     * 异常结束处理方法
     */
    void whenEnd();
}
