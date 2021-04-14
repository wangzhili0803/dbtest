package com.jerry.bitcoin.platform;

/**
 * @author Jerry
 * @createDate 4/2/21
 * @description
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(cny3());
    }

    private static double cny() {
        double benjin = 5000;
        double cny2btc = 393899.11;
        double btc2xrp = 0.00000985;
        double xrp2usdt = 0.5881;
        double usdt2cny = 6.7;
        double btc = benjin / cny2btc * 0.993;
        double xrp = btc / btc2xrp * 0.999 - 0.25;
        double usdt = xrp * xrp2usdt * 0.9998;
        return usdt * usdt2cny;
    }

    private static double cny2() {
        double cny = 10000;
        double cny2coin = 6.25;
        double coin2usdt = 0.929;
        double usdt2cny = 6.75;
        double coin = cny / cny2coin * 0.993 - 0.25;
        return coin * coin2usdt * 0.9998 * usdt2cny;
    }

    private static double cny3() {
        double cny = 10000;
        double cny2coin = 6.29;
        double usdt2cny = 6.75;
        double coin = cny / cny2coin * 0.993 - 0.25;
        return cny / (coin * 0.9998 * usdt2cny);
    }
}
