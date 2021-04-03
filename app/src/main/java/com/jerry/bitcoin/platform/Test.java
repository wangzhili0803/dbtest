package com.jerry.bitcoin.platform;

/**
 * @author Jerry
 * @createDate 4/2/21
 * @description
 */
public class Test {

    public static void main(String[] args) {
        double benjin = 5000;
        double cny2btc = 393899.11;
        double btc2xrp = 0.00000985;
        double xrp2usdt = 0.5881;
        double usdt2cny = 6.7;
        double btc = benjin / cny2btc * 0.993;
        double xrp = btc / btc2xrp * 0.999 - 0.25;
        double usdt = xrp * xrp2usdt * 0.9998;
        double cny = usdt * usdt2cny;
        System.out.println(cny);
    }
}
