package com.jerry.dbtest.entity;

import java.util.List;

public interface CityDao {

	/**
	 * 获取城市
	 * 
	 * @return
	 */
	List<City> queryCity(int code);

	/**
	 * 获取县区
	 * 
	 * @return
	 */
	List<String> queryDistrict(int code);

	/**
	 * 获取所有的省
	 * 
	 * @return
	 */
	List<Province> queryProvince();
}
