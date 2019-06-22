package com.daemonw.file.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.PermissionUtil;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.file.ui.R;
import com.daemonw.file.ui.adapter.FileAdapterWrapper;
import com.daemonw.file.ui.adapter.VolumeAdapter;
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

public class FileChooseActivity extends AppCompatActivity implements MultiItemTypeAdapter.OnItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.file_select_dialog);
        mVolumeList = (RecyclerView) findViewById(R.id.volume_list);
        mFileList = (RecyclerView) findViewById(R.id.file_list);
        mConfirmButton = (Button) findViewById(R.id.confirm);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mBtnContainer = (RelativeLayout) findViewById(R.id.btn_container);
        init();
        fixSize();
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
                finish();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        String rootPath = StorageUtil.getMountPath(mContext, mountType);
        if (rootPath == null) {
            return null;
        }
        if (!StorageUtil.hasWritePermission(mContext, mountType)) {
            PermissionUtil.requestPermission(mContext, mountType);
            return null;
        }
        adapter = new FileAdapterWrapper(mContext, R.layout.file_item, rootPath, mountType, false);
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

    public void fixSize() {
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
        final PopupWindow loading = UIUtil.getPopupLoading(mContext);
        RxUtil.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext, loading);
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
                        UIUtil.cancelLoading(loading);
                        isLoading = false;
                    }
                }));
    }

    public void updateToChild(Filer file) {
        if (isLoading) {
            return;
        }
        final PopupWindow loading = UIUtil.getPopupLoading(mContext);
        RxUtil.add(Single.just(file)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isLoading = true;
                        UIUtil.showLoading(mContext, loading);
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
                        UIUtil.cancelLoading(loading);
                        isLoading = false;
                    }
                }));
    }


    public void updateCurrent() {
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
                        mFileAdapter.updateCurrent();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }
            getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (requestCode == FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION) {
                sp.edit().putString(FileConst.PREF_EXTERNAL_URI, treeUri.toString()).apply();
            } else if (requestCode == FileConst.REQUEST_GRANT_USB_PERMISSION) {
                sp.edit().putString(FileConst.PREF_USB_URI, treeUri.toString()).apply();
            }
        } else {
            Toast.makeText(this, R.string.warn_grant_perm, Toast.LENGTH_SHORT).show();
        }
    }

    interface OnFileChooseListener {
        void onFileSelect(List<Filer> selected);
    }
}
