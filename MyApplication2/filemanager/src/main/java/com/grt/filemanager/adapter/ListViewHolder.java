package com.grt.filemanager.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grt.filemanager.R;
import com.grt.filemanager.view.CircleLayout;

public class ListViewHolder extends BaseViewHolder {

    private TextView mFileInfo1, mFileInfo2, mFileInfo3, mFileInfo4, mFileInfo5;
    private ImageView mLabelIcon;
    private View mDividingLine;

    /**
     * ListViewHolder 构造函数.
     *
     * @param v                   每一个条目的View
     * @param onClickListener     点击事件的监听器
     * @param onLongClickListener 长按事件的监听器
     */
    public ListViewHolder(RelativeLayout v, View.OnClickListener onClickListener,
                          View.OnLongClickListener onLongClickListener) {
        super(v, onClickListener, onLongClickListener);

        this.mFileInfo1 = (TextView) v.findViewById(R.id.fileInfo1);
        this.mFileInfo2 = (TextView) v.findViewById(R.id.fileInfo2);
        this.mFileInfo3 = (TextView) v.findViewById(R.id.fileInfo3);
        this.mFileInfo4 = (TextView) v.findViewById(R.id.fileInfo4);
        this.mFileInfo5 = (TextView) v.findViewById(R.id.lastModified);
        this.mLabelIcon = (ImageView) v.findViewById(R.id.label_icon);
        this.mDividingLine = v.findViewById(R.id.dividingLine);
    }

    @Override
    protected TextView initFileName(RelativeLayout itemView) {
        return (TextView) itemView.findViewById(R.id.fileName);
    }

    @Override
    protected ImageView initThumbView(RelativeLayout itemView) {
        return (ImageView) itemView.findViewById(R.id.item_thumb);
    }

    @Override
    protected ImageView initBigThumbView(RelativeLayout itemView) {
        return null;
    }

    @Override
    protected CircleLayout initThumbCircleLayout(RelativeLayout itemView) {
        return (CircleLayout) itemView.findViewById(R.id.thumb_circle_layout);
    }

    @Override
    protected ImageView initAppIcon(RelativeLayout itemView) {
        return (ImageView) itemView.findViewById(R.id.app_icon);
    }

    public TextView getFileInfo1() {
        return mFileInfo1;
    }

    public TextView getFileInfo2() {
        return mFileInfo2;
    }

    public TextView getFileInfo3() {
        return mFileInfo3;
    }

    public TextView getFileInfo4() {
        return mFileInfo4;
    }

    public TextView getFileInfo5() {
        return mFileInfo5;
    }

    public ImageView getLabelIcon() {
        return mLabelIcon;
    }

    public View getDividingLine() {
        return mDividingLine;
    }

}
