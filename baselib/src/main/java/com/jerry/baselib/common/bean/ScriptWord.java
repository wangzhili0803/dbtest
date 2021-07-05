package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 7/3/21
 * @copyright www.axiang.com
 * @description
 */
@Entity
public class ScriptWord extends AVBaseObject {

    private String id;
    private String userId;
    private String roomId;
    private String desc;
    @Generated(hash = 953498666)
    public ScriptWord(String id, String userId, String roomId, String desc) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.desc = desc;
    }
    @Generated(hash = 1767485855)
    public ScriptWord() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRoomId() {
        return this.roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
