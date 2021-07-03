package com.jerry.bitcoin.platform;

import java.util.List;

import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.RoomBean;
import com.jerry.bitcoin.beans.ScriptWord;

/**
 * @author Jerry
 * @createDate 7/2/21
 * @copyright www.axiang.com
 * @description
 */
public class TelegramTask {

    private static final String PACKAGE_NAME = "org.telegram.messenger:id/";

    private boolean isHomePage(final ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        List<AccessibilityNodeInfo> texts = root.findAccessibilityNodeInfosByText(Key.SPACE);
        AccessibilityNodeInfo flowBtnNode = texts.get(texts.size() - 1);
        return "Open navigation menu".contentEquals(flowBtnNode.getContentDescription());
    }

    public void doTask(final ListenerService service) {
        new AVObjQuery<>(RoomBean.class).whereContains("userIds", UserManager.getInstance().getPhone()).findObjects(data -> {
            if (data == null || data.getCode() == 1) {
                ToastUtil.showShortText("房间查询失败");
                return;
            }
            List<RoomBean> roomBeans = data.getData();
            if (CollectionUtils.isEmpty(roomBeans)) {
                ToastUtil.showShortText("暂无创建房间");
                return;
            }
            String roomId = roomBeans.get(0).getRoomId();
            new AVObjQuery<>(ScriptWord.class).whereContains("roomId", roomId).findObjects(data1 -> {
                if (data1 == null || data1.getCode() == 1) {
                    ToastUtil.showShortText("房间查询失败");
                    return;
                }
                List<ScriptWord> scriptWords = data1.getData();
                if (CollectionUtils.isEmpty(scriptWords)) {
                    ToastUtil.showShortText("暂无剧本词");
                    return;
                }
                enterRoom(service, roomId, result -> {

                });
            });
        });

    }

    private void enterRoom(final ListenerService service, final String roomId, final EndCallback endCallback) {
        if (isHomePage(service)) {

        }
    }
}
