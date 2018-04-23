package com.daemonw.fileui.core;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.daemonw.filelib.model.Filer;
import com.daemonw.fileui.widget.adapter.MultiItemTypeAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;
import com.daemonw.fileui.widget.adapter.WrapperUtils;
import com.example.fileui.R;

import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FileAdapterWrapper extends RecyclerView.Adapter<ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private PopupWindow mLoading;
    private boolean isLoading = false;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();

    private FileAdapter mInnerAdapter;
    private Activity mContext;
    private CompositeDisposable mDisposable;


    public FileAdapterWrapper(Activity context, int layoutResId, String rootPath, int mountType) {
        mContext = context;
        mInnerAdapter = new FileAdapter(context, layoutResId, rootPath, mountType);
        mDisposable = new CompositeDisposable();
        initPopupLoading(context);
        addHeaderView(inflateHeader(context), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToParent();
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
        if (isLoading) {
            return;
        }
        mDisposable.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        isLoading = true;
                        showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        mInnerAdapter.updateToParent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        notifyDataSetChanged();
                        dismissLoading();
                        isLoading = false;
                    }
                }));
    }

    public void updateToChild(Filer file) {
        if (isLoading) {
            return;
        }
        mDisposable.add(Single.just(file)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Filer, Boolean>() {
                    @Override
                    public Boolean apply(Filer localFile) throws Exception {
                        mInnerAdapter.updateToChild(localFile);
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        notifyDataSetChanged();
                        dismissLoading();
                        isLoading = false;
                    }
                }));
    }


    public void updateCurrent() {
        if (isLoading) {
            return;
        }
        mDisposable.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        mInnerAdapter.updateCurrent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        notifyDataSetChanged();
                        dismissLoading();
                        isLoading = false;
                    }
                }));
    }

    public boolean isRoot() {
        return mInnerAdapter.isRoot();
    }

    private View inflateHeader(Context context) {
        View v = View.inflate(context, R.layout.file_list_header, null);
        ImageView icon = v.findViewById(R.id.file_icon);
        icon.setImageResource(R.drawable.ic_folder_main);
        TextView name = v.findViewById(R.id.file_name);
        name.setText("...");
        return v;
    }

    private void initPopupLoading(Activity context) {
        mLoading = new PopupWindow();
        mLoading.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mLoading.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置不能取消
        mLoading.setOutsideTouchable(false);
        mLoading.setFocusable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
        mLoading.setContentView(view);
    }

    private void showLoading(Activity context) {
        if (mLoading == null) {
            initPopupLoading(context);
        }
        mLoading.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    private void dismissLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
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
}
