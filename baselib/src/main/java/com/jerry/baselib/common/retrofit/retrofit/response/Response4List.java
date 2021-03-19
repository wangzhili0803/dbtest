package com.jerry.baselib.common.retrofit.retrofit.response;

import java.util.List;

/**
 * Created by wzl on 16/4/22. 类说明: 返回数据为list形式的json数据解析基类
 */
public class Response4List<T> extends BaseResponse {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
