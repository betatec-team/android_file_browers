package com.wangy.myapplication.adapater;

import android.view.View;


import com.wangy.myapplication.adapater.viewhodel.CustomViewHodel;

import java.io.IOException;

public interface OnItemClickListener {
    void onItemClick(View view, CustomViewHodel holder, int position) throws IOException;
    boolean onItemLongClick(View view, CustomViewHodel holder, int position);
}
