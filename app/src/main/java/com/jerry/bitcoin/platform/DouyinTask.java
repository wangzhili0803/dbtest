package com.jerry.bitcoin.platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jerry.baselib.Key;
import com.jerry.baselib.assibility.EndCallback;
import com.jerry.baselib.common.bean.DyUser;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.AppUtils;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.bitcoin.ListenerService;
import com.jerry.bitcoin.beans.PreferenceKey;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description
 */
public class DouyinTask {

    private static int tryCount;
    private static final int TRY_TOTAL = 10;
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
            if (scrollLayout != null) {
                List<AccessibilityNodeInfo> follow1s = root.findAccessibilityNodeInfosByText("关注");
                List<AccessibilityNodeInfo> follow2s = root.findAccessibilityNodeInfosByText("回关");
                for (AccessibilityNodeInfo follow1 : follow1s) {
                    if (check(follow1)) {
                        Rect rect = new Rect();
                        follow1.getBoundsInScreen(rect);
                        if ((ListenerService.mWidth >> 1) < rect.left) {
                            MyAccessibilityNodeInfo myAccessibilityNodeInfo = new MyAccessibilityNodeInfo();
                            myAccessibilityNodeInfo.setAccessibilityNodeInfo(follow1);
                            myAccessibilityNodeInfo.setX(ListenerService.mWidth >> 1);
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
                            myAccessibilityNodeInfo.setX(ListenerService.mWidth >> 1);
                            myAccessibilityNodeInfo.setY((rect.top + rect.bottom) >> 1);
                            myAccessibilityNodeInfos.add(myAccessibilityNodeInfo);
                        }
                    }
                }
                Collections.sort(myAccessibilityNodeInfos);
                getInfo4FollowList(service, myAccessibilityNodeInfos, 0, result -> swipAndContinue(service, myAccessibilityNodeInfos));
                return;
            }
            AccessibilityNodeInfo tabNode = service.findNodeWithText(2, "综合", "视频", "用户");
            if (tabNode != null) {
                AccessibilityNodeInfo recyclerNode = null;
                AccessibilityNodeInfo searchText = service.findFirstByText(root, "搜索");
                AccessibilityNodeInfo swipLayoutNode = searchText.getParent().getParent().getParent();
                AccessibilityNodeInfo viewPager = swipLayoutNode.getChild(2).getChild(0).getChild(0).getChild(2);
                for (int i = 0; i < viewPager.getChildCount(); i++) {
                    AccessibilityNodeInfo tempTecyclerNode = viewPager.getChild(i).getChild(1).getChild(1).getChild(1);
                    if (tempTecyclerNode.isVisibleToUser()) {
                        recyclerNode = tempTecyclerNode;
                        break;
                    }
                }
                if (recyclerNode != null) {
                    // 选中综合栏目
                    if (tabNode.getChild(0).isSelected()) {
                        List<AccessibilityNodeInfo> items = recyclerNode.findAccessibilityNodeInfosByViewId(PACKAGE_NAME + "tv_user_name");
                        for (int i = 0; i < items.size(); i++) {
                            AccessibilityNodeInfo item = items.get(i);
                            Rect rect = new Rect();
                            item.getBoundsInScreen(rect);
                            MyAccessibilityNodeInfo myAccessibilityNodeInfo = new MyAccessibilityNodeInfo();
                            myAccessibilityNodeInfo.setAccessibilityNodeInfo(recyclerNode.getChild(i));
                            myAccessibilityNodeInfo.setX((rect.left + rect.right) >> 1);
                            myAccessibilityNodeInfo.setY((rect.top + rect.bottom) >> 1);
                            myAccessibilityNodeInfos.add(myAccessibilityNodeInfo);
                        }
                        if (!CollectionUtils.isEmpty(myAccessibilityNodeInfos)) {
                            getInfo4SearchResult(service, myAccessibilityNodeInfos, 0, result -> swipAndContinue(service, myAccessibilityNodeInfos));
                            return;
                        }
                        if (recyclerNode.getChildCount() > 0) {
                            swipAndContinue(service, myAccessibilityNodeInfos);
                            return;
                        }
                    }
                    // 选中用户栏目
                    else if (tabNode.getChild(2).isSelected()) {
                        for (int i = 0; i < recyclerNode.getChildCount(); i++) {
                            AccessibilityNodeInfo item = recyclerNode.getChild(i);
                            Rect rect = new Rect();
                            item.getBoundsInScreen(rect);
                            MyAccessibilityNodeInfo myAccessibilityNodeInfo = new MyAccessibilityNodeInfo();
                            myAccessibilityNodeInfo.setAccessibilityNodeInfo(item);
                            myAccessibilityNodeInfo.setX(ListenerService.mWidth >> 1);
                            myAccessibilityNodeInfo.setY((rect.top + rect.bottom) >> 1);
                            myAccessibilityNodeInfos.add(myAccessibilityNodeInfo);
                        }
                        if (!CollectionUtils.isEmpty(myAccessibilityNodeInfos)) {
                            getInfo4SearchResult(service, myAccessibilityNodeInfos, 0, result -> swipAndContinue(service, myAccessibilityNodeInfos));
                            return;
                        }
                    }
                }
            }
            ToastUtil.showShortText("该页面不可采集");
            service.stopScript();
            return;
        }
        service.postDelayed(() -> doTask(service));
    }

    private void swipAndContinue(final ListenerService service, final List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos) {
        if (myAccessibilityNodeInfos.size() > 1) {
            Rect rectTop = new Rect();
            Rect rectBottom = new Rect();
            myAccessibilityNodeInfos.get(0).getAccessibilityNodeInfo().getBoundsInScreen(rectTop);
            myAccessibilityNodeInfos.get(myAccessibilityNodeInfos.size() - 1).getAccessibilityNodeInfo().getBoundsInScreen(rectBottom);
            if (service.exeSwip(ListenerService.mWidth >> 1,
                rectBottom.top,
                ListenerService.mWidth >> 1,
                rectTop.top - rectTop.height()
            )) {
                service.postDelayed(() -> doTask(service));
                return;
            }
        }
        service.exeSwip(ListenerService.mWidth >> 1,
            (int) (ListenerService.mHeight * 0.75f),
            ListenerService.mWidth >> 1,
            0
        );
        service.postDelayed(() -> doTask(service));
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
        if (service.exeClick(myAccessibilityNodeInfo.getX(), myAccessibilityNodeInfo.getY())) {
            service.postDelayed(() -> getHomePageInfo(service, myAccessibilityNodeInfos, result -> {
                if (!UserManager.getInstance().isLogined() || !PreferenceHelp.getBoolean("follow_try")) {
                    tryCount++;
                    if (tryCount >= TRY_TOTAL) {
                        PreferenceHelp.putBoolean("follow_try", true);
                        ToastUtil.showShortText("未登录或试用只搜集10条数据");
                        service.stopScript();
                        return;
                    }
                }
                getInfo4FollowList(service, myAccessibilityNodeInfos, index + 1, endCallback);
            }));
        }
    }

    private void getHomePageInfo(final ListenerService service, final List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos,
        final EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        AccessibilityNodeInfo accessibilityNodeInfo = service.findFirstByText(service.getRootInActiveWindow(), "抖音号：");
        if (accessibilityNodeInfo != null) {
            try {
                DyUser dyUser = new DyUser();
                dyUser.setDyId(accessibilityNodeInfo.getText().toString());
                AccessibilityNodeInfo infoNode = accessibilityNodeInfo.getParent().getParent().getParent();
                if (infoNode != null) {
                    dyUser.setName(infoNode.getChild(1).getText().toString());
                    AccessibilityNodeInfo persons = service.findFirstByText(infoNode, "获赞").getParent().getParent();
                    if (persons != null) {
                        dyUser.setPraise(persons.getChild(0).getChild(0).getText().toString());
                        dyUser.setFollow(persons.getChild(1).getChild(0).getText().toString());
                        dyUser.setFans(persons.getChild(2).getChild(0).getText().toString());
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < infoNode.getChildCount(); i++) {
                        AccessibilityNodeInfo decsNode = infoNode.getChild(i);
                        if (decsNode != null && decsNode.getClassName().toString().contains("TextView")) {
                            sb.append(decsNode.getText().toString());
                        }
                        sb.append(Key.SPACE);
                    }
                    dyUser.setDesc(sb.toString());
                    AccessibilityNodeInfo phoneNode = service.findFirstByText(infoNode, "联系电话");
                    if (phoneNode != null && "[label] 联系电话".equals(phoneNode.getText().toString()) && service.exeClick(phoneNode)) {
                        service.postDelayed(() -> {
                            AccessibilityNodeInfo popWindow = service.getRootInActiveWindow();
                            if (popWindow != null) {
                                List<AccessibilityNodeInfo> items = popWindow.findAccessibilityNodeInfosByText("呼叫");
                                sb.delete(0, sb.length());
                                for (AccessibilityNodeInfo item : items) {
                                    sb.append(item.getText().toString().replace("呼叫", Key.NIL).trim()).append(Key.COMMA);
                                }
                                if (sb.length() > 0) {
                                    sb.deleteCharAt(sb.length() - 1);
                                }
                                dyUser.setPhones(sb.toString());
                                dyUser.setUpdateTime(System.currentTimeMillis());
                                ProManager.getInstance().insertObject(dyUser);
                                service.back();
                                service.postDelayed(() -> {
                                    service.back();
                                    service.postDelayed(() -> endCallback.onEnd(true));
                                }, 300);
                                return;
                            }
                            service.back();
                            service.postDelayed(() -> getHomePageInfo(service, myAccessibilityNodeInfos, endCallback));
                        });
                        return;
                    }
                    if (PreferenceHelp.getBoolean(PreferenceKey.CB_HELLO)) {
                        AccessibilityNodeInfo personalMsgNode = service.findFirstByText(infoNode, "私信");
                        if (personalMsgNode != null && service.exeClick(personalMsgNode)) {
                            service.postDelayed(() -> sendMsg(service, PreferenceHelp.getString(PreferenceKey.SAY_HELLO, "你好"), result -> {
                                service.postDelayed(() -> {
                                    service.back();
                                    service.postDelayed(() -> {
                                        service.back();
                                        service.postDelayed(() -> endCallback.onEnd(true));
                                    }, 300);
                                });
                            }));
                            return;
                        }
                        AccessibilityNodeInfo followNode = service.findFirstByText(infoNode, "关注");
                        if (followNode != null && service.exeClick(followNode)) {
                            service.postDelayed(() -> {
                                AccessibilityNodeInfo follow2Node = service.findFirstByText(infoNode, "私信");
                                if (follow2Node != null && service.exeClick(follow2Node)) {
                                    service.postDelayed(() -> sendMsg(service, PreferenceHelp.getString(PreferenceKey.SAY_HELLO, "你好"), result -> {
                                        service.postDelayed(() -> {
                                            service.back();
                                            service.postDelayed(() -> {
                                                service.back();
                                                service.postDelayed(() -> endCallback.onEnd(true));
                                            }, 300);
                                        });
                                    }));
                                }
                            });
                            return;
                        }
                    }

                }
                dyUser.setUpdateTime(System.currentTimeMillis());
                ProManager.getInstance().insertObject(dyUser);
                service.back();
                service.postDelayed(() -> endCallback.onEnd(true));
                return;
            } catch (Throwable e) {
                LogUtils.e(e.getLocalizedMessage());
            }
        }
        service.back();
        service.postDelayed(() -> endCallback.onEnd(false));
    }

    private void sendMsg(final ListenerService service, final String text, final EndCallback endCallback) {
        try {
            AccessibilityNodeInfo pNode = service.getRootInActiveWindow().getChild(0).getChild(0).getChild(1).getChild(0).getChild(0);
            AccessibilityNodeInfo editTextNode = pNode.getChild(pNode.getChildCount() - 1).getChild(1).getChild(0).getChild(0).getChild(2);
            if (service.input(editTextNode, text)) {
                AccessibilityNodeInfo p2Node = service.getRootInActiveWindow().getChild(0).getChild(0).getChild(1).getChild(0).getChild(0);
                AccessibilityNodeInfo snedMsgNode = p2Node.getChild(p2Node.getChildCount() - 1).getChild(1).getChild(0).getChild(0).getChild(4);
                if (snedMsgNode != null && service.exeClick(snedMsgNode)) {
                    endCallback.onEnd(true);
                }
            }
        } catch (Exception e) {
            endCallback.onEnd(false);
        }
    }

    private void getInfo4SearchResult(final ListenerService service, final List<MyAccessibilityNodeInfo> myAccessibilityNodeInfos, final int index,
        final EndCallback endCallback) {
        if (!AppUtils.playing) {
            return;
        }
        if (index >= myAccessibilityNodeInfos.size()) {
            endCallback.onEnd(true);
            return;
        }
        MyAccessibilityNodeInfo myAccessibilityNodeInfo = myAccessibilityNodeInfos.get(index);
        if (service.exeClick(myAccessibilityNodeInfo.getX(), myAccessibilityNodeInfo.getY())) {
            service.postDelayed(() -> getHomePageInfo(service, myAccessibilityNodeInfos, result -> {
                if (!UserManager.getInstance().isLogined() || !PreferenceHelp.getBoolean("search_try")) {
                    tryCount++;
                    if (tryCount >= TRY_TOTAL) {
                        PreferenceHelp.putBoolean("search_try", true);
                        ToastUtil.showShortText("未登录或试用只搜集10条数据");
                        service.stopScript();
                        return;
                    }
                }
                getInfo4SearchResult(service, myAccessibilityNodeInfos, index + 1, endCallback);
            }));
            return;
        }
        service.postDelayed(() -> getInfo4SearchResult(service, myAccessibilityNodeInfos, index + 1, endCallback));

    }

    private boolean check(final AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.isVisibleToUser()) {
            CharSequence text = nodeInfo.getText();
            return "关注".contentEquals(text) || "已关注".contentEquals(text) || "回关".contentEquals(text);
        }
        return false;
    }
}
