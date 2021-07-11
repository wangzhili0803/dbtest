package com.jerry.bitcoin.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.jerry.baselib.common.bean.AVBaseObject;

/**
 * @author Jerry
 * @createDate 7/3/21
 * @copyright www.axiang.com
 * @description
 */
public class RoomBean extends AVBaseObject implements Parcelable {

    private String roomId;
    private List<String> userIds;
    /**
     * 到期时间
     */
    private String expire;

    public RoomBean() {
    }

    protected RoomBean(Parcel in) {
        roomId = in.readString();
        userIds = in.createStringArrayList();
        expire = in.readString();
    }

    public static final Creator<RoomBean> CREATOR = new Creator<RoomBean>() {
        @Override
        public RoomBean createFromParcel(Parcel in) {
            return new RoomBean(in);
        }

        @Override
        public RoomBean[] newArray(int size) {
            return new RoomBean[size];
        }
    };

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

    public String getExpire() {
        return expire;
    }

    public void setExpire(final String expire) {
        this.expire = expire;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(roomId);
        dest.writeStringList(userIds);
        dest.writeString(expire);
    }
}
