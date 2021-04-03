package com.jerry.baselib.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2019-06-15
 * @description
 */
@Entity
public class CoinOrder implements Parcelable {

    /**
     * 订单号
     */
    private String orderId;
    /**
     * 币种
     */
    private String coinType;
    /**
     * 用户名
     */
    private String name;
    /**
     * 交易金额
     */
    private double amount;
    /**
     * 成交额
     */
    private double quantity;
    /**
     * 成交价
     */
    private double price;
    /**
     * 手续费
     */
    private double fee;
    /**
     * 1：待下单，2：待付款，3：付款完成，4：待放币，5：去评价，0：完成
     */
    private int status;
    /**
     * 转账信息
     */
    private String transInfo;

    public CoinOrder() {

    }

    protected CoinOrder(Parcel in) {
        orderId = in.readString();
        coinType = in.readString();
        name = in.readString();
        amount = in.readDouble();
        quantity = in.readDouble();
        price = in.readDouble();
        fee = in.readDouble();
        status = in.readInt();
        transInfo = in.readString();
    }

    @Generated(hash = 1706391502)
    public CoinOrder(String orderId, String coinType, String name, double amount,
            double quantity, double price, double fee, int status,
            String transInfo) {
        this.orderId = orderId;
        this.coinType = coinType;
        this.name = name;
        this.amount = amount;
        this.quantity = quantity;
        this.price = price;
        this.fee = fee;
        this.status = status;
        this.transInfo = transInfo;
    }

    public static final Creator<CoinOrder> CREATOR = new Creator<CoinOrder>() {
        @Override
        public CoinOrder createFromParcel(Parcel in) {
            return new CoinOrder(in);
        }

        @Override
        public CoinOrder[] newArray(int size) {
            return new CoinOrder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(orderId);
        dest.writeString(coinType);
        dest.writeString(name);
        dest.writeDouble(amount);
        dest.writeDouble(quantity);
        dest.writeDouble(price);
        dest.writeDouble(fee);
        dest.writeInt(status);
        dest.writeString(transInfo);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(final String coinType) {
        this.coinType = coinType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(final double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(final double fee) {
        this.fee = fee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getTransInfo() {
        return transInfo;
    }

    public void setTransInfo(final String transInfo) {
        this.transInfo = transInfo;
    }

    @Override
    public String toString() {
        return "CoinOrder{" +
            "orderId='" + orderId + '\'' +
            ", coinType='" + coinType + '\'' +
            ", name='" + name + '\'' +
            ", amount=" + amount +
            ", quantity=" + quantity +
            ", price=" + price +
            ", fee=" + fee +
            ", status=" + status +
            ", transInfo='" + transInfo + '\'' +
            '}';
    }
}
