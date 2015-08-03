package com.example.demo500px.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Kanghee
 */
public class BaseRecyclerGridAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    protected OnItemClickListener mItemClickListener;
    protected int mItemHeight = 0;
    protected int mNumColumns = 0;
    protected ViewGroup.LayoutParams mImageViewLayoutParams;

    @Override
    public T onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(T viewHolder, int viewType) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Sets the item height. Useful for when we know the column width so the height can be set
     * to match.
     */
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams =
                new RecyclerView.LayoutParams(mItemHeight, mItemHeight);
        notifyDataSetChanged();
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}

