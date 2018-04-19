package com.grt.daemonw.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grt.daemonw.filelibrary.FileConst;
import com.grt.daemonw.filelibrary.file.Filer;
import com.grt.daemonw.filelibrary.file.HybirdFile;
import com.grt.daemonw.filelibrary.reflect.Volume;
import com.grt.daemonw.filelibrary.utils.StorageUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mVolumeList;
    private ListView mFileList;
    private Handler mHandler = new Handler();
    private static final int REQUEST_EXT_STORAGE_WRITE_PERM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVolumeList = findViewById(R.id.volume_list);
        mFileList = findViewById(R.id.file_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            StorageUtil.requestPermission(MainActivity.this, Volume.MOUNT_USB);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        List<Volume> volumeList = StorageUtil.getVolumes(this);
        List<String> volumes = new ArrayList<>();
        for (Volume v : volumeList) {
            volumes.add(v.mPath);
        }

        BaseAdapter volumeAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return volumeList.size();
            }

            @Override
            public Object getItem(int position) {
                return volumeList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = View.inflate(MainActivity.this, R.layout.list_item, null);
                TextView tv=v.findViewById(R.id.text1);
                tv.setText(volumeList.get(position).mDescription);
                return v;
            }
        };
        mVolumeList.setAdapter(volumeAdapter);
        mVolumeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Volume v = volumeList.get(position);
                String rootPath;
                if (!StorageUtil.hasWritePermission(MainActivity.this, v.mountType)) {
                    StorageUtil.requestPermission(MainActivity.this, v.mountType);
                    return;
                }
                if (v.mountType == Volume.MOUNT_EXTERNAL) {
                    rootPath = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(FileConst.PREF_EXTERNAL_URI, null);
                } else if (v.mountType == Volume.MOUNT_USB) {
                    rootPath = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(FileConst.PREF_USB_URI, null);
                } else {
                    rootPath = v.mPath;
                }
                HybirdFile file = new HybirdFile(MainActivity.this, rootPath);
                List<Filer> sub = file.listFiles();
                ArrayList<String> subFiles = new ArrayList<>();
                for (Filer f : sub) {
                    HybirdFile h = (HybirdFile) f;
                    subFiles.add(h.getName());
                }
                ListAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, subFiles);
                mFileList.setAdapter(adapter);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        StorageUtil.handlePermissionRequest(this, requestCode, resultCode, resultData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXT_STORAGE_WRITE_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
