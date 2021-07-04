package com.jerry.bitcoin.platform;

import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.bean.AVObjQuery;
import com.jerry.baselib.common.bean.ScriptWord;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.MathUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.PreferenceKey;

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

    private boolean isSearchPage(final ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        AccessibilityNodeInfo search = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
        return search != null && search.getText() != null
            && ("Search".contentEquals(search.getText().toString()) || "搜索".contentEquals(search.getText().toString()));
    }

    public void doTask(final ListenerService service) {
        String roomId = UserManager.getInstance().getUser().getLiveRoom();
        enterRoom(service, roomId, result -> {
            new AVObjQuery<>(ScriptWord.class).whereContains("roomId", roomId).findObjects(data1 -> {
                if (data1 == null || data1.getCode() == 1) {
                    ToastUtil.showShortText("剧本词查询失败");
                    return;
                }
                List<ScriptWord> scriptWords = data1.getData();
                if (CollectionUtils.isEmpty(scriptWords)) {
                    ToastUtil.showShortText("暂无剧本词");
                    return;
                }
                AppTask.withoutContext().assign((BackgroundTask<Boolean>) () -> {
                    if (!CollectionUtils.isEmpty(scriptWords) && ProManager.getInstance().deleteAll(ScriptWord.class)) {
                        return ProManager.getInstance().insertMultObject(scriptWords);
                    }
                    return false;
                }).whenDone((WhenTaskDone<Boolean>) result1 -> {
                    LogUtils.d("updateDB:" + result1);
                    sendMsg(service);
                }).execute();
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
                List<AccessibilityNodeInfo> recycleViews = service.findVisibleNodesByClassName(root, "RecyclerView");
                if (!CollectionUtils.isEmpty(recycleViews)) {
                    AccessibilityNodeInfo firstItem = null;
                    AccessibilityNodeInfo recycleView = recycleViews.get(0);
                    for (int i = 0; i < recycleView.getChildCount(); i++) {
                        firstItem = recycleView.getChild(i);
                        if (firstItem.getClassName().toString().contains("ViewGroup")) {
                            break;
                        }
                    }
                    if (firstItem != null && service.exeClick(firstItem)) {
                        taskIndex++;
                    }
                }
                break;
            case 4:
                if (!service.exeClickText("join")) {
                    service.exeClickText("加入");
                }
                taskIndex++;
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

    private void sendMsg(final ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        AccessibilityNodeInfo sendMsgNode = root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT);
        List<ScriptWord> scriptWords = ProManager.getInstance().queryAll(ScriptWord.class, null, null);
        if (!CollectionUtils.isEmpty(scriptWords)) {
            int index = MathUtil.random(0, scriptWords.size());
            if (service.input(sendMsgNode, scriptWords.get(index).getDesc())) {
                service.postDelayed(() -> {
                    Rect rect = new Rect();
                    root.findFocus(AccessibilityNodeInfoCompat.FOCUS_INPUT).getBoundsInScreen(rect);
                    if (service.exeClick((rect.right + ListenerService.mWidth) >> 1, (rect.top + rect.bottom) >> 1)) {
                        int min = PreferenceHelp.getInt(PreferenceKey.DELAY_MIN, 10);
                        int max = PreferenceHelp.getInt(PreferenceKey.DELAY_MAX, 20);
                        long delay = MathUtil.random(min, max) * 1000;
                        ToastUtil.showShortText((delay / 1000) + "秒后在发送一条消息");
                        service.postDelayed(() -> {
                            sendMsg(service);
                        }, delay);
                    }
                });
            }
        }
    }

}
