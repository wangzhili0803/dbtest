package com.jerry.bitcoin.bean;

import java.util.List;

import com.jerry.baselib.common.bean.AVBaseObject;

/**
 * @author Jerry
 * @createDate 7/3/21
 * @copyright www.axiang.com
 * @description
 */
public class RoomBean extends AVBaseObject {

    private String roomId;
    private List<String> userIds;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(final List<String> userIds) {
        this.userIds = userIds;
    }
}
