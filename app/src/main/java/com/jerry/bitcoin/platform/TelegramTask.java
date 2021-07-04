package com.jerry.bitcoin.platform;

import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.util.AppUtils;
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
    protected static int ERRORCOUNT = 5;
    protected int taskIndex;
    protected int errorCount;

    private boolean isHomePage(final ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        List<AccessibilityNodeInfo> texts = root.findAccessibilityNodeInfosByText(Key.SPACE);
        AccessibilityNodeInfo flowBtnNode = texts.get(texts.size() - 1);
        return "Open navigation menu".contentEquals(flowBtnNode.getContentDescription());
    }
//service.getRootInActiveWindow().findFocus(1);
    private boolean isSearchPage(final ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        AccessibilityNodeInfo search = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
        return search != null && search.getText() != null
            && ("Search".contentEquals(search.getText().toString()) || "搜索".contentEquals(search.getText().toString()));
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
        if (errorCount > ERRORCOUNT || !AppUtils.playing) {
            errorCount = 0;
            taskIndex = 0;
            endCallback.onEnd(false);
            return;
        }
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        int tempTaskIndex = taskIndex;
        switch (taskIndex) {
            case 0:
                if (isSearchPage(service)) {
                    taskIndex++;
                    taskIndex++;
                } else if (isHomePage(service)) {
                    taskIndex++;
                } else {
                    service.back();
                }
                break;
            case 1:
                List<AccessibilityNodeInfo> texts = root.findAccessibilityNodeInfosByText(Key.SPACE);
                AccessibilityNodeInfo flowBtnNode = texts.get(texts.size() - 1);
                Rect rect = new Rect();
                flowBtnNode.getBoundsInScreen(rect);
                if (service.exeClick(ListenerService.mWidth - 10, (rect.top + rect.bottom) >> 1)) {
                    taskIndex++;
                }
                break;
            case 2:
                AccessibilityNodeInfo search = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
                if (service.input(search, roomId)) {
                    taskIndex++;
                }
                break;
            case 3:
                AccessibilityNodeInfo search1 = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
                search1.getParent().getParent().getParent().getParent().getParent().getParent().getChild(1) .getChild(0).getChild(0);
                taskIndex++;
                break;
            case 4:
                AccessibilityNodeInfo search2 = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
                List<AccessibilityNodeInfo> globalSearch = root.findAccessibilityNodeInfosByText("全局搜索");
                if (CollectionUtils.isEmpty(globalSearch) || !globalSearch.get(0).isVisibleToUser()) {
                    globalSearch = root.findAccessibilityNodeInfosByText("global search");
                }
                Rect rect1 = new Rect();
                search2.getParent().getBoundsInScreen(rect1);
                if (CollectionUtils.isEmpty(globalSearch) || !globalSearch.get(0).isVisibleToUser()) {
                    if (service.exeClick(ListenerService.mWidth >> 1, ((rect1.top + rect1.bottom) >> 1) + rect1.height())) {
                        taskIndex++;
                    }
                } else {
                    Rect rect2 = new Rect();
                    globalSearch.get(0).getBoundsInScreen(rect2);
                    if (service.exeClick(ListenerService.mWidth >> 1, ((rect2.top + rect2.bottom) >> 1) + rect1.height())) {
                        taskIndex++;
                    }
                }
                break;
            case 5:
                break;
            default:
                errorCount = 0;
                taskIndex = 0;
                endCallback.onEnd(true);
                return;
        }
        if (taskIndex == tempTaskIndex) {
            errorCount++;
        }
        service.postDelayed(() -> enterRoom(service, roomId, endCallback));
    }
}
