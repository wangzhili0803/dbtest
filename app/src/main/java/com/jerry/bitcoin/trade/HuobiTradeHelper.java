package com.jerry.bitcoin.trade;

import java.math.BigDecimal;
import java.util.List;

import com.huobi.client.AccountClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.trade.CreateOrderRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.account.Account;
import com.huobi.model.trade.MatchResult;
import com.jerry.bitcoin.Constants;

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

    public void createOrder(String symbol, double price, double amount) {
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
        long orderId = tradeClient.createOrder(CreateOrderRequest.spotBuyLimit(accountId, symbol, new BigDecimal(price), new BigDecimal(amount)));

        // 查询成交明细
        List<MatchResult> matchResults = tradeClient.getMatchResult(orderId);
    }
}
