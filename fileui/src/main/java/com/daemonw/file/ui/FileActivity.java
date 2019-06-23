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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import java.util.Set;

public class FileActivity extends AppCompatActivity implements MultiItemTypeAdapter.OnItemClickListener, FileLoader {
    private final static String LOG_TAG = FileActivity.class.getSimpleName();

    private Activity mContext;
    private RecyclerView mVolumeList;
    private RecyclerView mFileList;
    private ProgressBar mProgress;
    private boolean isShowVolume;
    private boolean isLoading;
    private VolumeAdapter mVolumeAdapter;
    private FileAdapter mFileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        isShowVolume = true;
        setContentView(R.layout.file_activity);
        mVolumeList = findViewById(R.id.volume_list);
        mFileList = findViewById(R.id.file_list);
        mProgress = findViewById(R.id.progress_loading);
        init();
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
            }

            @Override
            public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                return false;
            }
        });
        mVolumeList.setLayoutManager(new LinearLayoutManager(mContext));
        mVolumeList.setAdapter(mVolumeAdapter);
    }

    private FileAdapter getFileAdapter(int mountType) {
        FileAdapter adapter = null;
        String rootPath = StorageUtil.getMountPath(mContext, mountType);
        if (rootPath == null) {
            return null;
        }
        if (!StorageUtil.hasWritePermission(mContext, mountType)) {
            PermissionUtil.requestPermission(mContext, mountType);
            return null;
        }
        adapter = new FileAdapter(mContext, rootPath, mountType, this);
        adapter.setOnItemClickListener(this);
//        adapter.setOnHeadClickListener(new FileAdapterWrapper.OnHeadClickListener() {
//            @Override
//            public void onHeaderClicked() {
//                updateToParent();
//            }
//        });
        return adapter;
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
    public void onBackPressed() {
        if (mFileAdapter != null) {
            if(isLoading){
                return;
            }
            if (mFileAdapter.isMultiSelect()) {
                mFileAdapter.setMultiSelect(false);
                mFileAdapter.notifyDataSetChanged();
                return;
            }
            if (!mFileAdapter.isRoot()) {
                updateToParent();
                return;
            }
            if (isShowVolume) {
                super.onBackPressed();
            } else {
                showVolumeList();
            }
            return;
        }
        super.onBackPressed();
    }

    protected void onFileOpen(Filer file) {
        Intent intent = null;
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String provider = getApplication().getPackageName() + ".FileProvider";
            String path = file.getPath();
            try {
                uri = FileProvider.getUriForFile(this, provider, new File(path));
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
            Toast.makeText(this, R.string.unknow_file_type, Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isIntentAvailable(Intent intent) {
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list == null) {
            return false;
        }
        return list.size() > 0;
    }

    public void showVolumeList() {
        isShowVolume = true;
        mFileList.setVisibility(View.GONE);
        mVolumeList.setVisibility(View.VISIBLE);
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

    protected void switchVolume(int mountPoint) {
        mFileAdapter = getFileAdapter(mountPoint);
        if (mFileAdapter != null) {
            mFileList.setAdapter(mFileAdapter);
        }
    }

    protected Set<Filer> getSelected() {
        return mFileAdapter.getSelected();
    }

    protected Filer getCurrent() {
        return mFileAdapter.getCurrent();
    }

    protected void refresh() {
        mFileAdapter.setMultiSelect(false);
        updateCurrent();
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
    }

    @Override
    public void onError(Throwable err) {
        isLoading = false;
        mProgress.setVisibility(View.GONE);
        err.printStackTrace();
    }
}
