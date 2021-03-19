package com.jerry.bitcoin.analyze;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.BaseRecyclerFragment;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.retrofit.RetrofitHelper;
import com.jerry.baselib.common.retrofit.retrofit.response.Response4List;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.bitcoin.beans.AnalyzeBean;
import com.jerry.bitcoin.beans.KBean;
import com.jerry.bitcoin.interfaces.HuobiApi;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class AnalyzeFragment extends BaseRecyclerFragment<AnalyzeBean> {

    private List<AnalyzeBean> dayFilter = new ArrayList<>();
    private List<AnalyzeBean> hourFilter = new ArrayList<>();
    private List<AnalyzeBean> min5Filter = new ArrayList<>();

    @Override
    protected BaseRecyclerAdapter<AnalyzeBean> initAdapter() {
        return new BaseRecyclerAdapter<AnalyzeBean>(mActivity, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return 0;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final AnalyzeBean bean) {

            }
        };
    }

    @Override
    protected void getData() {
        RetrofitHelper.getInstance().getApi(HuobiApi.class).symbols()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(analyzeBeanResponse4List -> doNext(analyzeBeanResponse4List.getData()));
    }

    int log;
    private List<String> fsf = new ArrayList<>();

    private void doNext(final List<AnalyzeBean> analyzeBeanResponse4List) {
        fsf.clear();
        Observable.from(analyzeBeanResponse4List)
            .flatMap((Func1<AnalyzeBean, Observable<Response4List<KBean>>>) analyzeBean -> RetrofitHelper.getInstance().getApi(HuobiApi.class)
                .kline(analyzeBean.getBasecurrency() + analyzeBean.getQuotecurrency(), "1day", 14))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Response4List<KBean>>() {
                @Override
                public void onCompleted() {
                    LogUtils.d("ttttt:" + fsf.size());
                }

                @Override
                public void onError(final Throwable e) {

                }

                @Override
                public void onNext(final Response4List<KBean> kBeanResponse4List) {
                    boolean add = true;
                    List<KBean> kBeans = kBeanResponse4List.getData();
                    for (KBean kBean : kBeans) {
                        double fsdf = (kBean.getClose() - kBean.getOpen()) / kBean.getOpen();
                        if (Math.abs(fsdf) > 0.1) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        fsf.add(kBeanResponse4List.getCh());
                    }
                }
            });
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
