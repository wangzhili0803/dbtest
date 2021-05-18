package com.jerry.baselib.common.util;

import java.util.Set;

import android.os.Bundle;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jerry.baselib.ActionCode;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.retrofit.RetrofitHelper;
import com.jerry.baselib.parsehelper.HttpApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserManager {

    private static UserManager instance;
    private static AxUser user;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (null == instance) {
            synchronized (UserManager.class) {
                if (null == instance) {
                    instance = new UserManager();
                    String userInfoString = PreferenceHelp.getString(Key.USER_INFO);
                    user = JSONObject.parseObject(userInfoString, AxUser.class);
                    if (null == user) {
                        user = new AxUser();
                    }
                }
            }
        }
        return instance;
    }

    public AxUser getUser() {
        if (user == null) {
            String userInfoString = PreferenceHelp.getString(Key.USER_INFO);
            user = JSONObject.parseObject(userInfoString, AxUser.class);
            if (user == null) {
                user = new AxUser();
            }
        }
        return user;
    }

    public String getPhone() {
        return user.getPhone();
    }

    public double getAmount() {
        return user.getAmount();
    }

    public int getCount() {
        return user.getCount();
    }

    /**
     * 是否登录
     */
    public boolean isLogined() {
        return user != null && !TextUtils.isEmpty(user.getObjectId());
    }

    /**
     * 是否为代理商
     */
    public boolean isproxy() {
        return isLogined() && (user.getLevel() >= 10);
    }

    /**
     * 是否为代理商
     */
    public boolean isSuper() {
        return isLogined() && user.getLevel() == 100;
    }

    public void saveUser(AxUser user) {
        UserManager.user = user;
        String userInfoStr = JSON.toJSONString(user);
        PreferenceHelp.putString(Key.USER_INFO, userInfoStr);
        notifyUserUpdate();
    }

    public void notifyUserUpdate() {
        Bundle bundle = new Bundle();
        bundle.putInt(Key.ACTION, ActionCode.USER_UPDATED);
        EventBus.getDefault().post(bundle);
    }

    public void requestUser(OnDataCallback<AxUser> dataChangedListener) {
        if (!NetworkUtil.isNetworkAvailable(true)) {
            return;
        }
        if (UserManager.getInstance().isLogined()) {
            AVObjQuery<AxUser> bmobQuery = new AVObjQuery<>(AxUser.class);
            bmobQuery.getObject(UserManager.getInstance().getUser().getObjectId(), data -> {
                if (data == null || data.getCode() == 1) {
                    return;
                }
                AxUser ruser = data.getData();
                if (ruser != null) {
                    Set<String> devices = ruser.getDevices();
                    if ((!CollectionUtils.isEmpty(devices) && devices.contains(AppUtils.getDeviceId()))) {
                        UserManager.getInstance().saveUser(ruser);
                        if (dataChangedListener != null) {
                            dataChangedListener.onDataCallback(user);
                        }
                        return;
                    }
                }
                logout(false, dataChangedListener);
            });
        } else if (dataChangedListener != null) {
            dataChangedListener.onDataCallback(user);
        }
    }

    /**
     * host：主动登出 登出
     */
    public void logout(boolean host, OnDataCallback<AxUser> dataChangedListener) {
        if (UserManager.getInstance().isLogined()) {
            Set<String> devices = user.getDevices();
            if (devices != null) {
                for (String device : devices) {
                    if (device.equals(AppUtils.getDeviceId())) {
                        devices.remove(device);
                        break;
                    }
                }
                user.setDevices(devices);
            }
            user.update(data -> {
                user = new AxUser();
                PreferenceHelp.putString(Key.USER_INFO, Key.NIL);
                notifyUserUpdate();
                ToastUtil.showShortText(host ? R.string.user_logouted : R.string.user_out_relogin);
                if (dataChangedListener != null) {
                    dataChangedListener.onDataCallback(user);
                }
            });
            return;
        }
        if (dataChangedListener != null) {
            dataChangedListener.onDataCallback(user);
        }
    }

    /**
     * 是否为代理商
     */
    public boolean isActive() {
        return user.getLevel() > 0;
    }

    public void checkDate(OnDataCallback<Boolean> onDataCallback) {
        RetrofitHelper.getInstance().getApi(HttpApi.class).getTimestamp()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> {
                Long time = result.getJSONObject(Key.DATA).getLong("t");
                String dayout = UserManager.getInstance().getUser().getExpire();
                onDataCallback.onDataCallback(time < DateUtils.getLongByDateTime(dayout));
            });
    }
}
