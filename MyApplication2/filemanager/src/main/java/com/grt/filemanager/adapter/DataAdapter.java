package com.grt.filemanager.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grt.filemanager.R;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.cache.ThumbCache;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.model.ArchiveFileInfo;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.util.FeThumbUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.PreferenceUtils;
import com.grt.filemanager.util.StorageUtils;
import com.grt.filemanager.util.TimeUtils;
import com.grt.filemanager.view.CircleLayout;

/**
 * Created by LDC on 2018/3/19.
 */

public class DataAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    protected BaseItemListener mItemListener;
    private DataModel mData;
    private long mTmpSize;
    private int mDataId;
    protected StateHandler mStateHandler;
    public DataAdapter(Context context, BaseItemListener itemListener, DataModel mData){
        super();
        this.context = context;
        this.mItemListener = itemListener;
        this.mData = mData;
        this.mTmpSize = 0;
        this.mStateHandler = StateHandler.getInstance();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout itemBar;
        itemBar = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.list_item, parent, false);

        return new ListViewHolder(itemBar, mItemListener, mItemListener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mData != null && mData.childrenCount() != 0){
            FileInfo current = mData.getCurrentChildren(position);
            setFileName(holder, current);
            holder.getThumbView().setTag(getThumbKey(true, current));
            listItem((ListViewHolder) holder, current);
        }
    }

    protected void listItem(ListViewHolder holder, FileInfo current) {
        setupFileInfo1(holder.getFileInfo1(), current);
        setupFileInfo2(holder.getFileInfo2(), current);
        setupFileInfo3(holder.getFileInfo3(), current);
        setupFileInfo4(holder.getFileInfo4(), current);
        setupFileInfo5(holder.getFileInfo5(), current);
        setupListThumb(holder, current);
        setLabelIcon(holder, current);
    }

    @Override
    public int getItemCount() {
        if (mData != null){
            return mData.childrenCount();
        }
        return 0;
    }

    protected void setFileName(BaseViewHolder holder, FileInfo current) {
        holder.getFileName().setText(current.getName());
        holder.setFileInfo(current);
    }

    protected String getThumbKey(boolean isList, FileInfo current) {
        return String.valueOf(isList) + current.getPath();
    }

    protected void setupFileInfo1(TextView fileInfo1, FileInfo current) {
        mTmpSize = current.getSize();
        int allChildrenCount = current.getSpecialInfo(DataConstant.ALL_CHILDREN_COUNT)
                .getInt(DataConstant.ALL_CHILDREN_COUNT);
        if (current.isFolder()) {
            boolean b = PreferenceUtils.getPrefBoolean(SettingConstant.SHOW_DIR_INFO,
                    SettingConstant.SHOW_DIR_INFO_DEFAULT_VALUE);
            if (b) {
                fileInfo1.setVisibility(View.VISIBLE);
                if (current instanceof ArchiveFileInfo && (((ArchiveFileInfo) current).isHeader())) {
                    fileInfo1.setText(R.string.directory);
                } else {
                    if (allChildrenCount < 0) {
                        allChildrenCount = 0;
                    }
                    String childrenCount = context.getString(R.string.item).replace("&", String.valueOf(allChildrenCount));
                    fileInfo1.setText(childrenCount.concat(", "));
                }
            } else {
                fileInfo1.setVisibility(View.GONE);
            }
        } else {
            fileInfo1.setVisibility(View.GONE);
        }
    }

    protected void setupFileInfo2(TextView fileInfo2, FileInfo current) {
        if (current.isFolder()) {
            boolean b = PreferenceUtils.getPrefBoolean(SettingConstant.SHOW_DIR_INFO,
                    SettingConstant.SHOW_DIR_INFO_DEFAULT_VALUE);
            if (b) {
                setTmpSize(fileInfo2);
            } else {
                fileInfo2.setText(R.string.directory);
            }
        } else {
            setTmpSize(fileInfo2);
        }
    }

    private void setTmpSize(TextView tv) {
        if (mTmpSize < 0) {
            tv.setText("");
        } else {
            tv.setText(StorageUtils.getPrettyFileSize(mTmpSize));
        }
    }

    protected void setupFileInfo3(TextView fileInfo3, FileInfo current) {
        fileInfo3.setVisibility(View.GONE);
    }

    protected void setupFileInfo4(TextView fileInfo4, FileInfo current) {
        String text = current.getSpecialInfo(DataConstant.PERMISSION)
                .getString(DataConstant.PERMISSION);
        if (text == null) {
            fileInfo4.setVisibility(View.GONE);
        } else {
            fileInfo4.setVisibility(View.VISIBLE);
            fileInfo4.setText(text);
        }

    }

    protected void setupFileInfo5(TextView fileInfo5, FileInfo current) {
        fileInfo5.setText(context.getString(R.string.separator)
                .concat(TimeUtils.getDateString(current.getLastModified(), getDateFormat())));
    }

    protected String getDateFormat() {
        return TimeUtils.DATE_FORMAT;
    }

    protected void setupListThumb(ListViewHolder holder, FileInfo current) {
        ImageView thumb = holder.getThumbView();
        holder.getThumbCircleLayout().setVisibility(View.VISIBLE);

        if (current.isFolder()) {
            setFolderThumb(holder, current, thumb);
        } else {
            //获取文件缩略图
            setFileThumb(holder, true, current);
        }

        if (mStateHandler.getIsCopyState()) {
            if (FileUtils.getParentPath(holder.getPath()).equals(mStateHandler.getSelectedParentPath())
                    && mStateHandler.isSelected(holder.getPath())) {
                setListViewSelectState(holder);
            } else {
                setListViewIdleState(holder);
            }
        } else {
            if (mStateHandler.isSelected(holder.getPath())) {
                setListViewSelectState(holder);
            } else {
                setListViewIdleState(holder);
            }
        }

//        setListViewIdleState(holder);
    }

    protected void setFolderThumb(BaseViewHolder holder, FileInfo current, ImageView thumb) {
        thumb.setImageResource(R.drawable.ic_folder_main);
        FeThumbUtils.setFileThumbAlpha(current.getName(), thumb);
        holder.getThumbCircleLayout().setColor(context.getResources().getColor(R.color.transparent));
    }

    /**
     * 设置默认Icon
     * 如果有缓存采用缓存信息
     * 无缓存信息重新获取信息
     */
    protected void setFileThumb(BaseViewHolder holder, boolean isList, FileInfo current) {
        holder.getAppIcon().setVisibility(View.GONE);

        //从缓存中获取信息
        if (getThumbCache(holder, getThumbKey(isList, current), isList)) {
            return;
        }

        try {
            //获取默认
            Bitmap defBitmap = FeThumbUtils.getDefaultThumb(context, current.getMimeType());
            holder.getThumbView().setImageBitmap(defBitmap);
            FeThumbUtils.setFileThumbAlpha(current.getName(), holder.getThumbView());
            holder.getThumbCircleLayout().setColor(context.getResources().getColor(R.color.transparent));
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }

        //获取图库 视频 音频  apk Thumb
        if (hasThumb(mDataId, holder.getMimeType())) {
            createThumbThread(holder, current, isList);
        }
    }

    protected boolean getThumbCache(BaseViewHolder holder, String thumbKey, boolean isList) {
        Bitmap cacheBitmap = ThumbCache.getCache().getThumb(thumbKey, isList);
        return setThumbView(holder, cacheBitmap, isList);
    }
    protected boolean setThumbView(BaseViewHolder holder, Bitmap cacheBitmap, boolean isList) {
        if (null != cacheBitmap) {
            String name = "";
            if (null != holder.getFileInfo()) {
                name = holder.getFileInfo().getName();
            }

            if (isList) {
                holder.getThumbView().setImageBitmap(cacheBitmap);
                FeThumbUtils.setFileThumbAlpha(name, holder.getThumbView());
            } else {
                holder.getThumbView().setVisibility(View.GONE);
                holder.getBigThumbView().setVisibility(View.VISIBLE);
                holder.getBigThumbView().setImageBitmap(cacheBitmap);
                FeThumbUtils.setFileThumbAlpha(name, holder.getBigThumbView());
            }

            holder.getThumbCircleLayout().setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    protected boolean hasThumb(int dataId, String mimeType) {
        return mimeType.startsWith("image") ||
                mimeType.startsWith("video") ||
                mimeType.startsWith("audio") ||
                mimeType.equals("application/ogg") ||
                mimeType.equals("application/vnd.android.package-archive");
    }

    public void notifyDataSetChanged(int mDataId, DataModel dataModel){
        this.mDataId = mDataId;
        this.mData = dataModel;
        notifyDataSetChanged();
    }
    public void insertItem(int mDataId, DataModel dataModel, int position){
        this.mDataId = mDataId;
        this.mData = dataModel;
        notifyItemInserted(position);
    }

    protected void createThumbThread(BaseViewHolder holder, FileInfo current, boolean isList) {
        ThumbWorkManger.getInstance().getLocalThumb(holder, current, isList);
    }

    private void setListViewIdleState(ListViewHolder holder) {
        holder.getItemView().setBackgroundResource(R.color.transparent);
        holder.getLabelIcon().setVisibility(View.VISIBLE);
    }

    protected void setLabelIcon(ListViewHolder holder, FileInfo current) {
        String path = current.getPath();
        ImageView labelIcon = holder.getLabelIcon();
        labelIcon.setTag(path);
        labelIcon.setVisibility(View.GONE);

        ThumbWorkManger.getInstance().getLabelIcon(path, holder);
    }

    public String getPathByPosition(int position) {
        FileInfo file = mData.getCurrentChildren(position);
        return file != null ? file.getPath() : null;
    }

    private void setListViewSelectState(ListViewHolder holder) {
        holder.getThumbCircleLayout().setVisibility(View.VISIBLE);
        holder.getItemView().setBackgroundResource(R.color.item_select_bg_dark);
        holder.getLabelIcon().setVisibility(View.GONE);
        selectAnim(holder);
    }

    /**
     * 长按选中thumb动画
     */
    private void selectAnim(final BaseViewHolder holder) {
        StateHandler handler = StateHandler.getInstance();
        if (!handler.getIsAnimShow(holder.getLayoutPosition())) {

            final CircleLayout circleLayout = holder.getThumbCircleLayout();
            ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(circleLayout, "alpha", 0f);
            ObjectAnimator scaleXOutAnim = ObjectAnimator.ofFloat(circleLayout, "scaleX", 0f);
            ObjectAnimator scaleYOutAnim = ObjectAnimator.ofFloat(circleLayout, "scaleY", 0f);

            ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(circleLayout, "alpha", 1f);
            ObjectAnimator scaleXInAnim = ObjectAnimator.ofFloat(circleLayout, "scaleX", 1.1f);
            ObjectAnimator scaleYInAnim = ObjectAnimator.ofFloat(circleLayout, "scaleY", 1.1f);

            ObjectAnimator scaleXIn1Anim = ObjectAnimator.ofFloat(circleLayout, "scaleX", 1f);
            ObjectAnimator scaleYIn1Anim = ObjectAnimator.ofFloat(circleLayout, "scaleY", 1f);

            alphaOutAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    circleLayout.setColor(ApiPresenter.getContext().getResources().getColor(R.color.blue));
                    holder.getThumbView().setImageResource(R.drawable.ic_done_navbar);
                    FeThumbUtils.setFileThumbAlpha(null, holder.getThumbView());
                }
            });

            AnimatorSet animSet = new AnimatorSet();
            animSet.setDuration(120);
            animSet.play(alphaOutAnim).with(scaleXOutAnim).with(scaleYOutAnim);
            animSet.play(alphaInAnim).with(scaleXInAnim).with(scaleYInAnim).after(alphaOutAnim);
            animSet.play(scaleXIn1Anim).with(scaleYIn1Anim).after(alphaInAnim);
            animSet.start();

            handler.putAnimShow(holder.getLayoutPosition());
        } else {
            holder.getThumbCircleLayout().setColor(ApiPresenter.getContext().getResources().getColor(R.color.blue));
            holder.getThumbView().setImageResource(R.drawable.ic_done_navbar);
            FeThumbUtils.setFileThumbAlpha(null, holder.getThumbView());
        }
    }
}
