package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 5/16/21
 * @copyright www.axiang.com
 * @description
 */
@Entity
public class DyUser {

    @Id
    private String dyId;
    private String name;
    private String praise;
    private String follow;
    private String fans;
    private String desc;
    private String phones;
    private long updateTime;

    @Generated(hash = 1604658254)
    public DyUser(String dyId, String name, String praise, String follow,
        String fans, String desc, String phones, long updateTime) {
        this.dyId = dyId;
        this.name = name;
        this.praise = praise;
        this.follow = follow;
        this.fans = fans;
        this.desc = desc;
        this.phones = phones;
        this.updateTime = updateTime;
    }

    @Generated(hash = 2053461469)
    public DyUser() {
    }

    public String getDyId() {
        return this.dyId;
    }

    public void setDyId(String dyId) {
        this.dyId = dyId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPraise() {
        return this.praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getFollow() {
        return this.follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getFans() {
        return this.fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhones() {
        return this.phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

}
