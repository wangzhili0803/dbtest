package com.jerry.bitcoin.home;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.base.BaseFragment;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.weidgt.MyTextWatcher;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.beans.CoinConstant;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseFragment {

    private static final int SPAN_COUNT = 4;

    private static final List<String> PLATFORM_BUY = new ArrayList<>();
    private static final List<String> PLATFORM_SALE = new ArrayList<>();
    private static final List<String> COINS = new ArrayList<>();

    static {
        PLATFORM_BUY.add(CoinConstant.HUOBI);
        PLATFORM_BUY.add(CoinConstant.BINANCE);
        PLATFORM_BUY.add(CoinConstant.OKEX);

        PLATFORM_SALE.add(CoinConstant.HUOBI);
        PLATFORM_SALE.add(CoinConstant.BINANCE);
        PLATFORM_SALE.add(CoinConstant.OKEX);

        COINS.add(CoinConstant.USDT);
        COINS.add(CoinConstant.BTC);
        COINS.add(CoinConstant.ETH);
        COINS.add(CoinConstant.EOS);
        COINS.add(CoinConstant.XRP);
        COINS.add(CoinConstant.LTC);
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.app_name);
        TextView etPasswd = view.findViewById(R.id.et_passwd);
        etPasswd.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                PreferenceHelp.putString(Key.PASSWORD, s.toString());
            }
        });
        etPasswd.setText(PreferenceHelp.getString(Key.PASSWORD));
        GridLayoutManager platformLayoutManager = new GridLayoutManager(mActivity, SPAN_COUNT);
        platformLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                return 1;
            }
        });
        RecyclerView rvPlatformBuy = view.findViewById(R.id.rv_platform_buy);
        rvPlatformBuy.setLayoutManager(platformLayoutManager);
        BaseRecyclerAdapter<String> platformsAdapter = new BaseRecyclerAdapter<String>(mActivity, PLATFORM_BUY) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final String bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean);
                textView.setSelected(bean.equals(PreferenceHelp.getString(ListenerService.TYPE_PLATFORM_BUY, CoinConstant.HUOBI)));
            }
        };
        platformsAdapter.setOnItemClickListener((itemView, position) -> {
            String coinType = PLATFORM_BUY.get(position);
            PreferenceHelp.putString(ListenerService.TYPE_PLATFORM_BUY, coinType);
            ListenerService.setTaskPlatformBuy();
            platformsAdapter.notifyDataSetChanged();
        });
        rvPlatformBuy.setAdapter(platformsAdapter);

        GridLayoutManager saleManager = new GridLayoutManager(mActivity, SPAN_COUNT);
        saleManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                return 1;
            }
        });
        RecyclerView rvPlatformSale = view.findViewById(R.id.rv_platform_sale);
        rvPlatformSale.setLayoutManager(saleManager);
        BaseRecyclerAdapter<String> platformSaleAdapter = new BaseRecyclerAdapter<String>(mActivity, PLATFORM_SALE) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_select_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final String bean) {
                TextView textView = holder.getView(R.id.textView);
                textView.setText(bean);
                textView.setSelected(bean.equals(PreferenceHelp.getString(ListenerService.TYPE_PLATFORM_SALE, CoinConstant.HUOBI)));
            }
        };
        platformSaleAdapter.setOnItemClickListener((itemView, position) -> {
            String coinType = PLATFORM_SALE.get(position);
            PreferenceHelp.putString(ListenerService.TYPE_PLATFORM_SALE, coinType);
            ListenerService.setTaskPlatformSale();
            platformSaleAdapter.notifyDataSetChanged();
        });
        rvPlatformSale.setAdapter(platformSaleAdapter);

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
                textView.setSelected(bean.equals(PreferenceHelp.getString(ListenerService.TYPE_COINS, CoinConstant.USDT)));
            }
        };
        coinsAdapter.setOnItemClickListener((itemView, position) -> {
            String coinType = COINS.get(position);
            PreferenceHelp.putString(ListenerService.TYPE_COINS, coinType);
            ListenerService.setCoinType();
            coinsAdapter.notifyDataSetChanged();
        });
        rvCoins.setAdapter(coinsAdapter);
    }

    @Override
    public void onClick(final View v) {

    }
}
