package com.jerry.baselib.common.util;

import android.app.Activity;

import com.jerry.baselib.common.bean.PayInfo;

/**
 * @author Jerry
 * @createDate 2019-06-12
 * @description
 */
public class PayUtil {

    /**
     * 1.发起快捷支付调用(打开TrPay收银台页面，用户自己选择支付方式）
     *
     * tradename 商品名称
     * outtradeno 商户系统订单号(商户系统内唯一)
     * amount 商品价格（单位：分。如1.5元传150）
     * backparams 商户系统回调参数
     * notifyurl 商户系统回调地址
     * userid 商户系统用户ID(如：trpay@52yszd.com，商户系统内唯一)
     */
    public static void pay(Activity activity, PayInfo payInfo, OnDataChangedListener<Integer> onDataChangedListener) {
//        TrPay.getInstance(activity).callPay(payInfo.getTradename(), payInfo.getOuttradeno(), payInfo.getAmount(), payInfo.getBackparams(),
//            payInfo.getNotifyurl(), payInfo.getUserid(), new PayResultListener() {
//                /**
//                 * 支付完成回调
//                 *
//                 * @param context 上下文
//                 * @param outtradeno 商户系统订单号
//                 * @param resultCode 支付状态(RESULT_CODE_SUCC：支付成功、RESULT_CODE_FAIL：支付失败)
//                 * @param resultString 支付结果
//                 * @param payType 支付类型（1：支付宝 2：微信 3：银联）
//                 * @param amount 支付金额
//                 * @param tradename 商品名称
//                 */
//                @Override
//                public void onPayFinish(Context context, String outtradeno, int resultCode, String resultString, int payType, Long amount,
//                    String tradename) {
//                    if (resultCode == TrPayResult.RESULT_CODE_SUCC.getId()) {
//                        //支付成功逻辑处理
//                        onDataChangedListener.onDataChanged(0);
//                    } else if (resultCode == TrPayResult.RESULT_CODE_FAIL.getId()) {
//                        //支付失败逻辑处理
//                        onDataChangedListener.onDataChanged(1);
//                    }
//                }
//
//            });
    }

}
