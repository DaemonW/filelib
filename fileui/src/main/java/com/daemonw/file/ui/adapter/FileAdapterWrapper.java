package com.daemonw.file.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daemonw.file.core.model.Filer;
import com.daemonw.file.ui.R;
import com.daemonw.widget.MultiItemTypeAdapter;
import com.daemonw.widget.ViewHolder;
import com.daemonw.widget.WrapperUtils;

import java.util.List;
import java.util.Set;

public class FileAdapterWrapper extends RecyclerView.Adapter<ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();

    private FileAdapter mInnerAdapter;
    private Activity mContext;
    private OnHeadClickListener mHeadClickListener;


    public FileAdapterWrapper(Activity context, int layoutResId, String rootPath, int mountType, boolean showFile) {
        mContext = context;
        mInnerAdapter = new FileAdapter(context, layoutResId, rootPath, mountType, showFile);
        addHeaderView(getHeaderView(context), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeadClickListener != null) {
                    mHeadClickListener.onHeaderClicked();
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));
            return holder;

        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (mInnerAdapter.isRoot()) {
            return mInnerAdapter.getItemViewType(position);
        } else {
            if (isHeaderViewPos(position)) {
                return mHeaderViews.keyAt(position);
            }
            return mInnerAdapter.getItemViewType(position - getHeadersCount());
        }
    }

    private int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (mInnerAdapter.isRoot()) {
            mInnerAdapter.onBindViewHolder(holder, position);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
        }
    }

    @Override
    public int getItemCount() {
        if (mInnerAdapter.isRoot()) {
            return getRealItemCount();
        }
        return getHeadersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    public Filer getItem(int position) {
        if (mInnerAdapter.isRoot()) {
            return mInnerAdapter.getItem(position);
        }

        if (isHeaderViewPos(position)) {
            return mInnerAdapter.getCurrent().getParentFile();
        }
        int realPos = position - getHeadersCount();
        return mInnerAdapter.getItem(realPos);
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount() && !mInnerAdapter.isRoot();
    }

    public void addHeaderView(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public void setOnItemClickListener(MultiItemTypeAdapter.OnItemClickListener listener) {
        mInnerAdapter.setOnItemClickListener(listener);
    }

    public void update(List<Filer> fileList) {
        mInnerAdapter.update(fileList);
        notifyDataSetChanged();
    }

    public void updateToParent() {
        mInnerAdapter.updateToParent();
    }

    public void updateToChild(Filer file) {
        mInnerAdapter.updateToChild(file);
    }


    public void updateCurrent() {
        mInnerAdapter.updateCurrent();
    }


    public boolean isRoot() {
        return mInnerAdapter.isRoot();
    }

    public void setMultiSelect(boolean enable) {
        if (!enable) {
            mInnerAdapter.clearSelect();
        }
        mInnerAdapter.setMultiSelect(enable);
    }

    public boolean isMultiSelect() {
        return mInnerAdapter.isMultiSelect();
    }

    public Set<Filer> getSelected() {
        return mInnerAdapter.getSelected();
    }

    public Filer getCurrent() {
        return mInnerAdapter.getCurrent();
    }

    private View getHeaderView(Context context) {
        View v = View.inflate(context, R.layout.file_list_header, null);
        ImageView icon = v.findViewById(R.id.file_icon);
        icon.setImageResource(R.drawable.ic_folder);
        TextView name = v.findViewById(R.id.file_name);
        name.setText("...");
        ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return v;
    }

    public void setOnHeadClickListener(OnHeadClickListener headClickListener) {
        this.mHeadClickListener = headClickListener;
    }

    public interface OnHeadClickListener {
        void onHeaderClicked();
    }
}
