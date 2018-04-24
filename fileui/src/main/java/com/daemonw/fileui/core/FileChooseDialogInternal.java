package com.daemonw.fileui.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daemonw.filelib.BuildConfig;
import com.daemonw.filelib.exception.PermException;
import com.daemonw.filelib.model.Filer;
import com.daemonw.filelib.reflect.Volume;
import com.daemonw.filelib.utils.BuildUtils;
import com.daemonw.filelib.utils.MimeTypes;
import com.daemonw.filelib.utils.PermissionUtil;
import com.daemonw.filelib.utils.RxUtil;
import com.daemonw.filelib.utils.StorageUtil;
import com.daemonw.fileui.R;
import com.daemonw.fileui.core.FileAdapterWrapper;
import com.daemonw.fileui.core.OnFileChooseListener;
import com.daemonw.fileui.core.UIUtil;
import com.daemonw.fileui.core.VolumeAdapter;
import com.daemonw.fileui.widget.adapter.MultiItemTypeAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class FileChooseDialogInternal extends Dialog implements MultiItemTypeAdapter.OnItemClickListener {
    private Activity mContext;
    private RecyclerView mVolumeList;
    private RecyclerView mFileList;
    private Button mConfirmButton;
    private Button mCancelButton;
    private RelativeLayout mBtnContainer;
    private OnFileChooseListener mOnFileSelectListener;
    private boolean isLoading = false;
    private VolumeAdapter mVolumeAdapter;
    private FileAdapterWrapper mFileAdapter;

    public FileChooseDialogInternal(Activity context) {
        super(context);
        mContext = context;
    }

    public FileChooseDialogInternal(Activity context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setOnFileSelectListener(OnFileChooseListener onFileSelectListener) {
        this.mOnFileSelectListener = onFileSelectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_choose_dialog);
        mVolumeList = findViewById(R.id.volume_list);
        mFileList = findViewById(R.id.file_list);
        mConfirmButton = findViewById(R.id.confirm);
        mCancelButton = findViewById(R.id.cancel);
        mBtnContainer = findViewById(R.id.btn_container);
        init();
    }

    private void init() {
        final List<Volume> volumes = StorageUtil.getVolumes(mContext);
        mVolumeAdapter = new VolumeAdapter(mContext, R.layout.volume_item, volumes);
        mVolumeAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ViewHolder holder, int position) {
                Volume v = volumes.get(position);
                mFileAdapter = getFileAdapter(v.mountType);
                if (mFileAdapter != null) {
                    mVolumeList.setVisibility(View.GONE);
                    mFileList.setLayoutManager(new LinearLayoutManager(mContext));
                    mFileList.setAdapter(mFileAdapter);
                    mFileList.setVisibility(View.VISIBLE);
                    mBtnContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                return false;
            }
        });
        mVolumeList.setLayoutManager(new LinearLayoutManager(mContext));
        mVolumeList.setAdapter(mVolumeAdapter);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnFileSelectListener != null) {
                    Set<Filer> selected = mFileAdapter.getSelected();
                    ArrayList<Filer> selectedFiles = new ArrayList<>(selected);
                    mOnFileSelectListener.onFileSelect(selectedFiles);
                }
            }
        });
    }

    private FileAdapterWrapper getFileAdapter(int mountType) {
        FileAdapterWrapper adapter = null;
        try {
            String rootPath = StorageUtil.getMountPath(mContext, mountType);
            adapter = new FileAdapterWrapper(mContext, R.layout.file_item, rootPath, mountType);
            adapter.setOnItemClickListener(this);
            adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
                @Override
                public void onHeaderClicked() {
                    updateToParent();
                }
            });
        } catch (PermException e) {
            PermissionUtil.requestPermission(mContext, ((PermException) e).getMountType());
        }
        return adapter;
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        Filer file = mFileAdapter.getItem(position);
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            updateToChild(file);
        } else {
            if (!mFileAdapter.isMultiSelect()) {
                Toast.makeText(mContext, R.string.tip_choose_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, ViewHolder holder, int position) {
        if (!mFileAdapter.isMultiSelect()) {
            mFileAdapter.setMultiSelect(true);
            mFileAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams wp = getWindow().getAttributes();
        if (wp == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        wp.width = (int) (dm.widthPixels * 0.9);
        getWindow().setAttributes(wp);
        ViewGroup.LayoutParams fp = mFileList.getLayoutParams();
        fp.height = (int) (dm.heightPixels * 0.6);
        mFileList.setLayoutParams(fp);
    }

    public void updateToParent() {
        if (isLoading) {
            return;
        }
        RxUtil.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        mFileAdapter.updateToParent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mFileAdapter.notifyDataSetChanged();
                        UIUtil.cancelLoading();
                        isLoading = false;
                    }
                }));
    }

    public void updateToChild(Filer file) {
        if (isLoading) {
            return;
        }
        RxUtil.add(Single.just(file)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Filer, Boolean>() {
                    @Override
                    public Boolean apply(Filer localFile) throws Exception {
                        mFileAdapter.updateToChild(localFile);
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        mFileAdapter.notifyDataSetChanged();
                        UIUtil.cancelLoading();
                        isLoading = false;
                    }
                }));
    }


    public void updateCurrent() {
        if (isLoading) {
            return;
        }
        RxUtil.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        mFileAdapter.updateCurrent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        mFileAdapter.notifyDataSetChanged();
                        UIUtil.cancelLoading();
                        isLoading = false;
                    }
                }));
    }
}
