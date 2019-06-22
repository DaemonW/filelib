package com.daemonw.file.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.file.ui.util.RxUtil;
import com.daemonw.file.ui.util.UIUtil;
import com.daemonw.widget.MultiItemTypeAdapter;
import com.daemonw.widget.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


class FileChooseDialogInternal extends Dialog implements MultiItemTypeAdapter.OnItemClickListener {
    private Activity mContext;
    private RecyclerView mVolumeList;
    private RecyclerView mFileList;
    private Button mConfirmButton;
    private Button mCancelButton;
    private RelativeLayout mBtnContainer;
    private TextView mDlgTitle;
    private OnFileChooseListener mOnFileSelectListener;
    private boolean isLoading = false;
    private VolumeAdapter mVolumeAdapter;
    private FileAdapterWrapper mFileAdapter;
    private boolean showFile;

    public FileChooseDialogInternal(Activity context) {
        super(context);
        mContext = context;
        showFile = true;
    }

    public FileChooseDialogInternal(Activity context, boolean showFile) {
        super(context);
        mContext = context;
        this.showFile = showFile;
    }

    public FileChooseDialogInternal(Activity context, int theme) {
        super(context, theme);
        mContext = context;
        showFile = true;
    }

    public void setOnFileSelectListener(OnFileChooseListener onFileSelectListener) {
        this.mOnFileSelectListener = onFileSelectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_select_dialog);
        mVolumeList = findViewById(R.id.volume_list);
        mFileList = findViewById(R.id.file_list);
        mConfirmButton = findViewById(R.id.confirm);
        mCancelButton = findViewById(R.id.cancel);
        mBtnContainer = findViewById(R.id.btn_container);
        mDlgTitle = findViewById(R.id.dlg_title);
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
                    mOnFileSelectListener.onChooseFile(selectedFiles);
                }
            }
        });
        if (showFile) {
            mDlgTitle.setText(R.string.choose_file);
        } else {
            mDlgTitle.setText(R.string.choose_folder);
        }
    }

    private FileAdapterWrapper getFileAdapter(int mountType) {
        FileAdapterWrapper adapter = null;
        String rootPath = StorageUtil.getMountPath(mContext, mountType);
        if (rootPath == null) {
            return null;
        }
        if (!StorageUtil.hasWritePermission(mContext, mountType)) {
            //PermissionUtil.requestPermission(mContext, ((PermException) e).getMountType());
            //Toast.makeText(mContext, R.string.warm_no_permission, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, GrantPermissionActivity.class);
            intent.putExtra("mount_point", mountType);
            mContext.startActivity(intent);
            return null;
        }
        adapter = new FileAdapterWrapper(mContext, R.layout.file_item, rootPath, mountType, showFile);
        adapter.setOnItemClickListener(this);
        adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
            @Override
            public void onHeaderClicked() {
                updateToParent();
            }
        });
        return adapter;
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        Filer file = mFileAdapter.getItem(position);
        if (file == null) {
            return;
        }
        if (mFileAdapter.isMultiSelect()) {
            CheckBox checkBox = holder.getView(R.id.file_check);
            checkBox.setChecked(!checkBox.isChecked());
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
            return false;
        }
        return true;
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
        showLoadingWhenOperate(new FileOperator() {
            @Override
            public void operate() {
                mFileAdapter.updateToParent();
            }
        });
    }

    public void updateToChild(final Filer file) {
        showLoadingWhenOperate(new FileOperator() {
            @Override
            public void operate() {
                mFileAdapter.updateToChild(file);
            }
        });
    }


    public void updateCurrent() {
        showLoadingWhenOperate(new FileOperator() {
            @Override
            public void operate() {
                mFileAdapter.updateCurrent();
            }
        });
    }


    private void showLoadingWhenOperate(final FileOperator operator) {
        if (isLoading) {
            return;
        }
        final PopupWindow loading = UIUtil.getPopupLoading(mContext);
        RxUtil.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext, loading);
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        operator.operate();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        mFileAdapter.notifyDataSetChanged();
                        UIUtil.cancelLoading(loading);
                        isLoading = false;
                    }
                }));
    }
}
