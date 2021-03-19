package com.jerry.bitcoin.interfaces;

import com.jerry.baselib.common.retrofit.retrofit.response.Response4List;
import com.jerry.bitcoin.beans.AnalyzeBean;
import com.jerry.bitcoin.beans.KBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Jerry
 * @createDate 3/2/21
 * @copyright www.aniu.tv
 * @description
 */
public interface HuobiApi {

    String API = "https://api.huobi.pro/";

    /**
     * 获取所有交易对
     */
    @GET("v1/common/symbols")
    Observable<Response4List<AnalyzeBean>> symbols();

    /**
     * @param symbol 交易对 btcusdt, ethbtc等（如需获取杠杆ETP净值K线，净值symbol = 杠杆ETP交易对symbol + 后缀‘nav’，例如：btc3lusdtnav）
     * @param period 返回数据时间粒度，也就是每根蜡烛的时间区间:1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week, 1year
     * @param size 返回 K 线数据条数
     */
    @GET("/market/history/kline")
    Observable<Response4List<KBean>> kline(@Query("symbol") String symbol, @Query("period") String period, @Query("size") int size);


}
