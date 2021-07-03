package com.jerry.bitcoin.proxy;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.AxUser;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.bitcoin.R;

import androidx.core.content.ContextCompat;

/**
 * @author Jerry
 * @createDate 2019-05-25
 * @copyright www.aniu.tv
 * @description
 */
class UserItemAdapter extends BaseRecyclerAdapter<AxUser> {

    public UserItemAdapter(Context context, List<AxUser> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_add_user;
    }

    @Override
    public void convert(RecyclerViewHolder holder, int position, int viewType, AxUser bean) {
        TextView title = holder.getView(R.id.tv_title);
        TextView expire = holder.getView(R.id.tv_expire);
        TextView content = holder.getView(R.id.tv_content);
        String dateout = bean.getExpire();
        if (dateout != null && dateout.length() == DateUtils.YYYYMMDDHHMMSS.length()) {
            long time = DateUtils.getLongByDateTime(dateout);
            expire.setTextColor(ContextCompat.getColor(mContext, bean.getLevel() > 0 && time > System.currentTimeMillis() ?
                R.color.green_primary : R.color.red_primary));
        }
        title.setText(bean.getPhone());
        expire.setText(bean.getExpire());
        content.setText(bean.getPasswd());
    }
}
