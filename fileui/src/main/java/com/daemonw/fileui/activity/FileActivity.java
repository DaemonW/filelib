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
import com.daemonw.filelib.utils.StorageUtil;
import com.daemonw.fileui.core.FileAdapterWrapper;
import com.daemonw.fileui.widget.adapter.MultiItemTypeAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;
import com.example.fileui.R;

import java.io.File;
import java.util.Set;

public class FileActivity extends AppCompatActivity implements MultiItemTypeAdapter.OnItemClickListener {
    private final static String LOG_TAG = FileActivity.class.getSimpleName();

    private RecyclerView mFileListView;
    private FileAdapterWrapper mFileAdapterWrapper;
    private int mountPoint = Volume.MOUNT_INTERNAL;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_activity);
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
                mFileAdapterWrapper = new FileAdapterWrapper(this, R.layout.file_item, rootPath);
                mFileAdapterWrapper.setOnItemClickListener(this);
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
            mFileAdapterWrapper.updateToChild(file);
        } else {
            String path = file.getPath();
            String mime = MimeTypes.getMimeType(file.getName());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (BuildUtils.thanNougat()) {
                if (file.getFileType() == Filer.TYPE_RAW) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", new File(path));
                    intent.setDataAndType(contentUri, mime);
                }
            } else {
                Uri uri = file.getFileType() == Filer.TYPE_RAW ? Uri.fromFile(new File(path)) : Uri.parse(path);
                intent.setDataAndType(uri, mime);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
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
            mFileAdapterWrapper.updateToParent();
            return;
        }

        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }
            String rootPath = treeUri.toString();
            mFileAdapterWrapper = new FileAdapterWrapper(this, R.layout.file_item, rootPath);
            mFileAdapterWrapper.setOnItemClickListener(this);
            mFileListView.setAdapter(mFileAdapterWrapper);
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

    public Set<Filer> getSelected() {
        return mFileAdapterWrapper.getSelected();
    }

    public Filer getCurrent() {
        return mFileAdapterWrapper.getCurrent();
    }

    public void refresh() {
        mFileAdapterWrapper.updateCurrent();
    }

    public void switchVolume(int mountPoint) {
        try {
            String mountPath = StorageUtil.getMountPath(this, mountPoint);
            mFileAdapterWrapper = new FileAdapterWrapper(this, R.layout.file_item, mountPath);
            mFileListView.setAdapter(mFileAdapterWrapper);
        } catch (PermException e) {
            int mountType = e.getMountType();
            PermissionUtil.requestPermission(this, mountType);
        }
    }
}
