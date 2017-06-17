package com.jerry.dbtest.entity;

public class Province {

	private int sCode;
	private String province;

	public int getsCode() {
		return sCode;
	}

	public void setsCode(int sCode) {
		this.sCode = sCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Province(int sCode, String province) {
		super();
		this.sCode = sCode;
		this.province = province;
	}

	public Province() {
		super();
	}

}
