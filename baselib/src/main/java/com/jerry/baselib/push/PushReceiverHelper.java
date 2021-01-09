package com.jerry.baselib.push;

import java.io.File;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alibaba.fastjson.JSONObject;
import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.LogUtils;

/**
 * Created by wzl on 2016/6/16.
 *
 * @Description 推送逻辑处理模块，目的解耦
 */

public final class PushReceiverHelper {

    private PushReceiverHelper() {
    }

    public static PushReceiverHelper getInstance() {
        return InnerPushReceiverHelper.INSTANCE;
    }

    /**
     * 获取通知权限
     */
    public boolean isNotificationEnabled() {
        try {
            NotificationManagerCompat compat = NotificationManagerCompat.from(BaseApp.getInstance());
            return compat.areNotificationsEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 接收到自定义消息后对其进行处理，主要是生成自定义的推送样式
     */
    void processPassThroughMessage(Context context, String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        LogUtils.d(json);
        JSONObject jsonObject = JJSON.parseObject(json);
        String title = jsonObject.getString(Key.TITLE);
        String content = jsonObject.getString(Key.CONTENT);
        String ticker = jsonObject.getString("ticker");
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();
        ComponentName cName = new ComponentName(context, BaseApp.Config.APPLICATION_ID + ".MainActivity");
        intent.setComponent(cName);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            LogUtils.e("notificationManager error");
            return;
        }
        String channel = AppUtils.getChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int notifyID = new Random().nextInt(1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVibrate(new long[]{3000, 1000, 500, 700, 500, 300})
            .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Basic_tone.ogg")))
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true);
        mBuilder.setTicker(TextUtils.isEmpty(ticker) ? "点掌财经发来一则通知" : ticker);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        notificationManager.notify(notifyID, notification);
    }

    private static class InnerPushReceiverHelper {

        private static final PushReceiverHelper INSTANCE = new PushReceiverHelper();
    }

}
