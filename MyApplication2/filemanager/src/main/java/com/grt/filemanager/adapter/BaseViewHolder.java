package com.grt.filemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.view.CircleLayout;

import java.io.Serializable;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements Serializable {

    protected RelativeLayout mItemView;
    protected TextView mFileName;

    protected ImageView mBigThumbView;
    protected ImageView mThumbView;
    protected CircleLayout mThumbCircleLayout;
    protected ImageView mAppIcon;

    protected FileInfo mFileInfo;

    public BaseViewHolder(RelativeLayout itemView, View.OnClickListener onClickListener,
                          View.OnLongClickListener onLongClickListener) {
        super(itemView);

        mItemView = itemView;
        mFileName = initFileName(itemView);
        mThumbView = initThumbView(itemView);
        mBigThumbView = initBigThumbView(itemView);
        mThumbCircleLayout = initThumbCircleLayout(itemView);
        mAppIcon = initAppIcon(itemView);

        mItemView.setOnClickListener(onClickListener);
        mItemView.setOnLongClickListener(onLongClickListener);

        mItemView.setTag(this);
    }


    public RelativeLayout getItemView() {
        return mItemView;
    }

    public TextView getFileName() {
        return mFileName;
    }

    public ImageView getThumbView() {
        return mThumbView;
    }

    public ImageView getBigThumbView(){
        return mBigThumbView;
    }

    public CircleLayout getThumbCircleLayout() {
        return mThumbCircleLayout;
    }

    public ImageView getAppIcon() {
        return mAppIcon;
    }

    public FileInfo getFileInfo() {
        return mFileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.mFileInfo = fileInfo;
    }

    public boolean isFolder() {
        return mFileInfo.isFolder();
    }

    public String getPath() {
        return mFileInfo.getPath();
    }

    public String getMimeType() {
        return mFileInfo.getMimeType();
    }

    protected abstract TextView initFileName(RelativeLayout itemView);

    protected abstract ImageView initThumbView(RelativeLayout itemView);

    protected abstract ImageView initBigThumbView(RelativeLayout itemView);

    protected abstract CircleLayout initThumbCircleLayout(RelativeLayout itemView);

    protected abstract ImageView initAppIcon(RelativeLayout itemView);

}
