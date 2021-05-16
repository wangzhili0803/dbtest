package com.jerry.bitcoin.platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.DyUser;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.bitcoin.ListenerService;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class DouyinTask {

    private static volatile DouyinTask mInstance;
    private static final String PACKAGE_NAME = "com.ss.android.ugc.aweme:id/";

    public static DouyinTask getInstance() {
        if (mInstance == null) {
            synchronized (DouyinTask.class) {
                if (mInstance == null) {
                    mInstance = new DouyinTask();
                }
            }
        }
        return mInstance;
    }

    public void doTask(ListenerService service) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root != null) {
            List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos = new ArrayList<>();
            AccessibilityNodeInfo scrollLayout = service.findFirstById(root, PACKAGE_NAME + "scroll_layout");
            List<AccessibilityNodeInfo> follow1s = scrollLayout.findAccessibilityNodeInfosByText("关注");
            List<AccessibilityNodeInfo> follow2s = scrollLayout.findAccessibilityNodeInfosByText("回关");
            for (AccessibilityNodeInfo follow1 : follow1s) {
                if (check(follow1)) {
                    Rect rect = new Rect();
                    follow1.getBoundsInScreen(rect);
                    if ((ListenerService.mWidth >> 1) < rect.left) {
                        MyAccessibilityNodeInfo myAccessibilityNodeInfo = new MyAccessibilityNodeInfo();
                        myAccessibilityNodeInfo.setAccessibilityNodeInfo(follow1);
                        myAccessibilityNodeInfo.setY((rect.top + rect.bottom) >> 1);
                        myAccessibilityNodeInfos.add(myAccessibilityNodeInfo);
                    }
                }
            }
            for (AccessibilityNodeInfo follow2 : follow2s) {
                if (check(follow2)) {
                    Rect rect = new Rect();
                    follow2.getBoundsInScreen(rect);
                    if ((ListenerService.mWidth >> 1) < rect.left) {
                        MyAccessibilityNodeInfo myAccessibilityNodeInfo = new MyAccessibilityNodeInfo();
                        myAccessibilityNodeInfo.setAccessibilityNodeInfo(follow2);
                        myAccessibilityNodeInfo.setY((rect.top + rect.bottom) >> 1);
                        myAccessibilityNodeInfos.add(myAccessibilityNodeInfo);
                    }
                }
            }
            Collections.sort(myAccessibilityNodeInfos);
            getInfo4FollowList(service, myAccessibilityNodeInfos, 0, result -> {
                Rect rectTop = new Rect();
                Rect rectBottom = new Rect();
                myAccessibilityNodeInfos.get(0).getAccessibilityNodeInfo().getBoundsInScreen(rectTop);
                myAccessibilityNodeInfos.get(myAccessibilityNodeInfos.size() - 1).getAccessibilityNodeInfo().getBoundsInScreen(rectBottom);
                service.exeSwip(ListenerService.mWidth >> 1,
                    rectBottom.top,
                    ListenerService.mWidth >> 1,
                    rectTop.top - rectTop.height()
                );
                service.postDelayed(() -> doTask(service));
            });
        }
    }

    private void getInfo4FollowList(final ListenerService service, List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos, int index,
        EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        if (index >= myAccessibilityNodeInfos.size()) {
            endCallback.onEnd(true);
            return;
        }
        MyAccessibilityNodeInfo myAccessibilityNodeInfo = myAccessibilityNodeInfos.get(index);
        if (service.exeClick(ListenerService.mWidth >> 1, myAccessibilityNodeInfo.getY())) {
            service.postDelayed(() -> getHomePageInfo(service, myAccessibilityNodeInfos, index,
                result -> getInfo4FollowList(service, myAccessibilityNodeInfos, index + 1, endCallback)));
        }
    }

    private void getHomePageInfo(final ListenerService service, final List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos, final int index,
        final EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        AccessibilityNodeInfo accessibilityNodeInfo = service.findFirstByText(service.getRootInActiveWindow(), "抖音号：");
        if (accessibilityNodeInfo != null) {
            try {
                String dyId = accessibilityNodeInfo.getText().toString();
                AccessibilityNodeInfo infoNode = accessibilityNodeInfo.getParent().getParent().getParent();
                String name = infoNode.getChild(1).getText().toString();
                AccessibilityNodeInfo persons = service.findFirstByText(infoNode, "获赞").getParent().getParent();
                String praise = persons.getChild(0).getChild(0).getText().toString();
                String follow = persons.getChild(1).getChild(0).getText().toString();
                String fans = persons.getChild(2).getChild(0).getText().toString();
                String desc = infoNode.getChild(6).getText().toString();
                DyUser dyUser = new DyUser();
                dyUser.setDyId(dyId);
                dyUser.setName(name);
                dyUser.setPraise(praise);
                dyUser.setFollow(follow);
                dyUser.setFans(fans);
                dyUser.setDesc(desc);
                dyUser.setUpdateTime(System.currentTimeMillis());
                if (ProManager.getInstance().insertObject(dyUser)) {
                    service.back();
                    service.postDelayed(() -> endCallback.onEnd(true));
                    return;
                }
            } catch (Throwable e) {
                LogUtils.e(e.getLocalizedMessage());
            }
        }
        service.postDelayed(() -> getHomePageInfo(service, myAccessibilityNodeInfos, index, endCallback));
    }

    private boolean check(final AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.isVisibleToUser()) {
            CharSequence text = nodeInfo.getText();
            return "关注".contentEquals(text) || "已关注".contentEquals(text) || "回关".contentEquals(text);
        }
        return false;
    }
}
