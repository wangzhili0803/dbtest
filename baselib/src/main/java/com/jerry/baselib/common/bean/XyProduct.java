package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2019-04-28
 * @description
 */
@Entity
public class XyProduct {

    /**
     * 标题
     */
    private String title;
    /**
     * 价格
     */
    private String price;
    /**
     * 图片
     */
    private String picture;
    @Generated(hash = 143047743)
    public XyProduct(String title, String price, String picture) {
        this.title = title;
        this.price = price;
        this.picture = picture;
    }
    @Generated(hash = 1154579164)
    public XyProduct() {
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
