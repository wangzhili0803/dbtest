package com.jerry.dbtest.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiscAdapter extends BaseAdapter {

	private List<String> mListDatas;
	private Context mContext;

	public DiscAdapter(List<String> mListDatas, Context mContext) {
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
		((TextView) convertView).setText(mListDatas.get(position));
		return convertView;
	}

}
