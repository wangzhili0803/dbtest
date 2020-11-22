package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2020-03-08
 * @description im端信息交互
 */
public class AxiangMeassage {

    /**
     * 删除会话
     */
    public static final int MSG_DELETE_CONVERSATION = 1;
    /**
     * 指令，0：普通，1：删除
     */
    private int action;
    /**
     * 闲鱼用户
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 产品名称
     */
    private String title;
    /**
     * 所发内容
     */
    private String content;
    /**
     * 产品价格
     */
    private String price;
    /**
     * 产品图像
     */
    private String picture;
    /**
     * 设备号
     */
    private String deviceId;

    public int getAction() {
        return action;
    }

    public void setAction(final int action) {
        this.action = action;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }
}
