package com.daemonw.file.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.MimeTypes;
import com.daemonw.file.core.utils.PermissionUtil;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.widget.MultiItemTypeAdapter;
import com.daemonw.widget.ViewHolder;

import java.io.File;
import java.util.List;

public class FileFragment extends Fragment implements MultiItemTypeAdapter.OnItemClickListener, View.OnKeyListener, FileLoader {

    protected Activity mContext;
    private RecyclerView mVolumeList;
    private RecyclerView mFileList;
    private ProgressBar mProgress;
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
        mVolumeList = view.findViewById(R.id.volume_list);
        mFileList = view.findViewById(R.id.file_list);
        mProgress = view.findViewById(R.id.progress_loading);
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
        if(isLoading){
            return;
        }
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
                onFileOpen(file);
            }
        }
        onFolderChanged();
    }


    @Override
    public boolean onItemLongClick(View view, ViewHolder holder, int position) {
        if(isLoading){
            return true;
        }
        if (!mFileAdapter.isMultiSelect()) {
            mFileAdapter.setMultiSelect(true);
            mFileAdapter.notifyDataSetChanged();
            return false;
        }
        return true;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
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
        return false;
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
        mVolumeAdapter = new VolumeAdapter(mContext, volumes);
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
        adapter = new FileAdapterWrapper(mContext, rootPath, mountType, null);
        adapter.setOnItemClickListener(this);
        adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
            @Override
            public void onHeaderClicked() {
                updateToParent();
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


    protected void onFileOpen(Filer file) {
        Intent intent = null;
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String provider = getActivity().getApplication().getPackageName() + ".FileProvider";
            String path = file.getPath();
            try {
                uri = FileProvider.getUriForFile(getActivity(), provider, new File(path));
            } catch (Exception e) {
                e.printStackTrace();
                uri = Uri.parse(file.getUri());
            }
        } else {
            uri = Uri.parse(file.getUri());
        }
        intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, MimeTypes.getMimeType(file.getName()));
        if (isIntentAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.unknow_file_type, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isIntentAvailable(Intent intent) {
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list == null) {
            return false;
        }
        return list.size() > 0;
    }

    public void updateToParent() {
        mFileAdapter.loadParent();
    }

    public void updateToChild(Filer file) {
        mFileAdapter.load(file);
    }


    public void updateCurrent() {
        mFileAdapter.reload();
    }


    protected void onFolderChanged() {

    }

    @Override
    public Filer[] load(Filer f) throws Exception {
        return f.listFiles();
    }

    @Override
    public void onLoad() {
        isLoading = true;
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinish() {
        isLoading = false;
        mProgress.setVisibility(View.GONE);
        mFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable err) {
        isLoading = false;
        mProgress.setVisibility(View.GONE);
        err.printStackTrace();
    }
}
