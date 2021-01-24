package com.jerry.bitcoin.home;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.jerry.baselib.common.base.BaseFragment;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.beans.CoinConstant;
import com.jerry.bitcoin.interfaces.TaskCallback;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseFragment {

    private static final int SPAN_COUNT = 4;

    private static final List<String> PLATFORMS = new ArrayList<>();
    private static final List<String> COINS = new ArrayList<>();
    private static final List<Integer> BUYS = new ArrayList<>();

    static {
        PLATFORMS.add("huobi");
        PLATFORMS.add("coincola");
        COINS.add(CoinConstant.USDT);
        COINS.add(CoinConstant.BTC);
        COINS.add(CoinConstant.ETH);
        COINS.add(CoinConstant.EOS);
        COINS.add(CoinConstant.XRP);
        COINS.add(CoinConstant.LTC);
        BUYS.add(TaskCallback.TYPE_SELL);
        BUYS.add(TaskCallback.TYPE_BUY);
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        GridLayoutManager platformLayoutManager = new GridLayoutManager(mActivity, SPAN_COUNT);
        platformLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                return 1;
            }
        });
        RecyclerView rvPlatform = view.findViewById(R.id.rv_platform);
        rvPlatform.setLayoutManager(platformLayoutManager);
        BaseRecyclerAdapter<String> platformsAdapter = new BaseRecyclerAdapter<String>(mActivity, PLATFORMS) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final String bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean);
                textView.setSelected(bean.equals(PreferenceHelp.getString(ListenerService.TYPE_PLATFORMS, "huobi")));
            }
        };
        platformsAdapter.setOnItemClickListener((itemView, position) -> {
            String coinType = PLATFORMS.get(position);
            PreferenceHelp.putString(ListenerService.TYPE_PLATFORMS, coinType);
            ListenerService.setTaskPlatform();
            platformsAdapter.notifyDataSetChanged();
        });
        rvPlatform.setAdapter(platformsAdapter);

        GridLayoutManager coinLayoutManager = new GridLayoutManager(mActivity, SPAN_COUNT);
        coinLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                return 1;
            }
        });
        RecyclerView rvCoins = view.findViewById(R.id.rv_coins);
        rvCoins.setLayoutManager(coinLayoutManager);
        BaseRecyclerAdapter<String> coinsAdapter = new BaseRecyclerAdapter<String>(mActivity, COINS) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final String bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean);
                textView.setSelected(bean.equals(PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.XRP)));
            }
        };
        coinsAdapter.setOnItemClickListener((itemView, position) -> {
            String coinType = COINS.get(position);
            PreferenceHelp.putString(ListenerService.TYPE_COINS, coinType);
            ListenerService.setCoinType();
            coinsAdapter.notifyDataSetChanged();
        });
        rvCoins.setAdapter(coinsAdapter);

        GridLayoutManager buyLayoutManager = new GridLayoutManager(mActivity, SPAN_COUNT);
        buyLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                return 1;
            }
        });
        RecyclerView rvBuys = view.findViewById(R.id.rv_buys);
        rvBuys.setLayoutManager(buyLayoutManager);
        BaseRecyclerAdapter<Integer> buysAdapter = new BaseRecyclerAdapter<Integer>(mActivity, BUYS) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final Integer bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean == TaskCallback.TYPE_SELL ? "出售" : "购买");
                textView.setSelected(bean == PreferenceHelp.getInt(ListenerService.TYPE_BUYS));
            }
        };
        buysAdapter.setOnItemClickListener((itemView, position) -> {
            int buyType = BUYS.get(position);
            PreferenceHelp.putInt(ListenerService.TYPE_BUYS, buyType);
            ListenerService.setBuyType();
            buysAdapter.notifyDataSetChanged();
        });
        rvBuys.setAdapter(buysAdapter);
    }

    @Override
    public void onClick(final View v) {

    }
}
