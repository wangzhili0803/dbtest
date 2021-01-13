package com.jerry.bitcoin.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.UserManager;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

/**
 * Created by wli on 15/12/4. 实现自定义用户体系
 */
public class CustomUserProvider implements LCChatProfileProvider {

    private static CustomUserProvider customUserProvider;

    public synchronized static CustomUserProvider getInstance() {
        if (null == customUserProvider) {
            customUserProvider = new CustomUserProvider();
        }
        return customUserProvider;
    }

    private CustomUserProvider() {
    }

    private static final List<LCChatKitUser> CHAT_KIT_USERS = new ArrayList<LCChatKitUser>();

    @Override
    public void fetchProfiles(List<String> list, LCChatProfilesCallBack callBack) {
        List<LCChatKitUser> userList = new ArrayList<>();
        for (String userId : list) {
            for (LCChatKitUser user : CHAT_KIT_USERS) {
                if (user.getUserId().equals(userId)) {
                    userList.add(user);
                    break;
                }
            }
        }
        callBack.done(userList, null);
    }

    @Override
    public void fetchUsers() {
        CHAT_KIT_USERS.clear();
        String currentDev = AppUtils.getDeviceId();
        Set<String> devices = UserManager.getInstance().getUser().getDevices();
        for (String device : devices) {
            if (!device.equals(currentDev)) {
                CHAT_KIT_USERS.add(new LCChatKitUser(device, device, Key.NIL));
            }
        }
    }

    @Override
    public List<LCChatKitUser> getAllUsers() {
        return CHAT_KIT_USERS;
    }
}
