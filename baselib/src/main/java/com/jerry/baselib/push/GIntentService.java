package com.jerry.baselib.push;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.jerry.baselib.common.util.LogUtils;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 */
public class GIntentService extends GTIntentService {

    public GIntentService() {
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

      // 处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        // 透传消息的处理方式，详看SDK demo
        byte[] payload = msg.getPayload();
        if (payload == null) {
            LogUtils.e("receiver payload = null");
            return;
        }
        String json = new String(payload);
        PushReceiverHelper.getInstance().processPassThroughMessage(context, json);
    }

      // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

      // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

      // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

      // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
    }

      // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {   
    }
}