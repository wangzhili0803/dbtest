package com.jerry.bitcoin.beans;

/**
 * @author Jerry
 * @createDate 3/30/21
 * @copyright www.aniu.tv
 * @description
 */
public class TransformInfo {

    String name;
    String number;
    String bank;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(final String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "TransformInfo{" +
            "name='" + name + '\'' +
            ", number='" + number + '\'' +
            ", bank='" + bank + '\'' +
            '}';
    }
}
