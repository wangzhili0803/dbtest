package com.jerry.bitcoin.beans;

import java.util.ArrayList;
import java.util.List;

import android.util.ArrayMap;

/**
 * @author Jerry
 * @createDate 1/24/21
 * @description
 */
public class CoinConstant {

    public static final String HUOBI = "huobi";
    public static final String COINCOLA = "coincola";

    public static final String USDT = "USDT";
    public static final String BTC = "BTC";
    public static final String ETH = "ETH";
    public static final String EOS = "EOS";
    public static final String XRP = "XRP";
    public static final String BCH = "BCH";
    public static final String LTC = "LTC";

    public static final String XRP_USDT = "xrpusdt";
    public static final String BCH_USDT = "bchusdt";
    public static final String SUB_CANDLE = XRP_USDT + "," + BCH_USDT;
    public static final List<String> LISTEN_COINS = new ArrayList<>();
    public static final ArrayMap<String, Double> FEEMAP = new ArrayMap<>();
    public static final ArrayMap<String, Double> ACCOUNT_MAP = new ArrayMap<>();

    static {
        FEEMAP.put(XRP_USDT, 0.25);
        FEEMAP.put(BCH_USDT, 0.0005);
    }

    static {
        ACCOUNT_MAP.put(XRP, 70d);
        ACCOUNT_MAP.put(BCH, 0.2);
    }
}
