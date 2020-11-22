package com.jerry.baselib.common.base;

import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:RecyclerViewHolder封装
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    /**
     * 默认大小是10
     */
    private SparseArray<View> views;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
    }

    static RecyclerViewHolder createViewHolder(View itemView) {
        return new RecyclerViewHolder(itemView);
    }

    /**
     * 获取view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

}
