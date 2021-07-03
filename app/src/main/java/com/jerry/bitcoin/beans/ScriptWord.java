package com.jerry.bitcoin.beans;

import org.greenrobot.greendao.annotation.Entity;

import com.jerry.baselib.common.bean.AVBaseObject;

/**
 * @author Jerry
 * @createDate 7/3/21
 * @copyright www.axiang.com
 * @description
 */
@Entity
public class ScriptWord extends AVBaseObject {

    private String id;
    private String roomId;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
