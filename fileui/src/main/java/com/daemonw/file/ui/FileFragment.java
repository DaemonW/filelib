package com.daemonw.file.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.PermissionUtil;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.file.ui.util.RxUtil;
import com.daemonw.file.ui.util.UIUtil;
import com.daemonw.widget.MultiItemTypeAdapter;
import com.daemonw.widget.ViewHolder;

import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FileFragment extends Fragment implements MultiItemTypeAdapter.OnItemClickListener, View.OnKeyListener {

    protected Activity mContext;
    private RecyclerView mVolumeList;
    private RecyclerView mFileList;
    private boolean isLoading = false;
    private boolean isVisible = false;
    protected boolean isShowVolume;
    private VolumeAdapter mVolumeAdapter;
    private FileAdapterWrapper mFileAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.file_fragment, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        isShowVolume = true;
        mVolumeList = (RecyclerView) view.findViewById(R.id.volume_list);
        mFileList = (RecyclerView) view.findViewById(R.id.file_list);
        mFileList.setOnKeyListener(this);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }

            getActivity().getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (requestCode == FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION) {
                sp.edit().putString(FileConst.PREF_EXTERNAL_URI, treeUri.toString()).apply();
            } else if (requestCode == FileConst.REQUEST_GRANT_USB_PERMISSION) {
                sp.edit().putString(FileConst.PREF_USB_URI, treeUri.toString()).apply();
            }
        } else {
            Toast.makeText(mContext, R.string.warn_grant_perm, Toast.LENGTH_SHORT).show();
        }
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
        onFolderChanged();
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean handled = false;
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!isVisible) {
                return false;
            }
            if (isLoading) {
                return true;
            }
            if (mFileAdapter != null) {

                if (mFileAdapter.isMultiSelect()) {
                    mFileAdapter.setMultiSelect(false);
                    mFileAdapter.notifyDataSetChanged();
                    return true;
                }

                if (!mFileAdapter.isRoot()) {
                    updateToParent();
                    onFolderChanged();
                    return true;
                }

                if (isShowVolume) {
                    return false;
                } else {
                    showVolumeList();
                    onFolderChanged();
                }

                return true;
            }
        }
        return handled;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible() {
        isVisible = true;
        mVolumeAdapter.refresh();
    }

    protected void onInvisible() {
        isVisible = false;
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
                    isShowVolume = false;
                    mVolumeList.setVisibility(View.GONE);
                    mFileList.setLayoutManager(new LinearLayoutManager(mContext));
                    mFileList.setAdapter(mFileAdapter);
                    mFileList.setVisibility(View.VISIBLE);
                }
                onFolderChanged();
            }

            @Override
            public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                return false;
            }
        });
        mVolumeList.setLayoutManager(new LinearLayoutManager(mContext));
        mVolumeList.setAdapter(mVolumeAdapter);
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
        adapter = new FileAdapterWrapper(mContext, R.layout.file_item, rootPath, mountType, true);
        adapter.setOnItemClickListener(this);
        adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
            @Override
            public void onHeaderClicked() {
                updateToParent();
                onFolderChanged();
            }
        });
        return adapter;
    }


    public void showVolumeList() {
        isShowVolume = true;
        mFileList.setVisibility(View.GONE);
        mVolumeAdapter.refresh();
        mVolumeList.setVisibility(View.VISIBLE);
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


    protected void onFolderChanged() {

    }

    protected Set<Filer> getSelected() {
        return mFileAdapter.getSelected();
    }

    protected Filer getCurrent() {
        return mFileAdapter.getCurrent();
    }

    protected void disableSelectMode() {
        mFileAdapter.setMultiSelect(false);
    }

    protected void refresh() {
        updateCurrent();
    }
}
