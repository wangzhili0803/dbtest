package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2020-02-28
 * @description
 */

public class AxiangConversationListItem {

    /**
     * 内容
     */
    private String text;
    /**
     * 来自设备号
     */
    private String root;
    /**
     * 设备号下的某个用户昵称
     */
    private String from;
    /**
     * 产品名称
     */
    private String title;
    /**
     * 设备号
     */
    private String deviceId;
    /**
     * im中的conversationId
     */
    private String conversationId;
    /**
     * 发送时间
     */
    private long updateAt;
    /**
     * 选择
     */
    private boolean selected;

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(final String root) {
        this.root = root;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(final long updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
}
