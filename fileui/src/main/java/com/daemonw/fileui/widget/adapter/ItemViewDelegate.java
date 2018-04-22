package com.daemonw.fileui.widget.adapter;


/**
 * Created by zhy on 16/6/22.
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    boolean isMatchedType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}