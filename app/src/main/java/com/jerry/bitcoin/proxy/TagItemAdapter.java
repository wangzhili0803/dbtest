package com.jerry.bitcoin.proxy;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.SelectBean;

/**
 * @author Jerry
 * @createDate 2019-05-25
 * @copyright www.aniu.tv
 * @description
 */
public class TagItemAdapter extends BaseRecyclerAdapter<SelectBean> {
    public TagItemAdapter(Context context, List<SelectBean> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return com.jerry.baselib.R.layout.item_select_text;
    }

    @Override
    public void convert(RecyclerViewHolder holder, int position, int viewType, SelectBean bean) {
        TextView textView = holder.getView(com.jerry.baselib.R.id.textView);
        textView.setText(bean.getTitle());
        textView.setSelected(bean.isSelected());
    }
}
