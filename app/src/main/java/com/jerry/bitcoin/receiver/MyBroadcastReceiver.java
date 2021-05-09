package com.jerry.bitcoin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.greenrobot.eventbus.EventBus;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.baselib.common.util.LogUtils;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            if (smsObj != null) {
                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    String address = msg.getOriginatingAddress();
                    String content = msg.getDisplayMessageBody();
                    long timestampMillis = msg.getTimestampMillis();
                    LogUtils.i("address:" + address
                        + "  body:" + content
                        + "  time:" + DateUtils.getDateWTimesByLong(timestampMillis));
                    Bundle msgBundle = new Bundle();
                    msgBundle.putString(Key.CODE, address);
                    msgBundle.putString(Key.CONTENT, content);
                    msgBundle.putLong(Key.TIME, timestampMillis);
                    EventBus.getDefault().post(msgBundle);
                }
            }
        }
    }
}