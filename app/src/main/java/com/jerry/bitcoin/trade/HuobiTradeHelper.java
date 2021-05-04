package com.jerry.bitcoin.trade;

import java.math.BigDecimal;
import java.util.List;

import android.util.ArrayMap;

import com.huobi.client.AccountClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.trade.CreateOrderRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.account.Account;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.OnDataCallback;
import com.jerry.bitcoin.Constants;
import com.jerry.bitcoin.beans.CoinConstant;

import androidx.annotation.NonNull;

/**
 * @author Jerry
 * @createDate 4/14/21
 * @description
 */
public class HuobiTradeHelper {

    private static volatile HuobiTradeHelper mInstance;

    @NonNull
    private final AccountClient accountClient;
    @NonNull
    private final TradeClient tradeClient;

    public static final ArrayMap<String, Integer> PRICE_EXACT_MAP = new ArrayMap<>();
    public static final ArrayMap<String, Integer> AMOUNT_EXACT_MAP = new ArrayMap<>();

    static {
        PRICE_EXACT_MAP.put(CoinConstant.XRP_USDT, 5);
        PRICE_EXACT_MAP.put(CoinConstant.BCH_USDT, 2);
    }

    static {
        AMOUNT_EXACT_MAP.put(CoinConstant.XRP_USDT, 2);
        AMOUNT_EXACT_MAP.put(CoinConstant.BCH_USDT, 4);
    }

    private HuobiTradeHelper() {
        HuobiOptions mHuobiOptions = new HuobiOptions();
        mHuobiOptions.setApiKey(Constants.API_KEY);
        mHuobiOptions.setSecretKey(Constants.SECRET_KEY);
        accountClient = AccountClient.create(mHuobiOptions);
        tradeClient = TradeClient.create(mHuobiOptions);
    }

    public static HuobiTradeHelper getInstance() {
        if (mInstance == null) {
            synchronized (HuobiTradeHelper.class) {
                if (mInstance == null) {
                    mInstance = new HuobiTradeHelper();
                }
            }
        }
        return mInstance;
    }

    public void buy(String symbol, double price, double amount, OnDataCallback<Long> dataCallback) {
        AppTask.withoutContext().assign((BackgroundTask<Long>) () -> {
            long accountId = 0;
            // /v1/account/accounts
            List<Account> accounts = accountClient.getAccounts();
            for (Account account : accounts) {
                if ("spot".equals(account.getType())) {
                    accountId = account.getId();
                    break;
                }
            }
            // 下单
            return tradeClient.createOrder(CreateOrderRequest.spotBuyLimit(accountId, symbol, new BigDecimal(price), new BigDecimal(amount)));
        }).whenDone((WhenTaskDone<Long>) dataCallback::onDataCallback).whenBroken(t -> dataCallback.onDataCallback(-1L)).execute();
    }


    public void sell(String symbol, double price, double amount, OnDataCallback<Long> dataCallback) {
        AppTask.withoutContext().assign((BackgroundTask<Long>) () -> {
            long accountId = 0;
            // /v1/account/accounts
            List<Account> accounts = accountClient.getAccounts();
            for (Account account : accounts) {
                if ("spot".equals(account.getType())) {
                    accountId = account.getId();
                    break;
                }
            }
            Integer priceExact = PRICE_EXACT_MAP.get(symbol);
            Integer amountExact = AMOUNT_EXACT_MAP.get(symbol);
            if (priceExact != null && amountExact != null) {
                double finalPrice = MathUtil.halfEven(price, priceExact);
                double finalAmount = MathUtil.halfEven(amount, amountExact);
                return tradeClient
                    .createOrder(
                        CreateOrderRequest.spotSellLimit(accountId, symbol, BigDecimal.valueOf(finalPrice), BigDecimal.valueOf(finalAmount)));
            }
            return null;
            // 下单
        }).whenDone((WhenTaskDone<Long>) dataCallback::onDataCallback).whenBroken(t -> dataCallback.onDataCallback(-1L)).execute();
    }
}
