package com.jerry.dbtest.adapter;


import java.util.List;

import com.jerry.dbtest.entity.Province;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProvinceAdapter extends BaseAdapter {

	private List<Province> mListDatas;
	private Context mContext;

	public ProvinceAdapter(List<Province> mListDatas, Context mContext) {
		super();
		this.mListDatas = mListDatas;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mListDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mListDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(mContext);
		}
		((TextView) convertView)
				.setText(mListDatas.get(position).getProvince());
		return convertView;
	}

}
