package com.jerry.dbtest.entity;

import java.util.ArrayList;
import java.util.List;

import com.jerry.dbtest.manager.DBManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CityDaoImpl implements CityDao {

	private DBManager dbManager;
	private SQLiteDatabase db;

	public CityDaoImpl(Context context) {
		dbManager = new DBManager(context);

	}

	@Override
	public List<City> queryCity(int code) {
		db = dbManager.getDatabase();
		List<City> cityList = null;
		String[] columns = { "sCode", "sName" };

		String selection = "sBelongCode=?";
		String[] selectionArgs = { String.valueOf(code) };
		Cursor cursor = db.query("tCity", columns, selection, selectionArgs,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cityList = new ArrayList<City>();
			while (cursor.moveToNext()) {
				int sCode = cursor.getInt(0);
				String cName = cursor.getString(1);
				City city = new City();
				city.setsCode(sCode);
				if (cName.equals("阿坝藏族羌族自治州")) {
					cName = "阿坝州";
				}
				if (cName.equals("甘孜藏族自治州")) {
					cName = "甘孜州";
				}
				if (cName.equals("凉山彝族自治州")) {
					cName = "凉山州";
				}
				city.setCityName(cName);
				cityList.add(city);
			}
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		db.close();
		db = null;
		return cityList;
	}

	@Override
	public List<String> queryDistrict(int code) {
		db = dbManager.getDatabase();
		List<String> districtList = null;
		String[] columns = { "sName" };

		String selection = "sBelongCode=?";
		String[] selectionArgs = { String.valueOf(code) };
		Cursor cursor = db.query("tCity", columns, selection, selectionArgs,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			districtList = new ArrayList<String>();
			while (cursor.moveToNext()) {
				String district = cursor.getString(0);
				districtList.add(district);
			}
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		db.close();
		db = null;
		return districtList;
	}

	@Override
	public List<Province> queryProvince() {
		db = dbManager.getDatabase();
		List<Province> listProvinces = new ArrayList<Province>();
		if (db.isOpen()) {
			String[] columns = { "sCode", "sName" };

			String selection = "sBelongCode=?";
			String[] selectionArgs = { "0" };
			Cursor cursor = db.query("tCity", columns, selection,
					selectionArgs, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					int sCode = cursor.getInt(0);
					String provinceName = cursor.getString(1);
					Province province = new Province(sCode, provinceName);
					listProvinces.add(province);
				}
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		db.close();
		return listProvinces;
	}

}
