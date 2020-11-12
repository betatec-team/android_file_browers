package com.wangy.myapplication.adapater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.wangy.myapplication.adapater.viewhodel.CustomViewHodel;

import java.io.IOException;
import java.util.List;

public abstract class CustomAdapater<T> extends RecyclerView.Adapter<CustomViewHodel> {
    private int mLayoutIds;
    private List<T> mData;
    private Context mContext;
    protected OnItemClickListener mOnItemClickListener;

    public CustomAdapater(int layoutId, List<T> data, Context context) {
        mLayoutIds = layoutId;
        mData = data;
        mContext = context;


    }

    @Override
    public CustomViewHodel onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mLayoutIds, parent, false);
        CustomViewHodel customViewHodel = new CustomViewHodel(mContext, view);
        setListener(parent, customViewHodel, viewType);
        return customViewHodel;
    }

    public void setData(List<T> datas) {
        mData.clear();
        mData.addAll(datas);
//        mData = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
//        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(CustomViewHodel holder, final int position) {
//        holder.setIsRecyclable(false);  //todo recyclerview 造成的问题
//        LogUtils.e(" currentPostion "  + position);
        convert(holder, mData.get(position), position);


    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    protected void setListener(final ViewGroup parent, final CustomViewHodel viewHolder, int viewType) {
//        if (!isEnabled(viewType)) return;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    try {
                        int position = viewHolder.getAdapterPosition();
                        mOnItemClickListener.onItemClick(v, viewHolder, position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected abstract void convert(CustomViewHodel holder, T t, int position);
}
