package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description
 */
@Entity
public class XyUser {

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    @Generated(hash = 1753126712)
    public XyUser(String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
    }
    @Generated(hash = 406033456)
    public XyUser() {
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
