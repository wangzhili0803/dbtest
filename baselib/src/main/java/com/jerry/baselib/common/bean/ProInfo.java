package com.jerry.baselib.common.bean;

import java.util.List;

/**
 * @author Jerry
 * @createDate 2019-05-04
 * @description
 */
public class ProInfo extends AVBaseObject {
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户商品信息json
     */
    private List<Product> products;

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(final List<Product> products) {
        this.products = products;
    }
}
