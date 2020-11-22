package com.jerry.baselib.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author Jerry
 * @createDate 2019-06-15
 * @description
 */
@Entity
public class Order implements Parcelable {

    /**
     * 订单号
     */
    @Id
    private String orderId;
    /**
     * 用户名
     */
    private String name;
    /**
     * 电话
     */
    private String tel;
    /**
     * 地址
     */
    private String addr;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 交易号
     */
    private String aliPayId;
    /**
     * 交易时间
     */
    private String time;
    /**
     * 产品title
     */
    private String title;
    /**
     * 物流地址
     */
    private String logisticsid;
    /**
     * 物流公司
     */
    private String logisticscm;
    /**
     * 是否被选中
     */
    private boolean isSel;

    protected Order(Parcel in) {
        orderId = in.readString();
        name = in.readString();
        tel = in.readString();
        addr = in.readString();
        nickname = in.readString();
        aliPayId = in.readString();
        time = in.readString();
        title = in.readString();
        logisticsid = in.readString();
        logisticscm = in.readString();
    }

    @Generated(hash = 1201181930)
    public Order(String orderId, String name, String tel, String addr,
            String nickname, String aliPayId, String time, String title,
            String logisticsid, String logisticscm, boolean isSel) {
        this.orderId = orderId;
        this.name = name;
        this.tel = tel;
        this.addr = addr;
        this.nickname = nickname;
        this.aliPayId = aliPayId;
        this.time = time;
        this.title = title;
        this.logisticsid = logisticsid;
        this.logisticscm = logisticscm;
        this.isSel = isSel;
    }

    @Generated(hash = 1105174599)
    public Order() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(addr);
        dest.writeString(nickname);
        dest.writeString(aliPayId);
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(logisticsid);
        dest.writeString(logisticscm);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAliPayId() {
        return this.aliPayId;
    }

    public void setAliPayId(String aliPayId) {
        this.aliPayId = aliPayId;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogisticsid() {
        return this.logisticsid;
    }

    public void setLogisticsid(String logisticsid) {
        this.logisticsid = logisticsid;
    }

    public String getLogisticscm() {
        return this.logisticscm;
    }

    public void setLogisticscm(String logisticscm) {
        this.logisticscm = logisticscm;
    }

    public boolean getIsSel() {
        return this.isSel;
    }

    public void setIsSel(boolean isSel) {
        this.isSel = isSel;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
