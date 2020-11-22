package com.jerry.baselib.common.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.jerry.baselib.common.util.AppUtils;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:RecyclerView公共adapter
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    protected Context mContext;
    protected List<T> mData;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    public static final int TYPE_DATA = 0;
    public static final int TYPE_STICKY_HEAD = 1;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<>() : data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return RecyclerViewHolder.createViewHolder(mHeaderViews.get(viewType));

        } else if (mFooterViews.get(viewType) != null) {
            return RecyclerViewHolder.createViewHolder(mFooterViews.get(viewType));
        }
        final RecyclerViewHolder holder = new RecyclerViewHolder(LayoutInflater.from(mContext)
                .inflate(getItemLayoutId(viewType), viewGroup, false));
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                if (AppUtils.isFastDoubleClick()) {
                    return;
                }
                if (holder.getLayoutPosition() - getHeadersCount() < mData.size()) {
                    mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition() - getHeadersCount());
                } else {
                    notifyDataSetChanged();
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition() - getHeadersCount());
                return true;
            });
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return super.getItemViewType(position - getHeadersCount());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            return;
        }
        if (position - getHeadersCount() >= getItemCount()) {
            return;
        }
        convert(holder, position, getItemViewType(position), mData.get(position - getHeadersCount()));
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void convert(RecyclerViewHolder holder, int position, int viewType, T bean);

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    public int getRealItemCount() {
        return mData.size();
    }

    public void add(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void delete(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        delete(position);
    }

    public void swap(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public T getItemAtPosition(int position) {
        return (mData == null || position < 0 || position >= mData.size()) ? null : mData.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View itemView, int position);
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFooterView(View view) {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void removeFooterView() {
        mFooterViews.remove(mFooterViews.size() - 1 + BASE_ITEM_TYPE_FOOTER);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) layoutManager;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderViewPos(position) || isFooterViewPos(position)) ? manager.getSpanCount() : 1;
                }
            });
            manager.setSpanCount(manager.getSpanCount());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

}
