package com.daemonw.fileui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daemonw.filelib.BuildConfig;
import com.daemonw.filelib.FileConst;
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
import com.daemonw.fileui.core.UIUtil;
import com.daemonw.fileui.widget.adapter.MultiItemTypeAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;

import java.io.File;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FileActivity1 extends AppCompatActivity implements MultiItemTypeAdapter.OnItemClickListener {
    private final static String LOG_TAG = FileActivity.class.getSimpleName();

    private RecyclerView mFileListView;
    private FileAdapterWrapper mFileAdapterWrapper;
    private int mountPoint = Volume.MOUNT_INTERNAL;
    private SharedPreferences sp;
    private boolean isLoading = false;
    private Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_activity);
        mContext = this;
        mFileListView = (RecyclerView) findViewById(R.id.file_list);
        mFileListView.setLayoutManager(new LinearLayoutManager(this));
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle bundle = getIntent().getExtras();
        mountPoint = bundle != null ? bundle.getInt("mount_point", Volume.MOUNT_UNKNOWN) : Volume.MOUNT_UNKNOWN;
        if (mountPoint == Volume.MOUNT_UNKNOWN) {
            mountPoint = sp.getInt("PREF_MOUNT_POINT", Volume.MOUNT_INTERNAL);
        }

        try {
            String rootPath = StorageUtil.getMountPath(this, mountPoint);
            if (rootPath != null) {
                mFileAdapterWrapper = initFileAdapter(rootPath, mountPoint);
                mFileListView.setAdapter(mFileAdapterWrapper);
            }
        } catch (PermException e) {
            int mountType = e.getMountType();
            PermissionUtil.requestPermission(this, mountType);
        }
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        Filer file = mFileAdapterWrapper.getItem(position);
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            updateToChild(file);
        } else {
            String path = file.getPath();
            String mime = MimeTypes.getMimeType(file.getName());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (file.getType() == Filer.TYPE_INTERNAL) {
                Uri uri;
                if (BuildUtils.thanNougat()) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", new File(path));

                } else {
                    uri = Uri.fromFile(new File(path));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                intent.setDataAndType(uri, mime);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, ViewHolder holder, int position) {
        if (!mFileAdapterWrapper.isMultiSelect()) {
            mFileAdapterWrapper.setMultiSelect(true);
            mFileAdapterWrapper.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mFileAdapterWrapper == null) {
            super.onBackPressed();
            return;
        }

        if (mFileAdapterWrapper.isMultiSelect()) {
            mFileAdapterWrapper.setMultiSelect(false);
            mFileAdapterWrapper.notifyDataSetChanged();
            return;
        }

        if (!mFileAdapterWrapper.isRoot()) {
            updateToParent();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }
            String rootPath = treeUri.toString();
            int mountPoint = Volume.MOUNT_INTERNAL;
            getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (requestCode == FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION) {
                sp.edit().putString(FileConst.PREF_EXTERNAL_URI, treeUri.toString()).apply();
                mountPoint = Volume.MOUNT_EXTERNAL;
            } else if (requestCode == FileConst.REQUEST_GRANT_USB_PERMISSION) {
                sp.edit().putString(FileConst.PREF_USB_URI, treeUri.toString()).apply();
                mountPoint = Volume.MOUNT_USB;
            }
            mFileAdapterWrapper = new FileAdapterWrapper(this, R.layout.file_item, rootPath, mountPoint);
            mFileAdapterWrapper.setOnItemClickListener(this);
            mFileListView.setAdapter(mFileAdapterWrapper);
        } else {
            Toast.makeText(this, R.string.warn_grant_perm, Toast.LENGTH_SHORT).show();
        }
    }

    protected Set<Filer> getSelected() {
        return mFileAdapterWrapper.getSelected();
    }

    protected Filer getCurrent() {
        return mFileAdapterWrapper.getCurrent();
    }

    protected void refresh() {
        updateCurrent();
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
                        mFileAdapterWrapper.updateToParent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mFileAdapterWrapper.notifyDataSetChanged();
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
                        mFileAdapterWrapper.updateToChild(localFile);
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        mFileAdapterWrapper.notifyDataSetChanged();
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
                        mFileAdapterWrapper.updateCurrent();
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        mFileAdapterWrapper.notifyDataSetChanged();
                        UIUtil.cancelLoading();
                        isLoading = false;
                    }
                }));
    }

    private FileAdapterWrapper initFileAdapter(String rootPath, int mountPoint) {
        FileAdapterWrapper adapter = new FileAdapterWrapper(this, R.layout.file_item, rootPath, mountPoint);
        adapter.setOnItemClickListener(this);
        adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
            @Override
            public void onHeaderClicked() {
                updateToParent();
            }
        });
        return adapter;
    }

    protected void switchVolume(int mountPoint) {
        try {
            String mountPath = StorageUtil.getMountPath(this, mountPoint);
            mFileAdapterWrapper = initFileAdapter(mountPath, mountPoint);
            mFileListView.setAdapter(mFileAdapterWrapper);
            this.mountPoint = mountPoint;
        } catch (PermException e) {
            int mountType = e.getMountType();
            PermissionUtil.requestPermission(this, mountType);
        }
    }
}
